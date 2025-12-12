/**
 * File: TrainerAction.java
 * Purpose:
 * 		Represents the activation of a Trainer's unique ability.
 * 		Each trainer's ability is resolved by the BattleManager.
 */

package action;

import models.Trainer;

public class TrainerAction implements Action {
	private final Trainer actor;
	
	/**
	 * @param actor the trainer activating their ability
	 */
	public TrainerAction(Trainer actor) {
		this.actor = actor;
	}

	@Override
	public ActionType getType() {
		return ActionType.TRAINER_ABILITY;
	}

	@Override
	public Trainer getActor() {
		return actor;
	}

}
