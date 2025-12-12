/**
 * File: ItemType.java
 * Purpose:
 * 		Describes the basic behavior of a battle item
 */
package items;

public enum ItemType {
	/** Restores HP to the user's active Goober. */
	HEAL,
	
	/** Deals direct damage to the opposing active Goober. */
	DAMAGE,
	
	/** Applies a stun effect to the opposing active Goober. */
	STUN
}
