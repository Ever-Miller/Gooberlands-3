/**
 * File: MoveFactory.java
 * A factory class responsible for creating the list of moves associated with a specific {@link Goober} species.
 * <p>
 * Each Goober is identified by its display {@code name}, and receives a* predefined set of {@link GooberMove} objects based on that identity.
 * </p>
 * 
 * <p>
 * The move sets returned by this factory should be treated as immutable by callers; 
 * if modification is needed (for example, to add or remove moves at runtime), callers should create a defensive copy.
 * </p>
 * 
 * <p>
 * If a Goober species has not yet been assigned moves, this factory returns an* empty list rather than {@code null}. 
 * This avoids null checks and keeps the behavior consistent with how {@link Goober#getUsableMoves()} handles move lists.
 * </p>
 * 
 * <p>
 * The typical usage pattern is:
 * </p>
 * 
 * <pre>
 * {@code
 * Goober g = GooberFactory.getGoober("DaBaby", 5);
 * List<GooberMove> moves = MoveFactory.getMoves(g.getName());
 * }
 * </pre>
 * 
 * @author: 
 */
package models;

import java.util.ArrayList;
import effects.*;

public class MoveFactory {
	/**
	 * Returns a list of all moves available to the specified Goober species.
	 * <p>
	 * These moves may require a minimum level before they become usable in battle (see {@link GooberMove#getUnlockLevel()}). 
	 * The returned list is a* newly-created {@link ArrayList}, so callers may modify it if necessary without affecting the factory's internal definitions.
	 * </p>
	 * 
	 * <p>
	 * Move definitions are grouped in the switch statement by Goober display name.
	 * Any name not explicitly handled results in an empty move list.
	 * </p>
	 * 
	 * @param name the exact display name of the Goober requesting moves
	 * @return a list of moves for the given Goober species; never {@code null}
	 */
	public static ArrayList<GooberMove> getMoves(String name) {
		ArrayList<GooberMove> moves = new ArrayList<>();
		
		switch (name) {
		
		// SUPPORTS 
		
		// GooberMove(name, damage, effect, hitchance, critChance, unlockLevel)
		
		// Early-game heal + defensive support
		case "Tralalero Tralala":
			moves.add(new GooberMove("Fanum Tax", 30, null, 1.0, 0.05, TargetType.ENEMY, 0));
			moves.add(new GooberMove("Hawk Tuah", 0, new HealEffect(1, 0.3), 1.0, 0.0, TargetType.ALL_ALLIES, 0));
			moves.add(new GooberMove("Goon Cave",  0, new DefenceModificationEffect(3, 0.3), 1.0, 0.0, TargetType.ALL_ALLIES, 10));
			moves.add(new GooberMove("Brain Rot", 20, new DamageModificationEffect(2, -0.2), 1.0, 0.0, TargetType.ENEMY, 15));
			break;
		
		// Support with stun
		case "Boss Baby":
			moves.add(new GooberMove("LinkedIn Maxxing", 0, new DamageModificationEffect(2, 0.2), 0.9, 0.05, TargetType.SELF, 0));
			moves.add(new GooberMove("Passive Income", 0, new HealEffect(1, 0.3), 1.0, 0.0, TargetType.SELF, 0));
			moves.add(new GooberMove("Go to the Mines", 10, new DefenceModificationEffect(3, -0.2), 0.95, 0.0, TargetType.ALL_ENEMIES, 10));
			moves.add(new GooberMove("Tax Evasion", 0, new StunEffect(1, 0), 0.8, 0.0, TargetType.ENEMY, 15));
			break;
			
			
		// TANKS 
		// High defence tank with stun potential
		case "DaBaby":
			moves.add(new GooberMove("LESS GOOO!", 0, new DefenceModificationEffect(3, 0.5), 1.0, 0.0, TargetType.SELF, 0));
			moves.add(new GooberMove("DaConvertible", 50, new StunEffect(1, 0), 0.85, 0.05, TargetType.ENEMY, 0));
			moves.add(new GooberMove("Pull Up", 35, null, 1.0, 0.05, TargetType.ENEMY, 10));
			moves.add(new GooberMove("Slime Time", 0, new PoisonEffect(3, 0.05), 0.7, 0.0, TargetType.ENEMY, 15));
			break;
		
		// Very defensive tank with sustain and light damage
		case "Doge":
			moves.add(new GooberMove("Such Defence", 0, new DefenceModificationEffect(3, 0.6), 1.0, 0.0, TargetType.SELF, 0));
			moves.add(new GooberMove("Bonk", 30, null, 1.0, 0.0, TargetType.ENEMY, 0));
			moves.add(new GooberMove("Bonk 2 (The Sequel)", 60, null, 0.85, 0.1, TargetType.ENEMY, 10));
			moves.add(new GooberMove("Much Heal", 0, new HealEffect(1, 0.3), 1.0, 0.0, TargetType.SELF, 15));
			break;
			
			
		// DAMAGE DEALERS 
		// Burst damage dealer with self/target damage modification	
		case "Peter Griffin":
			moves.add(new GooberMove("Bird Is The Word", 70, null, 0.75, 0.2, TargetType.ENEMY, 0));
			moves.add(new GooberMove("Shut Up Meg", 0, new DamageModificationEffect(3, 0.4), 1.0, 0.0, TargetType.SELF, 0));
			moves.add(new GooberMove("Slightly Offensive Joke", 50, null, 0.9, 0.05, TargetType.ENEMY, 10));
			moves.add(new GooberMove("Cutaway Gag", 30, new CritModificationEffect(2, 0.3), 0.9, 0.1, TargetType.SELF, 15));
			break;
		
		// Heavy nukes and defence shredding
		case "Tung Tung Tung Sahur":
			moves.add(new GooberMove("Sahur Blast", 90, null, 0.70, 0.15, TargetType.ENEMY, 0));
			moves.add(new GooberMove("WAKE UP!!", 10, new DefenceModificationEffect(2, -0.5), 1.0, 0.0, TargetType.ENEMY, 0));
			moves.add(new GooberMove("Pan Banging", 40, null, 0.95, 0.1, TargetType.ENEMY, 10));
			moves.add(new GooberMove("Steal Your Girl", 0, new StunEffect(1, 0), 0.75, 0.0, TargetType.ENEMY, 15));
			break;
			
		// ASSASSINS
		// Crit-focused assassin with dizzy debuff
		case "John Pork":
			moves.add(new GooberMove("Pick Up The Phone", 0, new CritModificationEffect(2, 0.5), 1.0, 0.0, TargetType.SELF, 0));
			moves.add(new GooberMove("Left On Read", 10, new DizzyEffect(1, 0.2), 1.0, 0.0, TargetType.ENEMY, 0));
			moves.add(new GooberMove("Oink Strike", 60, null, 0.9, 0.3, TargetType.ENEMY, 10));
			moves.add(new GooberMove("Baconator", 40, null, 0.85, 0.5, TargetType.ENEMY, 15));
			break;
		
		// High-pressure assassin: big crits and damage debuffs
		case "Duolingo Bird":
			moves.add(new GooberMove("Missed Lesson", 65, null, 0.85, 0.4, TargetType.ENEMY, 0));
			moves.add(new GooberMove("Spanish Or Vanish", 20, new DamageModificationEffect(2, -0.3), 1.0, 0.0, TargetType.ENEMY, 0));
			moves.add(new GooberMove("Kidnap Family", 80, null, 0.7, 0.1, TargetType.ENEMY, 10));
			moves.add(new GooberMove("No Remorse", 0, new StunEffect(1, 0), 0.6, 0.0, TargetType.ENEMY, 15));
			break;
			
		// Crit freak
		case "William Shakespear":
			moves.add(new GooberMove("Thou Art Cringe", 0, new CritModificationEffect(3, 0.5), 1.0, 0.0, TargetType.SELF, 0));
			moves.add(new GooberMove("Poetic Justice", 45, null, 0.95, 0.1, TargetType.ENEMY, 0));
			moves.add(new GooberMove("No Maidens?", 10, new DefenceModificationEffect(3, -0.25), 0.90, 0.0, TargetType.ENEMY, 10));
			moves.add(new GooberMove("Ratio + L + Bozo", 70, new DizzyEffect(1, 0.3), 0.85, 0.2, TargetType.ALL_ENEMIES, 15));
			break;
			
		// SPECIALS 
		// Long winded fights
		case "Mahoraga":
			moves.add(new GooberMove("Big Raga The Opp Stoppa", 50, null, 0.9, 0.1, TargetType.ENEMY, 0));
			moves.add(new GooberMove("Nah, I'd Adapt", 0, new DefenceModificationEffect(3, 0.5), 1.0, 0.0, TargetType.SELF, 0));
			moves.add(new GooberMove("Malevolent Shrine", 70, new DefenceModificationEffect(1, -0.4),  0.88, 0.15, TargetType.ENEMY, 10));
			moves.add(new GooberMove("With This Treasure", 40, new StunEffect(2, 0), 0.85, 0.1, TargetType.ENEMY, 15));
			break;
		
		// Stun, strong damage buffs, and dizzy utility
		case "LeBron":
			moves.add(new GooberMove("You Are My Sunshine", 60, new StunEffect(1, 0), 0.80, 0.15, TargetType.ENEMY, 0));
			moves.add(new GooberMove("The GOAT", 0, new DamageModificationEffect(3, 0.4), 1.0, 0.0, TargetType.SELF, 0));
			moves.add(new GooberMove("Free Throw", 50, null, 0.95, 0.2, TargetType.ENEMY, 10));
			moves.add(new GooberMove("Sprite Cranberry", 0, new HealEffect(1, 0.3), 1.0, 0.0, TargetType.SELF, 15));
			break;
		
		// Buffs own damage, shreds enemy defence, and can stun
		case "Gigachad":
			moves.add(new GooberMove("Sigma Grindset", 0, new DamageModificationEffect(3, 0.5), 1.0, 0.0, TargetType.SELF, 0));
			moves.add(new GooberMove("Mewing Streak", 70, new DefenceModificationEffect(2, -0.4), 0.85, 0.2, TargetType.ENEMY, 0));
			moves.add(new GooberMove("Can You Feel My Heart", 30, new StunEffect(1, 0), 0.9, 0.05, TargetType.ENEMY, 10));
			moves.add(new GooberMove("Average Health Enjoyer", 0, new HealEffect(1, 0.4), 1.0, 0.0, TargetType.SELF, 15));
			break;
		
		// Boss-only assassin: high damage, debuffs, and a risky finisher.
		case "Lopunny":
			moves.add(new GooberMove("There's No Laws", 50, new CritModificationEffect(3, 0.3), 1.0, 0.2, TargetType.SELF, 0));
			moves.add(new GooberMove("Batman I caught a...", 0, new DefenceModificationEffect(2, -0.4), 1.0, 0.0, TargetType.ENEMY, 0));
			moves.add(new GooberMove("Thirst Trap", 90, new DizzyEffect(1, 0.2), 0.8, 0.3, TargetType.ENEMY, 10));
			moves.add(new GooberMove("OnlyFans Link", 60, null, 0.9, 0.1, TargetType.ENEMY, 15));
			break;
			
		default:
			// Unknown Goober returns an empty list to avoid null checks.
			break;
		}
		return moves;
	}
	
	// Prevent instantiation
	private MoveFactory() {}
}