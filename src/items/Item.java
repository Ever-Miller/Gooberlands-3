/**
 * File: Item.java
 * Purpose:
 * 		Represents a consumable battle item.
 * 		Items are single-use and are removed from a Trainer's inventory when used in battle. 
 */
package items;

import java.io.Serializable;
import java.util.List;

public class Item implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private final String name;
	private final ItemType type;
	private final boolean targetSelf;
	private final double magnitude;
	private final int cost;
	
	public static final List<String> NAMES = List.of(
			"Chicken Nugget",
			"Plankton",	
	        "Baby Thing",
	        "The Annoying Orange",
	        "Freakbob",
	        "Job Application"
	    );
	
	/**
	 * Constructs a new item.
	 * 
	 * @param name display name of the item 
	 * @param type behavior category of the item
	 * @param targetSelf true if the item targets the user's active Goober false if it targets the opponent's active Goober
	 * @param magnitude generic strength value (amount healed, damaged, etc.)
	 * @param cost Gold cost of the item in the shop
	 */
	public Item(String name, ItemType type, boolean targetSelf, double magnitude, int cost) {
		this.name = name;
		this.type = type;
		this.targetSelf = targetSelf;
		this.magnitude = magnitude;
		this.cost = cost;
	}
	
    /** @return the name of this item */
	public String getName() { return name; }
    
    /** @return the item type (heal, damage, stun) */
	public ItemType getType() { return type; }
    
    /** @return true if the item targets the user's own Goober */
	public boolean isTargetSelf() { return targetSelf; }
    
    /** @return the magnitude (heal amount, damage amount, etc.) */
	public double getMagnitude() { return magnitude; }
	
	public int getCost() { return cost; }
}
