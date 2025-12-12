/**
 * File: MapState.java
 * Purpose:
 *      Represents the world map state where the player selects
 *      which level to play next.
 */

package game;

import controller.WorldController;
import javafx.scene.Scene;
import view.MapView;

/**
 * A game state that displays the world map.
 * <p>
 * This state allows the player to view unlocked levels, select
 * a level to battle, and track progression through the campaign.
 * </p>
 */
public class MapState implements GameState {

    private GameManager gm;
    private MapView view;
    private WorldController controller;
    private UserSession session;

    /**
     * Constructs a MapState.
     *
     * @param gm the main game manager
     */
    public MapState(GameManager gm) {
        this.gm = gm;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Initializes the world map:
     * <ul>
     *   <li>Ensures a valid {@link UserSession} exists</li>
     *   <li>Generates levels if not already present</li>
     *   <li>Creates the {@link WorldController} and {@link MapView}</li>
     *   <li>Displays the map scene</li>
     * </ul>
     * </p>
     *
     * @param session the active user session
     */
    @Override
    public void enter(UserSession session) {
        // Prefer the passed session, fall back to GameManager's session
        this.session = (session != null) ? session : gm.getSession();

        if (this.session == null) {
            this.session = new UserSession();
            gm.session = this.session;
        }

        if (this.session.getLevels() == null || this.session.getLevels().isEmpty()) {
            this.session.generateLevels();
        }

        // Create controller and view
        controller = new WorldController(gm, this.session);
        view = new MapView(gm.getStage(), controller);

        if (gm.getStage() != null) {
            Scene scene = new Scene(view, 1140, 640);
            gm.getStage().setScene(scene);
        }
    }
}
