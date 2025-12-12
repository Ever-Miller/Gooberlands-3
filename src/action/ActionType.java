
/**
 * File: ActionType.java
 * Purpose:
 * 		Enumerates the types of actions a Trainer may select during a battle turn. 
 * 		Each concrete {@link action.Action} corresponds to exactly one of these types.
 */
package action;

public enum ActionType {
	/** A direct attack using the active Goober's chosen move. */
	ATTACK, 
	
	/** Switching to another Goober in the Trainer's team. */
	SWITCH, 
	
	/** Using an item (heal, buff, stun, etc.) from the Trainer's inventory. */
	USE_ITEM,
	
	
	TRAINER_ABILITY
}
