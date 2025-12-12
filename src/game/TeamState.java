/**
 * File: TeamState.java
 * Purpose:
 *      Represents the game state where the player can view
 *      and manage their current Goober team.
 */

package game;

import controller.TeamController;
import view.TeamPage;

/**
 * A game state that displays the team management screen.
 * <p>
 * This state allows the player to view their trainer,
 * inspect their current team of Goobers, and review
 * available resources such as coins.
 * </p>
 */
public class TeamState implements GameState {

    private GameManager gm;
    private TeamPage view;
    private TeamController controller;

    /**
     * Constructs a TeamState.
     *
     * @param gm the main game manager
     */
    public TeamState(GameManager gm) {
        this.gm = gm;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Initializes the team management view by creating the
     * {@link TeamController} and {@link TeamPage}, then
     * displays the scene.
     * </p>
     *
     * @param session the active user session
     */
    @Override
    public void enter(UserSession session) {
        this.controller = new TeamController(session);
        view = new TeamPage(gm.getStage(), controller);

        if (gm.getStage() != null) {
            gm.getStage().getScene().setRoot(view);
        }
    }
}
