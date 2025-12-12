/**
 * File: TeamController.java
 * Purpose:
 *      Provides access to the player's team data.
 *      Acts as a controller for team-related views by exposing
 *      trainer, team, and currency information.
 */

package controller;

import java.util.List;

import game.UserSession;
import models.Goober;
import models.Trainer;

public class TeamController {
    private final UserSession session;

    /**
     * Constructs a TeamController tied to a user session.
     *
     * @param session the current user session
     */
    public TeamController(UserSession session) {
        this.session = session;
    }

    /**
     * Returns the player's trainer.
     *
     * @return the player trainer
     */
    public Trainer getPlayerTrainer() {
        return session.getPlayerTrainer();
    }

    /**
     * Returns the list of goobers in the player's team.
     *
     * @return the player's team
     */
    public List<Goober> getTeam() {
        return session.getPlayerTrainer().getTeam();
    }

    /**
     * Returns the player's current coin balance.
     *
     * @return the number of coins owned by the player
     */
    public int getPlayerCoins() {
        return session.getCoins();
    }
}
