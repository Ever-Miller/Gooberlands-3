/** 
 * File: SwitchAction.java
 * Purpose:
 * 		Represents an action where a Trainer switches their active Goober to another one in their team. 
 */
package action;

import models.Trainer;

public class SwitchAction implements Action{
	private final Trainer actor;
	private final int newIndex;
	
    /**
     * @param actor 		the trainer switching Goobers
     * @param newIndex 		the index of the Goober to switch to     
     */
	public SwitchAction(Trainer actor, int newIndex) {
		this.actor = actor;
		this.newIndex = newIndex;
	}

	@Override
	public ActionType getType() {
		return ActionType.SWITCH;
	}

	@Override
	public Trainer getActor() {
		return actor;
	}
	
	/** 
	 * @return the index of the Goober the Trainer wishes to switch to     
	 */
	public int getNewIndex() {
		return newIndex;
	}


}
