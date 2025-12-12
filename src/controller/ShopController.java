/**
 * File: ShopController.java
 * Purpose:
 *      Manages shop-related interactions.
 *      Handles item purchases using the player's session data
 *      and updates inventory and currency accordingly.
 */

package controller;

import game.UserSession;
import items.ItemFactory;

public class ShopController {
    private final UserSession session;

    /**
     * Constructs a ShopController tied to a user session.
     *
     * @param session the current user session
     */
    public ShopController(UserSession session) {
        this.session = session;
    }

    /**
     * Attempts to purchase an item from the shop.
     * Deducts the item's cost from the player's coins and
     * adds the item to the player's inventory if successful.
     *
     * @param itemName the name of the item to purchase
     * @return true if the purchase was successful, false otherwise
     */
    public boolean attemptPurchase(String itemName) {
        int cost = ItemFactory.getItemCost(itemName);

        if (session.getCoins() >= cost) {
            session.addCoins(-cost);
            session.getPlayerTrainer().addItem(ItemFactory.createItem(itemName));
            return true;
        }
        return false;
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
