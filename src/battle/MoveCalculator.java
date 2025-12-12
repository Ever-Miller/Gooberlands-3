/**
 * File: MoveCalculator
 * Purpose:
 * 		Handles all damage, accuracy, and critical-hit calculations for battle moves.
 * 		The {@code MoveCalculator} is stateless and may be reused across multiple battles.
 * 
 * <p> The formulas used here define the core combat mechanics of the game:
 * <ul> 
 * <li> Accuracy rolls determine whether a move hits </li> 
 * <li> Critical hit rolls determine bonus damage </li> 
 * <li> Damage is based on attacker stats, move damage, and defender defence </li> 
 * </ul> 
 */
package battle;

import models.Goober;
import models.GooberMove;

public class MoveCalculator {
	
    /**
     * Determines whether a move successfully hits the target.
     * 
     * @param attacker		the attacking Goober 
     * @param defender		the defending Goober
     * @param move			the move being used
     * 
     * @return true if the move hits, false otherwise     
     */
	public boolean moveHits(Goober attacker, Goober defender, GooberMove move) {
		float roll = (float) Math.random();
		return roll <= move.getHitChance();
	}
	
    /** 
     * Determines whether a move results in a critical hit.
     * The critical chance is computed as:
     * <pre>
     * 		totalCritChance = attackerBaseCrit + moveCritBonus
     * </pre>
     * and is capped at 100%.  
     * 
     * @param attacker		the attacking Goober
     * @param move			the move being used 
     * 
     * @return true if the move is a critical hit     
     */
	public boolean isCritical(Goober attacker, GooberMove move) {
		float roll = (float) Math.random();
		double totalCritChance = attacker.getCritChance() + move.getCritChance();
		return roll <= Math.min(totalCritChance, 1.0);
	}
	
    /**
     * Computes the amount of damage dealt by a move.
     * The formula is:
     * <pre>
     * 		rawDamage = moveDamage + attackerAttack
     * 		damageAfterDefense = rawDamage * (1 - defenderDefense)
     * 		if critical: damageAfterDefense *= 1.5
     * </pre>
     * 
     * Minimum damage is always 1.
     * 
     * @param attacker		the attacking Goober
     * @param defender		the defending Goober
     * @param move			the move being executed
     * @param critical		whether the attack is a critical hit
     * 
     * @return the integer damage dealt, minimum of 1     */
	public int calculateDamage(Goober attacker, Goober defender, GooberMove move, boolean critical) {
		int baseDamage = move.getDamage();
		if (baseDamage == 0) {
			return 0;
		}
		
		int rawDamage = baseDamage + attacker.getAttack();
		
		double reduction = defender.getDefence();
		double afterDefense = rawDamage * (1.0 - reduction);
		
		if (critical) {
			afterDefense *= 1.5;
		}
		
		int damage = (int) Math.round(afterDefense);
		return Math.max(1, damage);
		
	}

}
