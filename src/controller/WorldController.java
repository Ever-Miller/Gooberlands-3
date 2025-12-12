/**
 * File: WorldController.java
 * Purpose:
 *      Manages world-level interactions and progression.
 *      Provides access to level availability, level data,
 *      and handles starting battles from the world map.
 */

package controller;

import game.GameManager;
import game.Level;
import game.UserSession;

public class WorldController {
    private final GameManager gm;
    private final UserSession session;

    /**
     * Constructs a WorldController with access to the game manager
     * and user session.
     *
     * @param gm      the main game manager
     * @param session the current user session
     */
    public WorldController(GameManager gm, UserSession session) {
        this.gm = gm;
        this.session = session;
    }

    /**
     * Checks whether a given level is locked.
     *
     * @param levelNum the level number to check
     * @return true if the level is locked, false otherwise
     */
    public boolean isLevelLocked(int levelNum) {
        return !session.isLevelUnlocked(levelNum);
    }

    /**
     * Starts the battle associated with the specified level.
     *
     * @param levelNum the level number to start
     */
    public void startLevel(int levelNum) {
        gm.startBattleForLevel(levelNum);
    }

    /**
     * Returns the level data for the specified level.
     *
     * @param levelNum the level number
     * @return the level data, or null if not found
     */
    public Level getLevelData(int levelNum) {
        return session.getLevel(levelNum);
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
