/**
 * File: GooberMove.java
 * Purpose:
 * 		Represents a single move that a {@link Goober} can use in battle.
 * 		A move has a base damage value, hit chance, critical hit chance, an optional side {@link Effect}, and a level requirement to unlock.
 * 
 */
package models;

import java.io.Serializable;

import effects.Effect;
import effects.TargetType;

public class GooberMove implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private final String name;
	private final int damage;
	private final int unlockLevel;
	
	private final Effect effect;
	private final double hitChance;
	private final double critChance;
	
	private final TargetType targetType;
	
	/**
	 * Constructs a new move with the given parameters
	 * 
	 * @param name				the display name of the move
	 * @param damage			the base damage dealt when the move hits
	 * @param effect			the side effect applied on a successful hit, or {@code null} if the move has no additional effect
	 * @param hitChance			the probability (0.0–1.0) that this move will hit
	 * @param critChance		the probability (0.0–1.0) that this move will critically hit
	 * @param unlockLevel		the minimum level a {@link Goober} must have to use this move
	 */
	public GooberMove(String name, int damage, Effect effect, double hitChance, double critChance, TargetType targetType, int unlockLevel) {
		this.name = name;
		this.damage = damage;
		this.effect = effect;
		this.hitChance = hitChance;
		this.critChance = critChance;
		this.targetType = targetType;
		this.unlockLevel = unlockLevel;
	}
	
	/**
	 * Returns the minimum level required to use this move.
	 * 
	 * @return the unlock level for this move
	 */
	public int getUnlockLevel() { return unlockLevel; }
	
	/**
	 * Returns the name of this move.
	 * 
	 * @return the move's name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the base damage of this move.
	 * 
	 * @return the damage value
	 */
	public int getDamage() {
		return damage;
	}
	
	/**
	 * Returns the probability (0.0–1.0) that this move will hit.
	 * 
	 * @return the hit chance
	 */
	public double getHitChance() {
		return hitChance;
	}
	
	/**
	 * Returns the probability (0.0–1.0) that this move will be a critical hit.
	 * 
	 * @return the critical hit chance
	 */
	public double getCritChance() {
		return critChance;
	}
	
	/**
	 * Returns the side effect that this move applies on a successful hit, or {@code null} if the move has no additional effect.
	 * @return the move's effect, or {@code null}
	 */
	public Effect getEffect() {
		return effect;
	}
	
	public TargetType getTargetType() { return targetType; }
}
