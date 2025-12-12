/**
 * File: MenuState.java
 * Purpose:
 *      Represents the main menu state of the game.
 */

package game;

import javafx.scene.Scene;
import view.MainPage;

/**
 * A game state that displays the main menu.
 * <p>
 * This state serves as the entry point for the player, providing
 * access to starting a new game, loading progress, and exiting
 * the application.
 * </p>
 */
public class MenuState implements GameState {

    private GameManager gm;
    private MainPage view;

    /**
     * Constructs a MenuState.
     *
     * @param gm the main game manager
     */
    public MenuState(GameManager gm) {
        this.gm = gm;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Initializes and displays the main menu scene.
     * </p>
     *
     * @param session the active user session (may be {@code null})
     */
    @Override
    public void enter(UserSession session) {
        if (gm.getStage() != null) {
            view = new MainPage(gm.getStage());
            Scene scene = new Scene(view, 1140, 640);
            gm.getStage().setScene(scene);
        }
    }
}
