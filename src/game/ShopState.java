/**
 * File: ShopState.java
 * Purpose:
 *      Represents the in-game shop state where the player
 *      can purchase items using earned coins.
 */

package game;

import controller.ShopController;
import view.ShopPage;

/**
 * A game state that displays the item shop.
 * <p>
 * This state allows the player to browse and purchase
 * items using their current coin balance before
 * returning to gameplay.
 * </p>
 */
public class ShopState implements GameState {

    private GameManager gm;
    private ShopPage view;
    private ShopController controller;

    /**
     * Constructs a ShopState.
     *
     * @param gm the main game manager
     */
    public ShopState(GameManager gm) {
        this.gm = gm;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Initializes the shop by creating the {@link ShopController}
     * and {@link ShopPage}, then displays the shop scene.
     * </p>
     *
     * @param session the active user session
     */
    @Override
    public void enter(UserSession session) {
        controller = new ShopController(session);
        view = new ShopPage(gm.getStage(), controller);

        if (gm.getStage() != null) {
            gm.getStage().getScene().setRoot(view);
        }
    }
}
