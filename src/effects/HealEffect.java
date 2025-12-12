/** 
 * File: HealEffect.java
 * Purpose:
 * 		An effect that heals a Goober for a fixed amount each turn.
 * 		The heal amount is based on a multiplier, strength, and level. 
 */
package effects;

import models.Goober;

public class HealEffect extends Effect {
	private static final long serialVersionUID = 1L;
	/** Base heal multiplier used to scale healing. */
	private int healAmount;
	
	/**
	 * Creates a new heal effect with a specified target.
	 * 
	 * @param duration		the number of turns the effect lasts
	 * @param target		the Goober being healed
	 * @param strength		a scaling factor for healing  
	 */
	public HealEffect(int duration, Goober target, double strength) {
		super(EffectType.HEAL, duration, target, strength);
		if (target != null) {
			this.healAmount = (int) (target.getMaxHp() * strength);
		}
	}
	
	/**
	 * Creates a new heal effect without an initial target.
	 * {@link #setTarget(Goober)} must be called before use.
	 * 
	 * @param duration		the number of turns the effect lasts
	 * @param strength		a scaling factor for healing   
	 */
	public HealEffect(int duration, double strength) {
		this(duration, null, strength);
	}
	
	@Override
	public boolean apply() {
		if (target == null) {
			throw new IllegalStateException("HealEffect applied with null target");
		}
		
		this.healAmount = (int) (target.getMaxHp() * strength);
		target.getState().editHealth(healAmount);
		duration -= 1;
		return (duration <= 0);
	}
	
	@Override
	public HealEffect copy() {
		return new HealEffect(this.duration, this.strength);
	}
}
