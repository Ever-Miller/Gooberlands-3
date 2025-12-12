/**
 * File: XPManager.java
 * Purpose:
 *      Encapsulates experience point tracking and level progression logic
 *      for Goobers.
 *
 *      This class manages XP accumulation, level-up thresholds,
 *      and XP rewards granted when enemies are defeated.
 *
 *      It is designed to be serializable so that Goober progression
 *      persists across game saves.
 */

package models;

import java.io.Serializable;

/**
 * Encapsulates the XP and leveling logic for a Goober.
 * <p>
 * This class handles the functions for leveling up, experience generation 
 * when defeating opponents, and state persistence. It implements {@link Serializable} 
 * to allow the Goober's progress to be saved to disk.
 * </p>
 */
public class XPManager implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final int MAXLEVEL = 67;
	
	private int xp;
	private int level;
	
	/**
	 * Constructs a specific XP state.
	 * <p>
	 * Useful for loading saves or creating high-level enemies.
	 * </p>
	 * * @param level The starting level (clamped to {@link #MAXLEVEL}).
	 * @param xp The starting accumulated XP.
	 */
	public XPManager(int level, int xp) {
		this.xp = xp;
		this.level = Math.min(MAXLEVEL, level);
	}
	
	/**
	 * Default constructor.
	 * <p>
	 * Initializes the manager at Level 1 with 0 XP.
	 * </p>
	 */
	public XPManager() {
		this(1, 0);
	}
	
	/**
	 * Adds experience points and processes potential level-ups.
	 * <p>
	 * This method employs a {@code while} loop to handle "overflow" XP, allowing 
	 * a Goober to gain multiple levels from a single large XP drop.
	 * </p>
	 * 
	 * @param xpAmount The amount of experience to add.
	 * @return {@code true} if the Goober gained at least one level; {@code false} otherwise.
	 */
	public boolean addXp(int xpAmount) {
		int max;
		boolean leveled = false;
		
		xp += xpAmount;
		max = getMaxXp(level);
		// Level up loop: Keep leveling up as long as we have enough XP and aren't at the cap.
		while (xp >= max && level < MAXLEVEL) {
			xp -= max;
			level += 1;
			max = getMaxXp(level);
			leveled = true;
		}
		
		return leveled;
	}
	
	/**
	 * Calculates the total XP required to complete a specific level.
	 * <p>
	 * <b>Formula: {@code level^2 + level + 100}
	 * <br>
	 * This represents a quadratic growth curve (parabolic), meaning higher levels 
	 * become significantly harder to reach than lower ones.
	 * </p>
	 * * @param level The level to calculate requirements for.
	 * @return The integer XP threshold.
	 */
	public static int getMaxXp(int level) {
		return (level * level) + level + 100;
	}
	
	/**
	 * Helper to get the XP requirement for the current level.
	 * * @return The integer XP threshold to reach the next level.
	 */
	public int getMaxXp() {
		return XPManager.getMaxXp(level);
	}
	
	/**
	 * Determines how much XP this entity yields when defeated.
	 * <p>
	 * Economy Logic:</b> Returns 33% of the XP required to complete the current level.
	 * <br>
	 * A player must defeat roughly 3 enemies of equal level to level up once.
	 * </p>
	 * 
	 * @param level The level of the defeated opponent.
	 * @return The amount of XP to award the winner.
	 */
	public static int xpToGive(int level) {
		return XPManager.getMaxXp(level) / 3;
	}
	
	public int getLevel() { return level; }
	
	public int getCurrentXp() {
		return xp;
	}
	
	public void setXp(int xp) { this.xp = xp; }
}
