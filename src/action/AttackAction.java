/** 
 * File: AttackAction.java
 * Purpose:
 * 		Represents an attack action performed by a Trainer's active Goober. 
 * 		This action stores which move index should be used during the turn.
 * 
 * <p> The actual move lookup is performed by the battle system using the trainer's currently active Goober and this index. 
 */
package action;

import models.Trainer;

public class AttackAction implements Action {
	/** The trainer performing the attack. */
	private final Trainer actor;
	/** Index of the move chosen from the Goober's usable move list. */
	private final int moveIndex;
	
	/**
    * Constructs a new attack action.
    *
    * @param actor			the trainer performing the attack
    * @param moveIndex		the index of the chosen move in the active Goober's usable move list
    */
	public AttackAction(Trainer actor, int moveIndex) {
		this.actor = actor;
		this.moveIndex = moveIndex;
	}
	
	public AttackAction(int moveIndex2) {
		this.actor = null;
		this.moveIndex = 0;
	}
	@Override
	public ActionType getType() {
		return ActionType.ATTACK;
	}
	
	@Override
	public Trainer getActor() {
		return actor;
	}
	
    /**
     * Returns the index of the move chosen by the trainer.
     *
     * @return the move index (0-based)
     */
	public int getMoveIndex() {
		return moveIndex;
	}

}
