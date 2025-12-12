/** 
 * File: Action.java
 * Purpose:
 * 		Represents a single decision made by a {@link Trainer} during a battle turn. 
 * 		Different concrete action types (attack, switch, item usage, etc.) implement this interface and provide additional data as needed. 
 * 
 */
package action;

import models.Trainer;

public interface Action {

    /**
     * Returns the type of this action.
     *
     * @return the {@link ActionType} associated with this action
     */
	ActionType getType();
	
    /**
     * Returns the trainer who is performing this action.
     *
     * @return the acting trainer
     */
	Trainer getActor();
}
