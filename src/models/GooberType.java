/**
 * File: GooberType.java
 * Purpose:
 *      Defines the core stat archetypes for all Goober species.
 *
 *      Each GooberType represents a gameplay role (Tank, Assassin, Support, etc.)
 *      and provides:
 *      - Base stats (HP, Attack, Defence, Crit chance)
 *      - Per-level growth values for scaling
 *
 *      These values are used by {@link GooberFactory} when constructing
 *      {@link Goober} instances and serve as the central balancing point
 *      for the combat system.
 */

package models;


/**
 * Enumerates the core combat archetypes for {@link Goober} entities.
 * <p>
 * Each {@code GooberType} defines:
 * <ul>
 *   <li>Base combat statistics</li>
 *   <li>Per-level growth rates</li>
 * </ul>
 * These values are used by {@link GooberFactory} to construct balanced
 * Goober instances while keeping role-based behavior consistent.
 * </p>
 *
 * <p>
 * <b>Design Note:</b> Growth values are intentionally small to support
 * longer campaigns without runaway stats.
 * </p>
 */
public enum GooberType {
	//  	 HP   ATK  DEF    CRIT   HP_G  ATK_G   DEF_G    CRIT_G
	TANK(    110, 15,  0.15,  0.05,   14,   2,     0.0020,  0.0005),
	ASSASSIN(60,  28,  0.05,  0.20,   7,    6,     0.0008,  0.0020),
	DAMAGER( 80,  22,  0.10,  0.10,   9,    4,     0.0012,  0.0010),
	SUPPORT( 90,  14,  0.12,  0.05,   11,   2,     0.0015,  0.0005),
	SPECIAL( 70,  20,  0.08,  0.15,   8,    5,     0.0010,  0.0015),
	
	LOPUNNY( 180, 35,  0.10,  0.20,   20,   8,     0.0015,  0.0015);

    public final int baseHp;
    public final int baseAttack;
    public final double baseDefence;
    public final double baseCrit;
    
    public final int hpGrowth;
    public final int attackGrowth;
    public final double defenceGrowth;
    public final double critGrowth;

    /**
	 * Constructs a {@code GooberType} with base stats and scaling values.
	 *
	 * @param hp      base health points
	 * @param atk     base attack power
	 * @param def     base defence percentage
	 * @param crit    base critical hit chance
	 * @param hpG     health growth per level
	 * @param atkG    attack growth per level
	 * @param defG    defence growth per level
	 * @param critG   critical chance growth per level
	 */
    GooberType(int hp, int atk, double def, double crit, int hpG, int atkG, double defG, double critG) {
        this.baseHp = hp;
        this.baseAttack = atk;
        this.baseDefence = def;
        this.baseCrit = crit;
        this.hpGrowth = hpG;
        this.attackGrowth = atkG;
        this.defenceGrowth = defG;
        this.critGrowth = critG;
    }
}
