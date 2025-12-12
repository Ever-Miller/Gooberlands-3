/**
 * File: Difficulty.java
 * Purpose:
 *      Defines configuration settings for the AI agent
 *      based on game difficulty.
 */

package ai;

/**
 * Configuration settings for the AI agent based on game difficulty.
 * <p>
 * Controls the game tree search depth and random move chance
 * of the {@link MiniMaxAgent}.
 * </p>
 */
public enum Difficulty {

    /** Very forgiving. Looks only 1 move ahead and makes random mistakes 50% of the time. */
    EASY(1, 0.5, "easy"),

    /** Balanced. Looks 3 moves ahead with a 30% random move rate. */
    MEDIUM(3, 0.3, "medium"),

    /** Challenging. Looks 5 moves ahead and rarely makes mistakes. */
    HARD(5, 0.1, "hard"),

    /** Optimal play. Deep search (7 moves) and zero intentional errors. */
    IMPOSSIBLE(7, 0.0, "impossible");

    /** The depth of the MiniMax recursion tree. Higher means smarter but slower. */
    private final int depth;

    /**
     * The probability (0.0 to 1.0) that the AI will select
     * a random valid move instead of the optimal one.
     */
    private final double randomMoveChance;

    /** Lowercase name used for display or configuration. */
    private final String name;

    /**
     * Constructs a difficulty configuration.
     *
     * @param depth the MiniMax search depth
     * @param randomMoveChance probability of selecting a random move
     * @param name the difficulty name
     */
    Difficulty(int depth, double randomMoveChance, String name) {
        this.depth = depth;
        this.randomMoveChance = randomMoveChance;
        this.name = name;
    }

    /**
     * Returns the search depth for this difficulty.
     *
     * @return the MiniMax search depth
     */
    public int getDepth() {
        return depth;
    }

    /**
     * Returns the probability that the AI will choose a random move.
     *
     * @return the random move chance (0.0–1.0)
     */
    public double randomMoveChance() {
        return randomMoveChance;
    }

    /**
     * Returns the next higher difficulty tier.
     * <p>
     * Used for difficulty scaling. Difficulty is capped
     * at {@link Difficulty#IMPOSSIBLE}.
     * </p>
     *
     * @return the next difficulty level
     */
    public Difficulty getNext() {
        switch (depth) {
            case 1:
                return MEDIUM;
            case 3:
                return HARD;
            default:
                return IMPOSSIBLE;
        }
    }

    /**
     * Returns the display name of the difficulty.
     *
     * @return the difficulty name
     */
    public String getName() {
        return name;
    }
}
