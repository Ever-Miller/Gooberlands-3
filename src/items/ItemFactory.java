package items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * File: ItemFactory.java
 * Purpose:
 * 		Provides centralized creation of all known {@link Item} objects used in the game.
 * 		Each call returns a NEW instance (items are consumable and cannot be reused).
 * <p> This factory ensures:
 * <ul>
 * <li> Consistent item behavior across the game</li>
 * <li>Safe lookup via item name</li>
 * <li>Support for adding future items with minimal changes</li>
 * </ul>
 */

public class ItemFactory {
	/** Internal registry of item definitions keyed by name. */
	private static final Map<String, ItemDefinition> ITEM_REGISTRY = new HashMap<>();
	// Static: register all items once.
	static {
		register("Baby Thing",          ItemType.STUN,   false, 1.0, 5);
		register("Job Application",     ItemType.STUN,   false, 2.0, 10);
		register("Plankton",            ItemType.HEAL,   true,  0.25, 5);
		register("Freakbob",            ItemType.HEAL,   true,  0.50, 10);
		register("Chicken Nugget",      ItemType.DAMAGE, false, 0.15, 5);
		register("The Annoying Orange",  ItemType.DAMAGE, false, 0.25, 10);}
	
	/**
	 * Defines the immutable data needed to instantiate items.
	 * This lets the registry create NEW instances each time.*/
	private static class ItemDefinition {
		final String name;final ItemType type;
		final boolean targetSelf;
		final double magnitude;
		final int cost;
		
		ItemDefinition(String name, ItemType type, boolean targetSelf, double magnitude, int cost) {
			this.name = name;
			this.type = type;
			this.targetSelf = targetSelf;
			this.magnitude = magnitude;
			this.cost = cost;
			}
		}
	
	/**
	 * Registers a named item into the factory registry.
	 * @param name the item's display name
	 * @param type the category of effect (HEAL, DAMAGE, STUN)
	 * @param targetSelf true if item targets the user's own Goober
	 * @param magnitude integer strength value
	 */
	private static void register(String name, ItemType type, boolean targetSelf, double magnitude, int cost) {
		ITEM_REGISTRY.put(name, new ItemDefinition(name, type, targetSelf, magnitude, cost));
	}
	
	/**
	 * Creates a NEW {@link Item} instance using the name lookup.
	 * 
	 * @param name the item name (case-sensitive)
	 * @return a newly constructed Item
	 * @throws IllegalArgumentException if the name is unknown
	 */
	public static Item createItem(String name) {
		ItemDefinition def = ITEM_REGISTRY.get(name);
		
		if (def == null) {
			throw new IllegalArgumentException("Unknown item: " + name);
		}
		return new Item(def.name, def.type, def.targetSelf, def.magnitude, def.cost);
	}
	
	/** 
	 * @return an unmodifiable list of all known item names.
	 * Useful for shops, UI dropdowns, debugging.
	 */
	public static List<String> getAllItemNames() {
		return Item.NAMES;
	}
	
	/** 
	 * Creates a basic sample inventory for early testing, debugging, or as a player starter kit.
	 * @return a new List of sample Items
	 */
	public static List<Item> createStarterInventory() {
		List<Item> items = new ArrayList<>();
		items.add(createItem("Plankton"));
		items.add(createItem("The Annoying Orange"));
		items.add(createItem("Chicken Nugget"));
		return items;
	}
	
	public static String getItemImagePath(String itemName) {
        String fileName = "";
        switch (itemName) {
            case "Plankton":           fileName = "plankton.png"; break;
            case "Freakbob":           fileName = "freakbob.png"; break;
            case "Job Application":    fileName = "JobApplication.png"; break;
            case "Baby Thing":         fileName = "AshBaby.jpg"; break;
            case "Chicken Nugget":     fileName = "chickenNugget.png"; break;
            case "The Annoying Orange": fileName = "annoying_orange.png"; break;
            default:                   return null; 
        }
        return "/view/assets/images/item_sprites/" + fileName;
    }
	
	public static int getItemCost(String itemName) {
		return ITEM_REGISTRY.get(itemName).cost;
	}
}
