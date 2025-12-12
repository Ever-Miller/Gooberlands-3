/** 
 * File: DizzyEffect
 * Purpose:
 * 		An effect that temporarily reduces a Goober's accuracy (hit chance), simulating a "dizzy" condition. 
 * 		The hit chance is decreased once when the effect is first applied and restored when the effect expires. 
 */
package effects;

import models.Goober;

public class DizzyEffect extends StatModificationEffect {
	private static final long serialVersionUID = 1L;

	/** 
	 * Creates a new dizzy effect with a specified target.
	 * 
	 * @param duration 		the number of turns the effect lasts
	 * @param target 		the Goober affected by this effect
	 * @param strength 		the amount to reduce hit chance by  
	 */
	public DizzyEffect(int duration, Goober target, double strength) {
		super(EffectType.DIZZY, duration, target, strength);
	}
	
	/**
	 * Creates a new dizzy effect without an initial target.
	 * {@link #setTarget(Goober)} must be called before use.
	 * 
	 * @param duration		the number of turns the effect lasts
	 * @param strength		the amount to reduce hit chance by   
	 */
	public DizzyEffect(int duration, double strength) {
		this(duration, null, strength);
	}
}
