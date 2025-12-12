/**
 * File: MultiplayerBattleState.java
 * Purpose:
 *      Manages an online multiplayer battle between two players.
 */

package game;

import action.Action;
import battle.BattleManager;
import battle.BattleState;
import controller.BattleController;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import models.Goober;
import models.Trainer;
import networking.NetworkManager;
import view.BattlePage;

/**
 * A game state that manages a multiplayer battle.
 * <p>
 * This state coordinates battle logic, networking communication,
 * and UI updates for an online match. Each client sends its actions
 * to the opponent and resolves battle turns once both actions are received.
 * </p>
 */
public class MultiplayerBattleState implements GameState {

    private GameManager gm;
    private NetworkManager net;
    private BattleManager bm;
    private BattlePage view;

    private Trainer player;
    private Trainer opponent;

    /** Indicates whether the client is waiting for opponent information. */
    private boolean waitingForOpponent = false;

    /** The action selected by the local player. */
    private Action myAction = null;

    /** The action received from the opponent. */
    private Action opponentAction = null;

    /**
     * Constructs a MultiplayerBattleState.
     * <p>
     * Sends the local player's {@link Trainer} data to the opponent
     * upon initialization.
     * </p>
     *
     * @param gm the main game manager
     * @param net the networking manager handling communication
     * @param initialData optional initial data received from the network
     */
    public MultiplayerBattleState(GameManager gm, NetworkManager net, Object initialData) {
    	System.out.println("[MULTI] MultiplayerBattleState created. Data: " + initialData);
        this.gm = gm;
        this.net = net;
        this.player = gm.getSession().getPlayerTrainer();

        if (initialData instanceof Trainer) {
            this.opponent = (Trainer) initialData;
        }

        // Send this player's trainer to the opponent
        net.send(this.player);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Initializes the multiplayer battle once both trainers
     * are available, creating the {@link BattleManager} and
     * displaying the {@link BattlePage}.
     * </p>
     *
     * @param session the active user session
     */
    @Override
    public void enter(UserSession session) {
        // however you already set player/opponent for multiplayer:
    	// assume player and opponent are non-null here
    	if (player == null || opponent == null) { return; }    
    	
    	// Reset abilities and team status like single-player
    	player.setAbilityUsed(false);
    	opponent.setAbilityUsed(false);
    	
    	for (Goober g : player.getTeam()) {
    		g.setHealth(g.getMaxHp());
    		g.getState().unStun();
    		g.getState().getEffects().clear();
    	}
    	
    	for (Goober g : opponent.getTeam()) {
    		g.setHealth(g.getMaxHp());
    		g.getState().unStun();
    		g.getState().getEffects().clear();
    	}
    	
    	// Create battle manager and controller (use whatever levelIdx you want;    
    	bm = new BattleManager(player, opponent);
    	BattleController controller = new BattleController(gm, bm, 67);  // <<< key change    
    	
    	// Build and display the battle UI
    	if (gm.getStage() != null) {
    		Stage stage = gm.getStage();
    		view = new BattlePage(stage, controller, 0);
    		controller.setView(view); // like GameBattleState 
    		
    		// If you already have a Scene on the stage:
    		if (stage.getScene() == null) {
    			stage.setScene(new Scene(view, 1140, 640));
    		} else {
    			stage.getScene().setRoot(view);
    		}
    	}
    }

    /**
     * Handles data received from the network.
     * <p>
     * Supported data:
     * <ul>
     *   <li>{@link Trainer} — initializes the opponent if not yet set</li>
     *   <li>{@link Action} — records the opponent's selected action</li>
     * </ul>
     * </p>
     *
     * @param data the received network object
     */
    public void onNetworkDataReceived(Object data) {
        if (data instanceof Trainer && this.opponent == null) {
            this.opponent = (Trainer) data;

            // Re-enter state once opponent is known
            Platform.runLater(() -> enter(gm.getSession()));
        }
        else if (data instanceof Action) {
            this.opponentAction = (Action) data;
        }
    }

    /**
     * Displays the battle result and transitions back to the main menu.
     */
    private void handleBattleEnd() {
        BattleState.BattlePhase phase = bm.getState().getPhase();
        String message = (phase == BattleState.BattlePhase.PLAYER_WIN)
                ? "You Won!"
                : "You Lost!";

        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Battle Over");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();

        // Return to main menu
        gm.setState(new MenuState(gm));
    }
}
