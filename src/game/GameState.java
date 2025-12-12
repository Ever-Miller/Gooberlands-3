/**
 * File: GameState.java
 * Purpose:
 *      Defines the core contract for the State design pattern
 *      used by the {@link GameManager}.
 */

package game;

/**
 * The core contract for the game state system.
 * <p>
 * Each implementation of this interface represents a distinct
 * phase or screen of the game (e.g., Menu, Trainer Select,
 * World Map, Battle).
 * </p>
 * <p>
 * The {@link GameManager} maintains a reference to one active
 * {@code GameState} at a time and delegates control flow to it.
 * </p>
 */
public interface GameState {

    /**
     * Called immediately after the {@link GameManager} transitions
     * to this state.
     * <p>
     * Implementations should use this method to initialize UI,
     * prepare resources, and retrieve any required data from
     * the active {@link UserSession}.
     * </p>
     *
     * @param session the active {@link UserSession} containing
     *                player data such as team, inventory, and progress
     */
    void enter(UserSession session);
}
