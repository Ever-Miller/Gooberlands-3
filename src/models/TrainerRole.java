/**
 * File: TrainerRole.java
 * Purpose:
 *      Defines the available trainer archetypes in the game.
 *
 *      Each TrainerRole provides:
 *      - A display name used in the UI
 *      - A passive ability description (always active)
 *      - An active ability description (usable once per battle)
 *      - An associated soundtrack count for battle music
 *
 *      TrainerRole types directly influence gameplay mechanics,
 *      AI behavior, and audiovisual presentation.
 */

package models;

/**
 * Enumerates the different trainer archetypes available to players and enemies.
 */
public enum TrainerRole {
	NECROMANCER(
		"Necromancer", 
		"Life Steal: Goobers heal for 20% of damage dealt.", 
		"Raise Dead: Revive a random fainted Goober with 50% HP.",
		3
	),
	GAMBLER(
		"Gambler", 
		"High Roller: All Goobers gain +15% Crit Chance.", 
		"Red or Black: Deal 50% current health damage or take 25% current health damage.",
		4
		
	),
	CS_STUDENT(
		"CS Student", 
		"Memory Leak: Enemies start the battle with permanent Poison (Its a feature, not a bug)",
		"Sudo Kill: Instantly defeat an enemy if they have < 20% HP. Otherwise deal 0 damage (do the math).",
		4
	),
	WEEB(
		"Weeb", 
		"Plot Armor: The first time a Goober would faint, they survive with 1 HP.", 
		"Power of Friendship: Fully heal the active Goober and cure all status effects.",
		3
	),
	JOKER(
		"Joker", 
		"Society: Attacks have a 33% chance to apply a random Debuff (Stun, Dizzy, or Poison).", 
		"There's No Laws: 'Batman, I caught a little Pokemon...' Deals 69% current health damage and Stuns the enemy with pure trauma.",
		2
	);
	
	/** The display name shown in menus and UI elements. */
	private final String displayName;
	
	/** Description of the trainer's passive ability. */
	private final String passiveDesc;
	
	/** Description of the trainer's active ability. */
	private final String activeDesc;
	
	/** Number of music tracks associated with this trainer. */
	private final int nSongs;
	
	/**
	 * Constructs a TrainerRole with metadata used for gameplay and presentation.
	 *
	 * @param name     the display name of the trainer role
	 * @param passive  description of the passive ability
	 * @param active   description of the active ability
	 * @param nSongs   number of songs associated with this role
	 */
	TrainerRole(String name, String passive, String active, int nSongs) {
		this.displayName = name;
		this.passiveDesc = passive;
		this.activeDesc = active;
		this.nSongs = nSongs;
	}
	
	/**
	 * @return the display name of the trainer role
	 */
	public String getName() { return displayName; }
	
	/**
	 * @return the description of the passive ability
	 */
	public String getPassiveDescription() { return passiveDesc; }
	
	/**
	 * @return the description of the active ability
	 */
	public String getActiveDescription() { return activeDesc; }
	
	/**
	 * @return the number of music tracks associated with this trainer
	 */
	public int getSongCount() { return nSongs; }
}
