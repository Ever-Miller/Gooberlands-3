/**
 * File: Effect.java
 * Purpose:
 * 		Represents a status effect applied to a {@link Goober} during battle.
 * 
 * Effects have:
 * <ul> 
 * <li> An {@link EffectType} describing what they do </li> 
 * <li> A remaining duration in turns</li>
 * <li> A target Goober</li>
 * <li> A strength value used by the concrete implementation</li> 
 * </ul>
 * 
 * <p> The {@link #apply()} method is typically called once per turn. 
 * It should perform the effect's logic (damage, healing, stat change, etc.), decrement duration, and return {@code true} when the effect has expired and should be removed from the target. 
 */
package effects;

import java.io.Serializable;

import models.Goober;

public abstract class Effect implements Serializable {
	private static final long serialVersionUID = 1L;
	
	protected final EffectType type;
	protected int duration;
	protected Goober target;
	protected final double strength;
	
	protected double effectiveChange;
	
    /**
     * Creates a new Effect instance.
     * 
     * @param type 			the type of this effect
     * @param duration 		the number of turns this effect lasts
     * @param target 		the Goober this effect is applied to (may be null initially)
     * @param strength 		a strength or scaling factor used by the effect logic
     */
	public Effect(EffectType type, int duration, Goober target, double strength) {
		this.type = type;
		this.duration = duration;
		this.target = target;
		this.strength = strength;
	}
	
	/**
	 * Creates a new Effect without an initial target. 
	 * {@link #setTarget(Goober)} must be called before {@link #apply()} to avoid null-pointer issues.
	 * 
	 * @param type 			the type of this effect
	 * @param duration 		the number of turns this effect lasts
	 * @param strength 		a strength or scaling factor
	 */
	public Effect(EffectType type, int duration, double strength) {
		this(type, duration, null, strength);
	}
	
	/**
	 * Sets the target Goober for this effect.
	 * @param target 		the Goober to affect
	 */
	public void setTarget(Goober target) { this.target = target; }
	
	/** @return the type of this effect */
	public EffectType getType() { return type; }
	
	/** @return the remaining duration in turns */
	public int getDuration() { return duration; }
	
	/** @return the effect strength */
	public double getStrength() { return strength; }
	
	/**
	 * Applies this effect to the target for one turn. 
	 * Implementations should perform their logic, decrement {@link #duration}, and return true when the effect has expired and should be removed from the target.
	 * @return true if the effect has expired and should be removed
	 */
	public abstract boolean apply();
	
	public abstract Effect copy();
}
