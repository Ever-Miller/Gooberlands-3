/**
 * File: EndGameState.java
 * Purpose:
 *      Represents the final game state shown after the game ends.
 *      Displays the end-game screen and provides a transition
 *      back to the main menu.
 */

package game;

import javafx.scene.Scene;
import view.EndGameView;

/**
 * A game state that displays the end-game screen.
 * <p>
 * This state is entered once the game concludes and allows
 * the player to return to the main menu.
 * </p>
 */
public class EndGameState implements GameState {

    private GameManager gm;

    /**
     * Constructs an EndGameState.
     *
     * @param gm the main game manager
     */
    public EndGameState(GameManager gm) {
        this.gm = gm;
    }

    /**
     * Enters the end-game state.
     * <p>
     * Initializes and displays the {@link EndGameView} on the
     * main stage and sets up navigation back to the menu.
     * </p>
     *
     * @param session the current user session
     */
    @Override
    public void enter(UserSession session) {
        if (gm.getStage() != null) {
            EndGameView view = new EndGameView(
                    gm.getStage(),
                    () -> gm.setState(new MenuState(gm))
            );

            Scene scene = new Scene(view, 1140, 640);
            gm.getStage().setScene(scene);
        }
    }
}
