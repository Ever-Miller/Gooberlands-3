/**
 * File: Level.java
 * Purpose:
 *      Represents a single stage in the single-player campaign.
 */

package game;

import java.io.Serializable;
import models.Trainer;

/**
 * Represents a discrete stage in single-player mode.
 * <p>
 * A {@code Level} acts as a container for both:
 * <ul>
 *   <li><b>Challenge Data:</b> The AI {@link Trainer} opponent.</li>
 *   <li><b>Progress Data:</b> Completion status and progression.</li>
 * </ul>
 * </p>
 * <p>
 * This class implements {@link Serializable} so it can be
 * persisted directly as part of {@link GameSaveState}.
 * </p>
 */
public class Level implements Serializable {

    private static final long serialVersionUID = 1L;

    /** The AI opponent assigned to this level. */
    private final Trainer opponent;

    /** The sequential index of this level (typically 1 through 7). */
    private final int levelNum;

    /**
     * The completion state of the level.
     * <ul>
     *   <li>0 — Not completed.</li>
     *   <li>1 — Completed (basic clear).</li>
     *   <li>&gt;1 — Higher completion rank (reserved for future use).</li>
     * </ul>
     */
    private int completionLevel = 0;

    /**
     * Constructs a new {@code Level}.
     *
     * @param opponent the {@link Trainer} the player must defeat
     * @param num the sequential level number
     */
    public Level(Trainer opponent, int num) {
        this.opponent = opponent;
        this.levelNum = num;
    }

    /**
     * Returns the opponent trainer for this level.
     *
     * @return the AI opponent
     */
    public Trainer getOpponent() {
        return opponent;
    }

    /**
     * Returns the numerical index of this level.
     *
     * @return the level number
     */
    public int getLevelNum() {
        return levelNum;
    }

    /**
     * Sets the completion level explicitly.
     * <p>
     * Intended for save loading or debugging. For normal gameplay
     * completion, prefer {@link #markCompleted()} or
     * {@link #updateCompletionLevel(int)}.
     * </p>
     *
     * @param n the new completion level (0 to reset)
     */
    public void setCompletionLevel(int n) {
        completionLevel = n;
    }

    /**
     * Updates the completion level if the new value is higher.
     *
     * @param n the proposed completion level
     */
    public void updateCompletionLevel(int n) {
        completionLevel = Math.max(completionLevel, n);
    }

    /**
     * Returns the current completion level.
     *
     * @return the completion level
     */
    public int getCompletionLevel() {
        return completionLevel;
    }

    /**
     * Checks whether this level has been completed.
     *
     * @return {@code true} if completed at least once, {@code false} otherwise
     */
    public boolean hasBeenCompleted() {
        return completionLevel > 0;
    }

    /**
     * Marks this level as completed with a minimum completion level of 1.
     * <p>
     * This method preserves higher completion results if the player
     * replays the level and performs worse.
     * </p>
     */
    public void markCompleted() {
        this.completionLevel = Math.max(completionLevel, 1);
    }
}
