/**
 * File: DifficultySelectState.java
 * Purpose:
 *      Represents the game state in which the player selects
 *      the AI difficulty before starting a new game.
 */

package game;

import ai.Difficulty;
import javafx.scene.Scene;
import view.DifficultySelectView;

/**
 * A game state that displays the difficulty selection screen.
 * <p>
 * This state allows the player to choose an {@link Difficulty}
 * level, which is then passed forward into the game setup
 * process.
 * </p>
 */
public class DifficultySelectState implements GameState {

    private GameManager gm;
    private int saveSlot;

    /**
     * Constructs a DifficultySelectState.
     *
     * @param gm the main game manager
     * @param saveSlot the save slot being used
     */
    public DifficultySelectState(GameManager gm, int saveSlot) {
        this.gm = gm;
        this.saveSlot = saveSlot;
    }

    /**
     * Enters the difficulty selection state.
     * <p>
     * Initializes and displays the {@link DifficultySelectView}
     * on the main stage.
     * </p>
     *
     * @param session the current user session
     */
    @Override
    public void enter(UserSession session) {
        if (gm.getStage() != null) {
            DifficultySelectView view = new DifficultySelectView(gm.getStage(), this);
            Scene scene = new Scene(view, 1140, 640);
            gm.getStage().setScene(scene);
        }
    }

    /**
     * Handles the selection of a difficulty level.
     * Transitions the game to the trainer selection state,
     * passing along the chosen difficulty.
     *
     * @param difficulty the selected difficulty
     */
    public void selectDifficulty(Difficulty difficulty) {
        gm.setState(new TrainerSelectState(gm, saveSlot, difficulty));
    }
}
