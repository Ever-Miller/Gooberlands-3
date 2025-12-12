/**
 * File: PoisonEffect.java:
 * Purpose:
 * 		An effect that deals damage to a Goober each turn, simulating poison.
 * 		Damage is based on a multiplier, strength, and level. 
 */
package effects;

import models.Goober;

public class PoisonEffect extends Effect {
	private static final long serialVersionUID = 1L;
	private int damage = -1;

	/**
	 * Creates a new poison effect with a specified target.
	 * 
	 * @param duration		the number of turns the effect lasts
	 * @param target		the Goober being poisoned
	 * @param strength		a scaling factor for damage
	 */
	public PoisonEffect(int duration, Goober target, double strength) {
		super(EffectType.POISON, duration, target, strength);
		
		if (target != null) this.damage = (int) (target.getMaxHp() * strength);
	}
	
	/**
	 * Creates a new poison effect without an initial target.
	 * {@link #setTarget(Goober)} must be called before use.
	 * 
	 * @param duration		the number of turns the effect lasts
	 * @param strength		a scaling factor for damage      
	 */
	public PoisonEffect(int duration, double strength) {
		this(duration, null, strength);
	}

	@Override
	public boolean apply() {
		if (target == null) {
			throw new IllegalStateException("PoisonEffect applied with null target");
		}
		
		if (damage == -1) damage = target.getMaxHp(); 
		
		target.getState().editHealth(-damage);
		duration -= 1;
		return (duration <= 0);
	}
	
	@Override
	public PoisonEffect copy() {
		return new PoisonEffect(this.duration, this.strength);
	}
}
