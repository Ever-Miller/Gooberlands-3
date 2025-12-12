/**
 * File: ItemDescriptions.java
 * Purpose:
 *      Provides human-readable descriptions for item names
 *      used throughout the game UI.
 */

package items;

import java.util.HashMap;
import java.util.Map;

/**
 * A utility class that maps item names to description strings.
 * <p>
 * These descriptions are intended for display purposes only
 * (tooltips, shop views, inventory screens) and do not affect
 * gameplay mechanics.
 * </p>
 */
public class ItemDescriptions {

    /** Internal lookup table mapping item names to descriptions. */
    private static final Map<String, String> DESCRIPTIONS = new HashMap<>();

    static {
        DESCRIPTIONS.put(
                "Plankton",
                "He's after the formula. Restores 25% HP to your Goober."
        );
        DESCRIPTIONS.put(
                "Freakbob",
                "He is calling... Answer the phone. Restores 50% HP."
        );
        DESCRIPTIONS.put(
                "Job Application",
                "The scariest thing imaginable. Stuns the enemy for 2 turns."
        );
        DESCRIPTIONS.put(
                "Baby Thing",
                "A face only a mother could love. Stuns the enemy for a turn."
        );
        DESCRIPTIONS.put(
                "Chicken Nugget",
                "Gedagedigedagedago. Throws a nugget dealing 15% current health damage."
        );
        DESCRIPTIONS.put(
                "The Annoying Orange",
                "Remember when this guy was cool? Deals 25% current health damage."
        );
    }

    /**
     * Returns the description for a given item.
     *
     * @param itemName the name of the item
     * @return the description string, or a default message if none exists
     */
    public static String getDescription(String itemName) {
        return DESCRIPTIONS.getOrDefault(
                itemName,
                "A mysterious item with unknown properties."
        );
    }
}
