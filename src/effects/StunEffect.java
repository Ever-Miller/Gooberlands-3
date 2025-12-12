/** 
 * File: StunEffect.java
 * Purpose:
 * 		A stun effect that prevents a Goober from acting on its turn.
 * 		The Goober remains stunned for the duration of the effect.
 * 		StunEffect does not directly set the stunned state.
 * 		Instead, it calls {@code queueStun()}, meaning the Goober becomes stunned at the start of the next turn when the BattleManager calls {@code applyStun()}.
 * 		When duration expires, the stun flag is cleared.
 */
package effects;

import models.Goober;

public class StunEffect extends Effect {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Creates a new stun effect with a specified target.
	 * 
	 * @param duration		the number of turns the effect lasts 
	 * @param target		the stunned Goober 
	 * @param strength		unused for stun, but kept for consistency  
	 */
	public StunEffect(int duration, Goober target, double strength) {
		super(EffectType.STUN, duration, target, strength);
	}
	
	/**
	 * Creates a new stun effect without an initial target.
	 * {@link #setTarget(Goober)} must be called before use.
	 * 
	 * @param duration		the number of turns the effect lasts
	 * @param strength		unused for stun
	 */
	public StunEffect(int duration, double strength) {
		this(duration, null, strength);
	}
	
	@Override
	public boolean apply() {
		if (target == null) {
			throw new IllegalStateException("StunEffect applied with null target");
		}
		// Queue the stun for the next turn
		target.getState().queueStun();
		duration -= 1;
		
		// When expired, remove stun if it's active
		if (duration <= 0) {
			target.getState().unStun();
			return true;
		}
		return false;
	}
	
	@Override
	public Effect copy() {
		return new StunEffect(this.duration, this.strength);
	}
}
