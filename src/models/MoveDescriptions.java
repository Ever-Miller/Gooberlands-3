/**
 * File: MoveDescriptions.java
 * Purpose:
 *      Stores human-readable descriptions for all Goober moves.
 *
 *      This class functions as a centralized lookup table mapping
 *      move names (Strings) to their descriptive flavor text.
 *      These descriptions are used by the UI layer for tooltips,
 *      move previews, and battle displays.
 *
 *      This class contains no gameplay logic and serves a
 *      presentation-only role.
 */

package models;

import java.util.HashMap;
import java.util.Map;

/**
 * Static utility class that provides flavor text descriptions
 * for Goober moves.
 */
public class MoveDescriptions {

	/**
	 * Internal mapping from move names to their descriptions.
	 */
	private static final Map<String, String> DESCRIPTIONS = new HashMap<>();
	
	/**
	 * Static initializer block that populates the move description table.
	 */
	static {
		// --- TRALALERO TRALALA ---
		DESCRIPTIONS.put("Fanum Tax", "Damage doubles if you yell \"GYAT\" irl. Deals damage.");
        DESCRIPTIONS.put("Hawk Tuah", "Spit on that thang! Heals all allies. Don't Ask why.");
        DESCRIPTIONS.put("Goon Cave", "Retreats to the cave. Increases ally Defence. Don't Ask why.");
        DESCRIPTIONS.put("Brain Rot", "Applies brainrot. We insist this is a real debuff. Lowers enemy Damage.");
        
        // --- BOSS BABY ---
        DESCRIPTIONS.put("LinkedIn Maxxing", "#Grindset. Increases own Damage based on how many connections you have.");
        DESCRIPTIONS.put("Passive Income", "Make money while you sleep. Heals self.");
        DESCRIPTIONS.put("Go to the Mines", "The children yearn for the mines. Lowers enemy Defence.");
        DESCRIPTIONS.put("Tax Evasion", "It's not wrong if you're never caught. Stuns the enemy.");
        
        // --- DABABY ---
        DESCRIPTIONS.put("LESS GOOO!", "Lock in. Increases own Defence.");
        DESCRIPTIONS.put("DaConvertible", "Deals damage by transforming into something nobody asked for.");
        DESCRIPTIONS.put("Pull Up", "Hop out at the afterparty. Deals damage.");
        DESCRIPTIONS.put("Slime Time", "Bust your slime all over the enemy, poison for 3 round.");
        
        // --- DOGE ---
        DESCRIPTIONS.put("Such Defence", "Very Tanky! Increases Defence.");
        DESCRIPTIONS.put("Bonk", "Bonk! Deals damage.");
        DESCRIPTIONS.put("Bonk 2 (The Sequel)", "Bonk was so successful we had to make a sequel. Deals massive damage.");
        DESCRIPTIONS.put("Much Heal", "Such wow, very healthy. Heals self.");
        
        // --- PETER GRIFFIN ---
        DESCRIPTIONS.put("Bird Is The Word", "Have you heard? Deals damage.");
        DESCRIPTIONS.put("Shut Up Meg", "Daughters are made to bully. Increases Damage.");
        DESCRIPTIONS.put("Slightly Offensive Joke", "HR has been notified. Deals damage.");
        DESCRIPTIONS.put("Cutaway Gag", "This is like that time in 2001 when... Increases Crit chance.");
        
        // --- TUNG TUNG TUNG SAHUR ---
        DESCRIPTIONS.put("Sahur Blast", "Waking up the whole neighborhood. Massive damage.");
        DESCRIPTIONS.put("WAKE UP!!", "Apologies to headphone users. Lowers enemy Defence.");
        DESCRIPTIONS.put("Pan Banging", "Deals damage proportional to the dents in the pan.");
        DESCRIPTIONS.put("Steal Your Girl", "Deals emotional crits (Very similar to regular crits, but there is a difference");
        
        // --- JOHN PORK ---
        DESCRIPTIONS.put("Pick Up The Phone", "The swine is calling, please pick up. Increases Crit chance.");
        DESCRIPTIONS.put("Left On Read", "Emotional damage. Makes enemy Dizzy (Lowers Accuracy).");
        DESCRIPTIONS.put("Oink Strike", "Coming up with these names is hard. Deals damage.");
        DESCRIPTIONS.put("Baconator", "Just like Wendys (sponsor us). Deals damage.");
        
        // --- DUOLINGO BIRD ---
        DESCRIPTIONS.put("Missed Lesson", "You know what happens now. Deals damage.");
        DESCRIPTIONS.put("Spanish Or Vanish", "Beg for your life en español. Lowers enemy Damage.");
        DESCRIPTIONS.put("Kidnap Family", "Duo is outside your house. High damage.");
        DESCRIPTIONS.put("No Remorse", "I'm not joking look behind you. Please. Stuns the enemy.");
        
        // --- WILLIAM SHAKESPEARE ---
        DESCRIPTIONS.put("Thou Art Cringe", "Thy is mid. Increases Crit chance.");
        DESCRIPTIONS.put("Poetic Justice", "I dont undertstand anything this guy says. Deals damage.");
        DESCRIPTIONS.put("No Maidens?", "Dost thou even pull? Lowers enemy Defence.");
        DESCRIPTIONS.put("Ratio + L + Bozo", "Fell off. Makes enemies Dizzy (Lowers Accuracy).");
        
        // --- MAHORAGA ---
        DESCRIPTIONS.put("Big Raga The Opp Stoppa", "Summons the divine general. Deals damage.");
        DESCRIPTIONS.put("Nah, I'd Adapt", "Whatever you do, I win. Increases Defence.");
        DESCRIPTIONS.put("Malevolent Shrine", "Domain Expansion: Defence Reduction. Reduces the enemies defence by 40%");
        DESCRIPTIONS.put("With This Treasure", "Summons something nasty. Stuns the enemy.");
        
        // --- LEBRON ---
        DESCRIPTIONS.put("You Are My Sunshine", "Our little ray of light... Stuns the enemy with glory.");
        DESCRIPTIONS.put("The GOAT", "If you don't agree, stop playing our game. Increases Damage.");
        DESCRIPTIONS.put("Free Throw", "He hits these every time, what a unit. God I just wanna... Deals damage.");
        DESCRIPTIONS.put("Sprite Cranberry", "Want a Sprite Cranberry? Answer wisely. Heals self.");
        
        // --- GIGACHAD ---
        DESCRIPTIONS.put("Sigma Grindset", "Reject modernity. Increase Damage.");
        DESCRIPTIONS.put("Mewing Streak", "I swear mewing does something. Lowers enemy Defence.");
        DESCRIPTIONS.put("Can You Feel My Heart", "Stuns the enemy because he feels like it");
        DESCRIPTIONS.put("Average Health Enjoyer", "Heals by simply existing.");
        
        // --- LOPUNNY ---
        DESCRIPTIONS.put("There's No Laws", "Batman, I can do whatever I want. Increases Crit.");
        DESCRIPTIONS.put("Batman I caught a...", "Oh no... Lowers enemy Defence.");
        DESCRIPTIONS.put("Thirst Trap", "It's a trap! Makes enemy Dizzy (Lowers Accuracy).");
        DESCRIPTIONS.put("OnlyFans Link", "The enemy went broke subscribing. Deals damage.");
	}
	
	/**
	 * Retrieves the description for a given move name.
	 *
	 * @param moveName the name of the move
	 * @return the description associated with the move, or a fallback message
	 *         if the move is not present in the table
	 */
	public static String getDescription(String moveName) {
		return DESCRIPTIONS.getOrDefault(moveName, "No description available. Skill issue?");
	}
}
