/**
 * File: MultiplayerBattleState.java
 * Purpose:
 *      Manages an online multiplayer battle between two human players.
 *
 *      This state is the glue layer between:
 *          - Core battle logic ({@link battle.BattleManager})
 *          - Network plumbing ({@link networking.NetworkManager})
 *          - The JavaFX battle UI ({@link view.BattlePage})
 *
 *      One side is the "host" (the one running the server socket) and is
 *      responsible for actually resolving turns. The other side is the
 *      "client" and just sends its chosen actions, then waits for an update
 *      packet from the host.
 *
 *      TL;DR:
 *          - Each machine sees *its* trainer as "player" (bottom of the screen)
 *          - The other trainer is "opponent" (top of the screen)
 *          - The host is the only one who calls resolveTurn(...)
 */

package game;

import java.util.ArrayList;
import java.util.List;

import action.Action;
import action.AttackAction;
import action.ItemAction;
import action.SwitchAction;
import battle.BattleManager;
import battle.BattleState;
import battle.BattleTurnResult;
import controller.BattleController;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import models.Goober;
import models.Trainer;
import networking.BattleUpdatePacket;
import networking.NetworkManager;
import networking.TurnPacket;
import view.BattlePage;

public class MultiplayerBattleState implements GameState {

    /** Global game brain – used for swapping states and grabbing the Stage. */
    private final GameManager gm;

    /** Network layer for sending / receiving packets. */
    private final NetworkManager net;

    /** True on the machine that is hosting (running the ServerSocket). */
    private final boolean isHost;

    /**
     * The trainer that belongs to *this* machine.
     * This is who the local player is controlling and who appears on the bottom.
     */
    private Trainer localTrainer;

    /**
     * The trainer belonging to the other machine.
     * This is who appears on the top of the screen.
     */
    private Trainer remoteTrainer;

    /** Core battle logic – exactly the same engine used for single-player. */
    private BattleManager battleManager;

    /** Battle UI view. Pure view layer; no brains in here (ideally). */
    private BattlePage view;

    /** Action the local player has chosen for the current turn (if any). */
    private Action localPendingAction;

    /** Action the remote player has sent for the current turn (host only cares). */
    private Action remotePendingAction;

    /**
     * Builds a new multiplayer battle state.
     *
     * @param gm            main {@link GameManager} instance
     * @param net           active {@link NetworkManager} connection
     * @param localTrainer  trainer controlled by this player (bottom of screen)
     * @param remoteTrainer trainer controlled by the other player (top of screen)
     * @param isHost        true if this side is the host / turn resolver
     */
    public MultiplayerBattleState(GameManager gm,
                                  NetworkManager net,
                                  Trainer localTrainer,
                                  Trainer remoteTrainer,
                                  boolean isHost) {

        this.gm = gm;
        this.net = net;
        this.isHost = isHost;

        this.localTrainer = localTrainer;
        this.remoteTrainer = remoteTrainer;
    }

    /**
     * Called when this state becomes active.
     *
     * This is where we:
     *  - Reset trainer/Goober status so nobody drags in a half-dead team
     *  - Spin up a {@link BattleManager} using (localTrainer, remoteTrainer)
     *  - Build and show the {@link BattlePage} UI
     */
    @Override
    public void enter(UserSession session) {
        if (localTrainer == null || remoteTrainer == null) {
            // If we ever hit this, networking exploded earlier.
            System.err.println("[MULTI] enter() called with null trainers – aborting.");
            return;
        }

        // Reset ability flags and clean up both teams before the fight.
        resetTrainerForBattle(localTrainer);
        resetTrainerForBattle(remoteTrainer);

        // Local player is always "player" in the BattleManager/Controller sense.
        battleManager = new BattleManager(localTrainer, remoteTrainer);
        BattleController controller = new BattleController(gm, battleManager, /* levelIdx */ 67);
        controller.setView(null); // real view set right after construction

        Stage stage = gm.getStage();
        if (stage != null) {
            view = new BattlePage(stage, controller, /* levelIdx for background */ 0);
            controller.setView(view);

            if (stage.getScene() == null) {
                stage.setScene(new Scene(view, 1140, 640));
            } else {
                stage.getScene().setRoot(view);
            }
        }
    }

