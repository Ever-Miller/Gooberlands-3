/** 
 * File: BattleState.java
 * Purpose:
 * 		Represents the current state of an ongoing battle between two Trainers.
 * 		This class stores references to both competing trainers and tracks the current {@link BattlePhase}, 
 * 		such as whether the battle is still in progress or one side has won. 
 * 
 * <p> All mutable battle information (HP totals, active Goobers, fainting status) is stored within the Trainers' Goobers. 
 * The {@code BattleState} serves as a structural wrapper that helps the {@link BattleManager} coordinate turn resolution and determine the battle outcome. 
 */
package battle;

import java.util.ArrayList;
import java.util.List;

import models.Trainer;

public class BattleState {
	
	/**
	 * File: BattlePhase.java
	 * Purpose:
	 * 		Represents the current phase of a battle.
	 * 
	 * <ul>
	 * <li>{@code IN_PROGRESS} – the battle is ongoing</li> 
	 * <li>{@code PLAYER_WIN} – the player has won the battle</li> 
	 * <li>{@code OPPONENT_WIN} – the opponent has won</li> 
	 * </ul>
	 * 
	 * This enum helps the {@link BattleState} report whether the battle should continue and helps the {@link battle.BattleManager} determine how to resolve turns. 
	 */

	public enum BattlePhase {
		IN_PROGRESS, PLAYER_WIN, OPPONENT_WIN

	}
	/** The player-controlled trainer. */
	private Trainer player;
	/** The opposing trainer. */
	private Trainer opponent;
	/** Current phase of the battle. */
	private BattlePhase phase;
	
    /** 
     * Constructs a new battle state with the given player and opponent.
     * 
     * @param player 		the player-controlled trainer
     * @param opponent 		the AI or remote opponent trainer     
     */
	public BattleState(Trainer player, Trainer opponent) {
		this.player = player;
		this.opponent = opponent;
		this.phase = BattlePhase.IN_PROGRESS;
	}
	
	/** @return the player trainer */
	public Trainer getPlayer() {
		return player;
	}
	
	/** @return the opponent trainer */
	public Trainer getOpponent() {
		return opponent;
	}
	
	/** @return the current battle phase */
	public BattlePhase getPhase() {
		return phase;
	}

	/**
	 * Sets the current battle phase.
	 * This should only be called by the {@link BattleManager}.
	 * 
	 * @param phase the new battle phase     
	 */
	public void setPhase(BattlePhase phase) {
		this.phase = phase;
	}
	
	public void setPlayer(Trainer player) {
	    this.player = player;
	}

	public void setOpponent(Trainer opponent) {
	    this.opponent = opponent;
	}
	
    /**
     * Returns whether the battle has ended.
     * 
     * @return true if the battle is in a winning state, false if still in progress     
     */
	public boolean isFinished() {
		return phase != BattlePhase.IN_PROGRESS;
	}
	
    /**
     * Returns the trainer who won the battle, or null if the battle is still ongoing.
     * 
     * @return the winning trainer, or null if no winner yet     
     */
	public Trainer getWinner() {
		if (phase == BattlePhase.PLAYER_WIN) {
			return player;
		}
		if (phase == BattlePhase.OPPONENT_WIN) {
			return opponent;
		}
		
		return null;
	}
}
