/**
 * File: GooberState.java
 *
 * Purpose:
 * 		Represents the dynamic, battle-modifiable state of a Goober.
 * 		This class tracks the Goober's current HP, level-based stats, applied status effects,
 * 		temporary conditions (stun, accuracy debuffs), and XP progression.
 * 		All permanent stats originate from the base {@link Goober}.
 *
 * Design:
 * 		- This class is mutable during battles
 * 		- Works alongside {@link effects.Effect} and {@link battle.BattleManager}
 * 		- Serializable for save support
 */
package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import effects.Effect;
import effects.EffectType;
import effects.StatModificationEffect;

public class GooberState implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/** Base immutable Goober definition. */
	private final Goober goober;
	
	// --- Core Stats ---
	private int maxHp;
	private int currentHp;
    private int attack;
    private double critChance;
    private double defence;
    private int speed;

    // --- Temporary Battle Conditions ---
    private boolean stunned = false;        // active stun
    private boolean pendingStun = false;    // queues stun for next turn
    private double hitChance = 1.0;         // accuracy multiplier
    
    /** Base attack at current level, used for stat cap enforcement. */
    private int baseLevelAttack;
    
    /** Active effects applied to the Goober. */
    private ArrayList<Effect> effects = new ArrayList<>();
    
    /** XP and level management handler. */
    private XPManager leveler;
    
    
    /** 
     * Constructs a new GooberState for a specific Goober and level.
     *
     * Initializes XP, computes scaled stats, and sets HP to maximum.
     *
     * @param goober the Goober entity being represented
     * @param level  starting level
     */
    public GooberState(Goober goober, int level) {
    	this.goober = goober;
    	leveler = new XPManager(level, 0);
    	
    	recalculateStats(level);
    	
    	this.currentHp = maxHp;
    }
    
    /**
     * Recalculates all stats based on the supplied level.
     *
     * Uses non-linear scaling for HP, attack, defence, and crit.
     * Speed is determined by Goober type with level-based progression.
     *
     * @param curLevel the level used for stat scaling
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
    
    // ===============================
    // Temporary Status Conditions
    // ===============================
    
    /**
     * Queues a stun to be applied at the start of the next turn.
     */
    public void queueStun() { pendingStun = true; }
    
    /**
     * Applies a queued stun for the current turn.
     *
     * Called by {@link battle.BattleManager} at turn start.
     */
    public void applyStun() {
    	stunned = pendingStun;
    	pendingStun = false;
    }
    
    /**
     * Removes the stun condition.
     */
	public void unStun() { stunned = false; }
	
	/**
	 * @return true if the Goober is stunned and unable to act
	 */
	public boolean isStunned() { return stunned; }
	
	/**
	 * @return true if accuracy has been reduced from its default
	 */
	public boolean isDizzy() { return hitChance != 1.0; }
	
	/**
	 * @return current hit chance modifier
	 */
	public double getHitChance() { return hitChance; }
	
	/**
	 * Applies a stat modification effect.
	 *
	 * Used by {@link effects.StatModificationEffect}.
	 *
	 * @param type modification type
	 * @param strength strength multiplier
	 * @return actual amount modified
	 */
	public double incrStat(EffectType type, double strength) {
		switch (type) {
		case CRIT_MODIFICATION:
			double oldCrit = critChance;
			critChance = Math.max(0.0, Math.min(1.0, critChance + critChance * strength));
			return critChance - oldCrit;
			
		case DAMAGE_MODIFICATION:
			int atkChange = (int) (attack * strength);
			int maxLimit = baseLevelAttack * 3; 
			
			if (strength > 0) {
				if (attack >= maxLimit) return 0;
				if (attack + atkChange > maxLimit) atkChange = maxLimit - attack;
			}
			
			if (attack + atkChange < 1) atkChange = 1 - attack;
			
			attack += atkChange;
			return (double) atkChange;
			
		case DEFENCE_MODIFICATION:
			double oldDef = defence;
			defence = Math.max(0.0, Math.min(1.0, defence + defence * strength));
			return defence - oldDef;
			
		case DIZZY:
			double oldHit = hitChance;
			hitChance = Math.max(0.0, hitChance - strength);
			return oldHit - hitChance;
			
		default:
			return 0.0;
		}
	}
	
	/**
	 * Reverts a previously applied stat modification.
	 *
	 * @param type modification type
	 * @param amount amount to revert
	 */
	public void revertStatChange(EffectType type, double amount) {
		switch (type) {
		case DAMAGE_MODIFICATION:
			attack -= (int) amount;
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
	
    // ===============================
    // Effects Handling
    // ===============================
	
	/**
	 * Adds a new effect to the Goober.
	 *
	 * @param effect effect to add
	 */
	public void addEffect(Effect effect) {
		effects.add(effect);
	}
	
	/**
	 * Applies all active effects for the current turn.
	 *
	 * Expired effects are automatically removed.
	 */
	public void cycleEffects() {
		for (int i = effects.size() - 1; i >= 0; i--) {
			Effect e = effects.get(i);
			Boolean done = e.apply();
			if (done) effects.remove(i);
		}
	}
	
	/**
	 * Clears all effects and reverts stat changes.
	 */
	public void clearEffects() {
		for (Effect e : effects) {
			if (e instanceof StatModificationEffect) ((StatModificationEffect) e).remove();
		}
		effects.clear();
	}
	
    // ===============================
    // Health & XP
    // ===============================
	
	/**
	 * Adjusts HP by a delta value.
	 *
	 * @param change positive to heal, negative to damage
	 */
	public void editHealth(int change) {
		currentHp = Math.max(0, Math.min(currentHp + change, maxHp));
	}
	
	/**
	 * Sets HP to a fixed value within valid bounds.
	 *
	 * @param amount desired HP
	 */
	public void setHealth(int amount) {
		currentHp = Math.max(0, Math.min(amount, maxHp));
	}
	
	/**
	 * Grants XP and handles level-up logic.
	 *
	 * Fully heals the Goober after leveling.
	 *
	 * @param xpAmount XP earned
	 */
	public void gainXp(int xpAmount) {
		boolean leveled = leveler.addXp(xpAmount);
		if (leveled) {
			recalculateStats(leveler.getLevel());
			currentHp = maxHp;
		}
	}
	
	// ===============================
    // Getters
    // ===============================
	
	/** @return current Goober level */
	public int getLevel() { return leveler.getLevel(); }

	/** @return maximum HP value */
	public int getMaxHp() { return maxHp; }

	/** @return current attack stat */
	public int getAttack() { return attack; }

	/** @return current HP value */
	public int getCurrentHp() { return currentHp; }

	/** @return current defence multiplier */
	public double getDefence() { return defence; }

	/** @return current critical hit chance */
	public double getCritChance() { return critChance; }

	/** @return current speed stat */
	public int getSpeed() { return speed; }

	/** @return XP manager handling level progression */
	public XPManager getXpManager() { return leveler; }

	/** @return true if HP is zero or below */
	public boolean isFainted() { return currentHp <= 0; }

	/** @return list of active status effects */
	public List<Effect> getEffects() { return effects; }
}