    /**
     * Light cleanup of a trainer before starting a battle:
     * reset ability flags, restore HP, clear status effects, etc.
     */
    private void resetTrainerForBattle(Trainer t) {
        if (t == null) return;

        t.setAbilityUsed(false);
        for (Goober g : t.getTeam()) {
            g.setHealth(g.getMaxHp());
            g.getState().unStun();
            g.getState().getEffects().clear();
        }
    }

    // -------------------------------------------------------------------------
    // Network entry points
    // -------------------------------------------------------------------------

    /**
     * Called by the networking layer whenever *anything* arrives over the wire.
     * We only care about:
     *
     *  - {@link TurnPacket}        – the other player chose an action
     *  - {@link BattleUpdatePacket} – host finished a turn and is syncing state
     *
     * @param obj deserialized packet from the other side
     */
    public void onNetworkDataReceived(Object obj) {
        if (obj instanceof TurnPacket) {
            // Remote side has chosen their action for this turn.
            TurnPacket pkt = (TurnPacket) obj;

            // From our perspective, the remote player is always "opponent".
            Trainer actingTrainer = battleManager.getState().getOpponent();
            Action remoteAction = decodeAction(pkt, actingTrainer);
            remotePendingAction = remoteAction;

            // Only the host actually resolves the turn once both actions exist.
            if (isHost) {
                maybeResolveTurn();
            }

        } else if (obj instanceof BattleUpdatePacket) {
            // Client received the resolved state from host.
            applyBattleUpdate((BattleUpdatePacket) obj);
        }
    }

    /**
     * Called by the UI when *this* player picks an action (attack, switch, item, etc.).
     * We:
     *  - stash the local action
     *  - encode it into a {@link TurnPacket}
     *  - fire it over the network
     *  - if we are host, attempt to resolve the turn when both actions are present
     *
     * @param action the action selected by the local player
     */
    public void onLocalAction(Action action) {
        if (action == null) return;

        this.localPendingAction = action;

        // Ship a compact packet instead of the whole Action graph.
        TurnPacket packet = encodeAction(action);
        net.send(packet);

        if (isHost) {
            maybeResolveTurn();
        }
    }

    // -------------------------------------------------------------------------
    // Turn resolution (host-side only)
    // -------------------------------------------------------------------------

    /**
     * Host-only helper. Once both local and remote actions exist, the host:
     *  - calls {@link BattleManager#resolveTurn(Action, Action)}
     *  - updates its own UI
     *  - packages the updated trainers + logs into a {@link BattleUpdatePacket}
     *  - sends that packet to the client so they can mirror the new state
     */
    private void maybeResolveTurn() {
        if (!isHost) {
            // Client should never try to resolve; just chill and wait.
            return;
        }

        if (localPendingAction == null || remotePendingAction == null) {
            // Still waiting on somebody to lock in a move.
            return;
        }

        // Resolve one full turn of chaos.
        BattleTurnResult result = battleManager.resolveTurn(localPendingAction, remotePendingAction);

        // Clear the queued actions for the next turn.
        localPendingAction = null;
        remotePendingAction = null;

        // TODO: once BattlePage has a dedicated "apply result" hook, we can pass
        //       the full BattleTurnResult in. For now we at least tell it to
        //       refresh from the model.
        if (view != null) {
            Platform.runLater(() -> view.updateStats());
        }

        // Host prepares an update packet. From the host's point of view,
        //   - state.getPlayer()  == host trainer
        //   - state.getOpponent() == client trainer
        Trainer hostViewPlayer   = battleManager.getState().getPlayer();
        Trainer hostViewOpponent = battleManager.getState().getOpponent();

        // Copy logs so we don't leak the internal list reference.
        List<String> logsCopy = new ArrayList<>(battleManager.getLogs());

        BattleUpdatePacket update = new BattleUpdatePacket(hostViewPlayer, hostViewOpponent, logsCopy);
        net.send(update);

        // If the battle is over, both sides should eventually bail out.
        if (battleManager.getState().isFinished()) {
            Platform.runLater(this::handleBattleEnd);
        }
    }

