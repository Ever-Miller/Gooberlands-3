package battle;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import effects.TargetType;
import models.Goober;
import models.GooberMove;
import models.GooberType;

class MoveCalculatorTest {
	private final MoveCalculator calculator = new MoveCalculator();
	
	/**
	 * Create a very simple Goober with level 1 stats.
	 * Growth values are set to 0 so the stats stay equal to the base values.
	 */
	private Goober createGoober(String name,int baseHp,int baseAttack,double baseCrit,double baseDefence) {
		return new Goober(name, GooberType.DAMAGER, baseHp,baseAttack,baseCrit,baseDefence,0,0, 0.0, 0.0, 1) ;
		}
	
	/*** Helper to create a simple move with no side effect.*/
	private GooberMove createMove(String name,int damage,double hitChance,TargetType type, double critChance) {
		return new GooberMove(name,damage,null,hitChance,critChance, type, 1);
	}
	
	@Test
	void moveHits_alwaysTrueWhenHitChanceIsOne() {
		Goober attacker = createGoober("Attacker", 100, 10, 0.0, 0.0);
		Goober defender = createGoober("Defender", 100, 5, 0.0, 0.0);
		GooberMove move  = createMove("Never Miss", 10, 1.0, TargetType.ENEMY, 0.0);
		
		// Because MoveCalculator uses Math.random() in [0,1), any roll will be <= 1.0, so this should always hit.
		
		for (int i = 0; i < 100; i++) {
			assertTrue(calculator.moveHits(attacker, defender, move),"Move with hitChance=1.0 should always hit");
		}
	}
	
	@Test
	void isCritical_alwaysTrueWhenTotalCritChanceAtLeastOne() {
		// Base crit = 1.0 (100%), move crit bonus = 0.5 (50%) -> capped to 1.0 internally.
		Goober attacker = createGoober("Crit Lord", 100, 10, 1.0, 0.0);
		GooberMove move  = createMove("Overkill", 10, 1.0, TargetType.ENEMY,  0.5);
		
		for (int i = 0; i < 50; i++) {
			assertTrue(calculator.isCritical(attacker, move),"Total crit chance >= 1.0 should always result in a critical hit");
		}
	}
		
	@Test
	void calculateDamage_nonCritical_respectsAttackAndDefence() {
		// attacker attack = 10, defender defence = 0.2, move damage = 20
		// rawDamage       = 10 + 20 = 30
		// afterDefense    = 30 * (1 - 0.2) = 24
		Goober attacker = createGoober("Attacker", 100, 10, 0.0, 0.0);
		Goober defender = createGoober("Defender", 100, 5, 0.0, 0.2);
		GooberMove move  = createMove("Solid Hit", 20, 1.0, TargetType.ENEMY, 0.0);
		int damage = calculator.calculateDamage(attacker, defender, move, false);
		
		assertEquals(24, damage,"Non-critical damage should follow rawDamage * (1 - defence)");
	}
	
	@Test
	void calculateDamage_critical_appliesOnePointFiveMultiplier() {
		// attacker attack = 10, defender defence = 0.0, move damage = 10
		// rawDamage       = 10 + 10 = 20
		// afterDefense    = 20 * (1 - 0.0) = 20
		// critical        = 20 * 1.5 = 30
		Goober attacker = createGoober("Attacker", 100, 10, 0.0, 0.0);
		Goober defender = createGoober("Defender", 100, 5, 0.0, 0.0);
		GooberMove move  = createMove("Crit Hit", 10, 1.0, TargetType.ENEMY, 0.0);
		int damage = calculator.calculateDamage(attacker, defender, move, true);
		
		assertEquals(30, damage,"Critical damage should be 1.5x the non-critical damage (after defence)");
	}
		
	@Test
	void calculateDamage_neverReturnsLessThanOne() {
		// Make defence extremely high so rawDamage * (1 - defence) rounds to 0.
		// attacker attack = 1, defender defence = 0.99, move damage = 1
		// rawDamage       = 1 + 1 = 2
		// afterDefense    = 2 * (1 - 0.99) = 0.02 -> rounds to 0, then clamped to 1
		Goober attacker = createGoober("Weakling", 100, 1, 0.0, 0.0);
		Goober defender = createGoober("Tank", 100, 5, 0.0, 0.99);
		GooberMove move  = createMove("Tickle", 1, 1.0, TargetType.ENEMY, 0.0);
		int damage = calculator.calculateDamage(attacker, defender, move, false);
		assertEquals(1, damage,"Damage should never be less than 1 even with very high defence");
	}
	
}
