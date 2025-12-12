/**
 * File: GooberState.java
 * Purpose:
 * 		Represents the dynamic, battle-modifiable state of a Goober.
 * 		This class tracks the Goober's current HP, level-based stats, applied status effects, temporary conditions (stun, accuracy debuffs), and XP progression. 
 * 		All permanent stats come from the base Goober. 
 */
package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import effects.Effect;
import effects.EffectType;

public class GooberState implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private final Goober goober;
	
	private int maxHp;
	private int currentHp;
    private int attack;
    private double critChance;
    private double defence;
    private int speed;

    // Temporary battle conditions
    private boolean stunned = false; // active stun
    private boolean pendingStun = false; // will stun next turn
    private double hitChance = 1.0; // accuracy multiplier
    
    private int baseLevelAttack;
    
    private ArrayList<Effect> effects = new ArrayList<>();
    private XPManager leveler;
    
    
    /** 
     * Creates a new GooberState for a given Goober at a specific level.
     * 
     * @param goober the Goober whose state this represents 
     * @param level  the starting level of the Goober     
     */
    public GooberState(Goober goober, int level) {
    	this.goober = goober;
    	leveler = new XPManager(level, 0);
    	
    	recalculateStats(level);
    	
    	this.currentHp = maxHp;
    }
    
    /**
     * Recalculates all stats based on the Goober's current level.
     * 
     * @param curLevel the level to base stat scaling on     
     */
    public void recalculateStats(int curLevel) {
		double multiplier = Math.pow((double) curLevel - 1, 1.5);
		maxHp = (int) (goober.getBaseMaxHp() + goober.getHpGrowth() * multiplier);
		attack = (int) (goober.getBaseAttack() + goober.getAttackGrowth() * multiplier);
		critChance = Math.min(goober.getBaseCrit() + goober.getCritGrowth() * multiplier, 1.0);
		defence = Math.min(goober.getBaseDefence() + goober.getDefenceGrowth() * multiplier, 1.0);
		
		this.baseLevelAttack = attack;
		
		int baseSpeed = 10;
		switch (goober.getType()) {
		case ASSASSIN: baseSpeed = 20; break;
		case DAMAGER: baseSpeed = 15; break;
		case SUPPORT: baseSpeed = 12; break;
		case TANK: baseSpeed = 8; break;
		case SPECIAL: baseSpeed = 21; break;
		case LOPUNNY: baseSpeed = 22; break;
		}
		
		speed = (int)(baseSpeed + curLevel * 1.5);
	}
    
    // Temporary Status Conditions
    
    /** Applies a stun next turn. */
    public void queueStun() { pendingStun = true; }
    
    /** Activates stun for this turn. Called by BattleManager. */
    public void applyStun() {
    	stunned = pendingStun;
    	pendingStun = false;
    }
    /** Removes the stun condition. */
	public void unStun() { stunned = false; }
	
	/** @return true if the Goober cannot act this turn. */
	public boolean isStunned() { return stunned; }
	
	/** @return true if accuracy has been modified this battle. */
	public boolean isDizzy() { return hitChance != 1.0; }
	
	public double getHitChance() { return hitChance; }
	
	public double incrStat(EffectType type, double strength) {
		double changeAmount;
		switch (type) {
		case CRIT_MODIFICATION:
			changeAmount = critChance * strength;
			critChance = Math.max(0.0, Math.min(1.0, critChance + changeAmount));
			return changeAmount;
			
		case DAMAGE_MODIFICATION:
			int atkChange = (int) (attack * strength);
			
			int maxLimit = baseLevelAttack * 3; 
			
			if (strength > 0) {
				if (attack >= maxLimit) {
					return 0;
				}
				if (attack + atkChange > maxLimit) {
					atkChange = maxLimit - attack;
				}
			}
			
			if (attack + atkChange < 1) {
				atkChange = 1 - attack;
			}
			
			attack += atkChange;
			return (double) atkChange;
			
		case DEFENCE_MODIFICATION:
			changeAmount = defence * strength;
			defence = Math.max(0.0, Math.min(1.0, defence + changeAmount));
			return changeAmount;
			
		case DIZZY:
			double oldHit = hitChance;
			hitChance = Math.max(0.0, hitChance - strength);
			return oldHit - hitChance;
			
		default:
			break;
		}
		return 0.0;
	}
	
	
	public void revertStatChange(EffectType type, double amount) {
		switch (type) {
		case DAMAGE_MODIFICATION:
			attack -= (int) amount;
			// Safety clamp
			if (attack < 1) attack = 1;
			break;
		case DEFENCE_MODIFICATION:
			defence = Math.max(0.0, Math.min(1.0, defence - amount));
			break;
		case CRIT_MODIFICATION:
			critChance = Math.max(0.0, Math.min(1.0, critChance - amount));
			break;
		case DIZZY:
			hitChance = Math.min(1.0, hitChance + amount);
			break;
		default:
			break;
		}
	}
	
	// EFFECTS
	
	/**
	 * Adds a status effect to the Goober.
	 * @param effect the effect to apply
	 */
	public void addEffect(Effect effect) {
		effects.add(effect);
	}
	
	/**
	 * Applies all effects for this turn and removes expired ones.
	 * Called by the BattleManager at the start or end of each turn.    
	 */
	public void cycleEffects() {
		for (int i = effects.size() - 1; i >= 0; i--) {
			Effect e = effects.get(i);
			Boolean done = e.apply();
			if (done) effects.remove(i);
		}
	}
	
	// HEALTH & XP
	
	/**
	 * Edits current HP by a delta, clamped between 0 and maxHp.
	 * @param change positive to heal, negative to damage     
	 */
	public void editHealth(int change) {
		currentHp = Math.max(0, Math.min(currentHp + change, maxHp));
	}
	
	public void setHealth(int amount) {
		currentHp = Math.max(0, Math.min(amount, maxHp));
	}
	
	/**
	 * Adds XP and recalculates stats on level up.
	 * 
	 * @param xpAmount amount of XP gained     
	 */
	public void gainXp(int xpAmount) {
		boolean leveled = leveler.addXp(xpAmount);
		if (leveled) {
			recalculateStats(leveler.getLevel());
			currentHp = maxHp;
		}
	}
	
	// GETTERS 
	
	public int getLevel() { return leveler.getLevel(); }
	public int getMaxHp() { return maxHp; }
	public int getAttack() { return attack; }
	public int getCurrentHp() { return currentHp; }
	public double getDefence() { return defence; }
	public double getCritChance() { return critChance; }
	public int getSpeed() { return speed; }
	public XPManager getXpManager() { return leveler; }
	
	/** @return true if HP is at or below zero. */
	public boolean isFainted() { return currentHp <= 0; }
	
	/** @return a list of all active status effects. */
	public List<Effect> getEffects() { return effects; }
}