    // -------------------------------------------------------------------------
    // Applying host updates (client-side mirror)
    // -------------------------------------------------------------------------

    /**
     * Applies an update sent by the host after resolving a turn.
     *
     * Important ordering note:
     *  - On the host, {@code pkt.getPlayer()} = host, {@code pkt.getOpponent()} = client.
     *  - On the client, *our* local trainer is the client, which should be the
     *    bottom "player" in our UI. So we deliberately invert the assignment.
     *
     * @param pkt battle state snapshot from the host
     */
    private void applyBattleUpdate(BattleUpdatePacket pkt) {
        if (battleManager == null || pkt == null) return;

        BattleState state = battleManager.getState();

        if (isHost) {
            // In theory the host never receives its own update, but if that
            // somehow happens we keep the same mapping the host used.
            state.setPlayer(pkt.getPlayer());
            state.setOpponent(pkt.getOpponent());
        } else {
            // We are the client. From the host's perspective:
            //   pkt.player   == host trainer
            //   pkt.opponent == client trainer
            // On the client, "player" should be *us*, so we flip them.
            state.setPlayer(pkt.getOpponent());
            state.setOpponent(pkt.getPlayer());
        }

        // Mirror logs without needing a setLogs(...) API.
        List<String> logs = battleManager.getLogs();
        logs.clear();
        if (pkt.getLogs() != null) {
            logs.addAll(pkt.getLogs());
        }

        // Refresh the UI so HP bars, sprites, etc. line up with the new state.
        if (view != null) {
            Platform.runLater(() -> view.updateStats());
        }

        if (state.isFinished()) {
            Platform.runLater(this::handleBattleEnd);
        }
    }

    // -------------------------------------------------------------------------
    // Helpers: encoding / decoding actions into TurnPackets
    // -------------------------------------------------------------------------

    /**
     * Squashes a full {@link Action} down into a small, serializable
     * {@link TurnPacket} that can be shipped over the socket.
     */
    private TurnPacket encodeAction(Action action) {
        if (action instanceof AttackAction) {
            return new TurnPacket(TurnPacket.Type.ATTACK, ((AttackAction) action).getMoveIndex(), null);
        } else if (action instanceof SwitchAction) {
            return new TurnPacket(
                    TurnPacket.Type.SWITCH,
                    ((SwitchAction) action).getNewIndex(),
                    null
            );
        } else if (action instanceof ItemAction) {
            return new TurnPacket(
                    TurnPacket.Type.ITEM,
                    -1,
                    ((ItemAction) action).getItemName()
            );
        } else {
            // RUN or any weird fallback.
            return new TurnPacket(TurnPacket.Type.RUN, -1, null);
        }
    }

    /**
     * Re-inflates a {@link TurnPacket} back into a concrete {@link Action}
     * tied to the correct acting trainer.
     *
     * @param pkt           incoming packet
     * @param actingTrainer the trainer who is performing this action
     * @return reconstructed Action, or null for a RUN / no-op
     */
    private Action decodeAction(TurnPacket pkt, Trainer actingTrainer) {
        if (pkt == null || actingTrainer == null) return null;

        switch (pkt.getType()) {
            case ATTACK:
                return new AttackAction(actingTrainer, pkt.getIndex());
            case SWITCH:
                return new SwitchAction(actingTrainer, pkt.getIndex());
            case ITEM:
                return new ItemAction(actingTrainer, actingTrainer, pkt.getItemName());
            case RUN:
            default:
                return null;
        }
    }

    // -------------------------------------------------------------------------
    // Ending the match
    // -------------------------------------------------------------------------

    /**
     * Clean end-of-battle handler.
     *
     * Right now this just throws up an Alert and punts back to the main menu.
     * If you want post-game stats or a fancy "you both suck" screen, this is
     * where you'd branch out into another GameState.
     */
    private void handleBattleEnd() {
        BattleState.BattlePhase phase = battleManager.getState().getPhase();
        String message = (phase == BattleState.BattlePhase.PLAYER_WIN) ? "You Won!" : "You Lost!";

        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Battle Over");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();

        gm.setState(new MenuState(gm));
    }
}