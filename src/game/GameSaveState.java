/**
 * File: GameSaveState.java
 * Purpose:
 *      Represents a serialized snapshot of the user's progress
 *      used for saving and loading game state.
 */

package game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import models.Trainer;

/**
 * A serializable object that represents a frozen snapshot
 * of a user's game progress.
 * <p>
 * This class extracts only the essential data required to
 * restore a game session, including the player trainer,
 * level progression, and currency.
 * </p>
 * <p>
 * Instances of this class are written directly to disk via
 * {@code ObjectOutputStream}.
 * </p>
 */
public class GameSaveState implements Serializable {

    private static final long serialVersionUID = 1L;

    /** The player's trainer at the time of saving. */
    private final Trainer player;

    /** The list of levels and their completion state. */
    private List<Level> levels;

    /** The highest level completed by the player. */
    private final int highestCompleted;

    /** The number of coins the player has. */
    private final int coins;

    /**
     * Creates a save snapshot from the current user session.
     * <p>
     * This constructor performs a shallow copy of the level list
     * structure, relying on {@link Trainer} and {@link Level}
     * being serializable themselves.
     * </p>
     *
     * @param session the active {@link UserSession} to snapshot
     */
    public GameSaveState(UserSession session) {
        this.player = session.getPlayerTrainer();
        this.levels = new ArrayList<>(session.getLevels());
        this.highestCompleted = session.getHighestCompleted();
        this.coins = session.getCoins();
    }

    /**
     * Restores this saved snapshot into a user session.
     * <p>
     * This method should be called after loading a
     * {@code GameSaveState} object from disk.
     * </p>
     *
     * @param session the {@link UserSession} to populate
     */
    public void copyTo(UserSession session) {
        session.setPlayerTrainer(this.player);
        session.setLevels(this.levels);
        session.setHighestCompleted(this.highestCompleted);
        session.setCoins(this.coins);
    }
}
