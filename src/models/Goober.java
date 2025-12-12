/**
 * File: Goober.java
 * Purpose:
 * 		Represents a battle creature with fixed base stats, growth rates, and a mutable combat state tracked by a {@link GooberState}.
 * 		A Goober's stats scale with level, and it unlocks new moves as it levels up.
 * 
 * <p> Each Goober species has:
 * <ul>
 * 		<li> Base Stats (HP, Attack, Defence, crit chance) </li>
 * 		<li> Growth values that determine stat increases per level </li>
 * 		<li> A predefined list of possible moves (provided by {@link MoveFactory})</li>
 * 		<li> A {@link GooberType} that classifies its role in battle</li>
 * </ul>
 * 
 * <p> The {@code Goober} class itself is mostly immutable.
 * 		Base stats and growth values never change after construction. All mutable, battle-specific values are stored inside the {@link GooberState}
 */
package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import effects.Effect;

public class Goober implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * Represents the combat role of a Goober
	 */

	public static final List<String> NAMES = List.of(
	        "Tralalero Tralala",
	        "DaBaby",
	        "Peter Griffin",
	        "John Pork",
	        "Tung Tung Tung Sahur",
	        "Doge",
	        "Boss Baby",
	        "Duolingo Bird",
	        "William Shakespear"
	    );
	
	public static final List<String> SPECIAL_NAMES = List.of(	        
			"Mahoraga",
	        "LeBron",
	        "Gigachad");
	
	private final String name;
	private final GooberType type;
	
	private final int baseMaxHp;
	private final int baseAttack;
	private final double baseCrit;
	private final double baseDefence;
    
	private final int hpGrowth;
	private final int attackGrowth;
	private final double critGrowth;
	private final double defenceGrowth;
	
    private GooberState state;
	private List<GooberMove> moves;
	
	/**
	 * Constructs a new Goober with the given base stats, growth values, and starting level.
	 * Battle-modifiable data is stored within a new {@link GooberState}.
	 * The Goober's potential move set is retrieved from the {@link MoveFactory} based on its name
	 * 
	 * @param name				the display name of this Goober
	 * @param type				the combat role
	 * @param baseMaxHp			the base maximum HP for level 1
	 * @param baseAttack		the base attack value for level 1
	 * @param baseCrit			the base critical strike chance at level 1
	 * @param baseDefence		the base defence value for level 1
	 * @param hpGrowth			HP gained per level
	 * @param attackGrowth		attack gained per level
	 * @param critGrowth		crit chance gained per level
	 * @param defenceGrowth		defence gained per level
	 * @param level				the initial level of this Goober
	 */
	public Goober(String name, GooberType type, int baseMaxHp, int baseAttack, double baseCrit, double baseDefence,
            int hpGrowth, int attackGrowth, double critGrowth, double defenceGrowth, int level) {
		
		// Base stats (never change)
		this.name = name;
		this.type = type;
        this.baseMaxHp = baseMaxHp;
        this.baseAttack = baseAttack;
        this.baseCrit = baseCrit;
        this.baseDefence = baseDefence;
        
        // Growth stats
        this.hpGrowth = hpGrowth;
        this.attackGrowth = attackGrowth;
        this.critGrowth = critGrowth;
        this.defenceGrowth = defenceGrowth;
        
        // State and move setup
        state = new GooberState(this, level);
        moves = MoveFactory.getMoves(name);
        
        if (moves == null) {
        	moves = new ArrayList<>();
        }
     
	}
	
	/**
	 * Returns the internal state object that tracks level, HP, XP, status effects, and derived stats
	 * 
	 * @return this Goober's state
	 */
	public GooberState getState() { return state; }
	
	/**
	 * Aoolies a battle effect to this Goober.
	 * 
	 * @param effect		The effect to apply
	 */
	public void addEffect(Effect effect) { state.addEffect(effect); }
	
	/**
	 * Grants experience to this Goober. Level-Up logic is handled by {@link GooberState} and {@link XPManager}
	 * 
	 * @param xpAmount		The amount of XP to gain
	 */
	public void gainXp(int xpAmount) { state.gainXp(xpAmount); }
	
	/**
	 * Returns a list of all moves this Goober can currently use.
	 * A move is usable if the Goober's level is greater than or equal to the move's unlock level.
	 * 
	 * @return a list of currently usable moves
	 */
	public ArrayList<GooberMove> getUsableMoves() {
		ArrayList<GooberMove> usable = new ArrayList<>();
		
		int curLevel = state.getLevel();
		for (GooberMove m : moves) {
			if (curLevel >= m.getUnlockLevel()) usable.add(m);
		}
		
		return usable;
	}
	

	/**
	 * Directly edits this Goober's HP by the given amount. 
	 * Positive values heal, negative values deal damage. 
	 * HP clamping and faint logic are handled inside the {@link GooberState}.
	 * 
	 * @param amount 	amount the amount to modify HP by
	 */
	public void editHealth(int amount) {
		state.editHealth(amount);
	}
	
	public void setHealth(int amount) {
		state.setHealth(amount);
	}
	
	/**
	 * Deals damage to this Goober. Damage must be non-negative.
	 * 
	 * @param dmg		the amount of damage to apply
	 * @throws IllegalArgumentException if dmg is negative
	 */
	public void takeDamage(int dmg) {
		if (dmg < 0) {
			throw new IllegalArgumentException("Damage must be non-negative");
		}
		state.editHealth(-dmg);
	}
	
	/**
	 * Heals this Goober by the given amount. 
	 * Healing must be non-negative.
	 * 
	 * @param amt		the amount of HP restored
	 * @throws IllegalArgumentException if amt is negative
	 */
	public void heal(int amt) {
		if (amt < 0) {
			throw new IllegalArgumentException("Heal amount must be non-negative");
		}
		state.editHealth(amt);
	}
	
	/**
	 * Returns the usable move at the specified index. 
	 * The index refers to the list returned by {@link #getUsableMoves()}, not the full move list.
	 * 
	 * @param index		the index into the usable moves list
	 * @return the move at the requested position
	 * @throws IndexOutOfBoundsException if the index is invalid
	 */
	public GooberMove getMove(int index) {
		return getUsableMoves().get(index);
	}
	
	/**
	 * Returns the list of active battle effects currently applied to this Goober.
	 * 
	 * @return a list of active effects
	 */
	public List<Effect> getEffects() {
		return state.getEffects();
	}
	
	/**
	 * Creates a new Goober with the same species stats, growth values, and level as this one. 
	 * The returned Goober has a fresh {@link GooberState} and full HP.
	 * 
	 * @return a new Goober with identical properties and starting state
	 */
	public Goober copy() {
		Goober clone = new Goober(name, type, baseMaxHp, baseAttack, baseCrit, baseDefence, hpGrowth, attackGrowth, critGrowth, defenceGrowth, getLevel());
		clone.getXpManager().setXp(this.getXpManager().getCurrentXp());
		clone.setHealth(this.getCurrentHp());
		return clone;
	}
	
	public void clearEffects() {
		state.clearEffects();
	}
	
	/** @return the name of this Goober */
	public String getName() { return name; }
	
	/** @return this Goober's type classification */
	public GooberType getType() { return type; }
	
	public int getBaseMaxHp() { return baseMaxHp; }
	
	/** @return the current HP of this Goober */
	public int getCurrentHp() { return state.getCurrentHp(); }
	
	/** @return the maximum HP based on level */
	public int getMaxHp() { return state.getMaxHp(); }
	
	public int getHpGrowth() { return hpGrowth; }
	
	public int getBaseAttack() { return baseAttack; }
	
	/** @return the attack value based on level */
	public int getAttack() { return state.getAttack(); }
	
	public int getAttackGrowth() { return attackGrowth; }
	
	public double getBaseDefence() { return baseDefence; }
	
	/** @return the defence value based on level */
	public double getDefence() { return state.getDefence(); }
	
	public double getDefenceGrowth() { return defenceGrowth; }
	
	public double getBaseCrit() { return baseCrit; }
	
	/** @return the crit chance based on level */
	public double getCritChance() { return state.getCritChance(); }
	
	public double getCritGrowth() { return critGrowth; }
	
	/** @return the current level of this Goober */
	public int getLevel() { return state.getLevel(); }
	
	/** @return the XP manager for this Goober */
	public XPManager getXpManager() { return state.getXpManager(); }
	
	/** @return whether this Goober has fainted (HP <= 0) */
	public boolean isFainted() { return state.isFainted(); }
	
	/** @return the speed stat, likely used for turn order */
	public int getSpeed() { return state.getSpeed(); }
}