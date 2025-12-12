/**
 * File: GameBattleState.java
 * Purpose:
 *      Manages the active gameplay state during a battle.
 *      Acts as the bridge between the game state machine,
 *      battle logic, and the battle UI.
 */

package game;

import action.Action;
import action.SwitchAction;
import ai.Difficulty;
import ai.MiniMaxAgent;
import battle.BattleManager;
import battle.BattleTurnResult;
import controller.BattleController;
import javafx.scene.Scene;
import javafx.stage.Stage;
import models.Goober;
import models.Trainer;
import view.AudioManager;
import view.BattlePage;

/**
 * Represents the game state active during a battle.
 * <p>
 * This class coordinates the {@link GameManager} state machine,
 * the combat logic handled by {@link BattleManager}, and the
 * battle UI represented by {@link BattlePage}.
 * </p>
 * <p>
 * It manages turn flow, AI decision-making, audio transitions,
 * and post-battle progression.
 * </p>
 */
public class GameBattleState implements GameState {

    private GameManager gm;
    private BattleManager bm;
    private BattlePage view;
    private UserSession session;
    private BattleController controller;

    private Trainer player;
    private Trainer opponent;

    private MiniMaxAgent ai;

    /** 1-based level index (1..7). */
    private int levelIdx;

    /**
     * Constructs a new GameBattleState.
     * <p>
     * Initializes the AI agent based on the session's difficulty.
     * A difficulty spike is applied if {@code levelIdx} corresponds
     * to the final boss level.
     * </p>
     *
     * @param gm the main game manager
     * @param levelIdx the current level index (1-based)
     */
    public GameBattleState(GameManager gm, int levelIdx) {
        this.gm = gm;
        this.levelIdx = levelIdx;

        Difficulty diff = gm.getSession().getDifficulty();

        // Difficulty spike for the final boss (Level 7)
        if (levelIdx == 7) {
            ai = new MiniMaxAgent(diff.getNext());
        } else {
            ai = new MiniMaxAgent(diff);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Initializes all battle components:
     * <ul>
     *   <li>Retrieves trainers from the {@link UserSession}</li>
     *   <li>Resets team health and status effects</li>
     *   <li>Creates the {@link BattleManager} and {@link BattleController}</li>
     *   <li>Initializes the {@link BattlePage} UI</li>
     * </ul>
     * </p>
     *
     * @param session the active user session
     */
    @Override
    public void enter(UserSession session) {
        this.session = (session != null) ? session : gm.getSession();
        if (this.session == null) return;

        // Fetch trainers
        player = this.session.getPlayerTrainer();
        Level lvl = this.session.getLevel(levelIdx);
        if (lvl == null) return;

        opponent = lvl.getOpponent();
        if (player == null || opponent == null) return;

        player.setAbilityUsed(false);
        opponent.setAbilityUsed(false);

        // Reset teams before battle
        for (Goober g : player.getTeam()) {
            g.setHealth(g.getMaxHp());
            g.getState().unStun();
            g.clearEffects();
        }

        for (Goober g : opponent.getTeam()) {
            g.setHealth(g.getMaxHp());
            g.getState().unStun();
            g.clearEffects();
        }

        // Create battle manager and controller
        bm = new BattleManager(player, opponent);
        controller = new BattleController(gm, bm, levelIdx);

        // Set battle music
        AudioManager.getInstance().setPlaylist(opponent.getRole());

        // Build and display battle UI
        Stage stage = gm.getStage();
        view = new BattlePage(stage, controller, levelIdx);
        controller.setView(view);

        Scene scene = new Scene(view, 1140, 640);
        stage.setScene(scene);
    }

    /**
     * Generates an action for the AI-controlled opponent.
     * <p>
     * Uses {@link MiniMaxAgent} to determine the optimal move.
     * Automatically performs a forced switch if the active
     * Goober has fainted.
     * </p>
     *
     * @return the {@link Action} the opponent will perform
     */
    public Action generateAIAction() {
        if (opponent == null || opponent.getActiveGoober() == null) {
            return null;
        }

        if (opponent.getActiveGoober().isFainted()) {
            for (int i = 0; i < opponent.getTeam().size(); i++) {
                if (!opponent.getTeam().get(i).isFainted()) {
                    return new SwitchAction(opponent, i);
                }
            }
        }

        return ai.getBestAction(opponent, player);
    }
}
