/**
 * File: ItemAction.java
 * Purpose:
 * 		Represents an action where a Trainer uses an item during battle.
 * 		The specific logic for applying the item is handled by the BattleManager. 
 */

package action;

import models.Trainer;

public class ItemAction implements Action{
	
	private final Trainer actor;
	private final Trainer target;
	private final String itemName;
	
	/**
	 * Constructs an item action.
	 * 
	 * @param actor 		the trainer using the item
	 * @param target		the trainer whose active Goober is targeted (may be the same as actor for self-targeting items)
	 * @param itemName		the name of the item being used     */
	public ItemAction(Trainer actor, Trainer target, String itemName) {
		this.actor = actor;
		this.target = target;
		this.itemName = itemName;
	}

	@Override
	public ActionType getType() {
		return ActionType.USE_ITEM;
	}

	@Override
	public Trainer getActor() {
		return actor;
	}
	
	public Trainer getTarget() {
		return target;
	}
	
    /** 
     * @return the identifier of the item being used     
     */
	public String getItemName() {
		return itemName;
	}

}
