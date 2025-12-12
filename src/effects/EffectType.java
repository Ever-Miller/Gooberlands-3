/**
 * File: EffectType.java
 * Purpose:
 *      Defines the different categories of effects that can be
 *      applied to a Goober during battle.
 */

package effects;

/**
 * Enumerates the different kinds of effects that can be applied.
 * <p>
 * Effect types are used by the effect system to determine how an
 * {@link Effect} behaves, including stat modifications, status
 * conditions, and healing effects.
 * </p>
 */
public enum EffectType {

    /** Damage over time effect applied each turn. */
    POISON("Poison"),

    /** Prevents the target from acting for a turn. */
    STUN("Stun"),

    /** Applies accuracy or action penalties to the target. */
    DIZZY("Dizzy"),

    /** Restores health to the target. */
    HEAL("Heal"),

    /** Temporarily modifies the damage dealt by the target. */
    DAMAGE_MODIFICATION("Damage Modification"),

    /** Temporarily modifies the defence value of the target. */
    DEFENCE_MODIFICATION("Defence Modification"),

    /** Temporarily modifies the critical strike chance of the target. */
    CRIT_MODIFICATION("Crit Modification");

    /** Human-readable display name for this effect type. */
    private final String name;

    /**
     * Constructs an effect type with a display name.
     *
     * @param name the human-readable name of the effect
     */
    EffectType(String name) {
        this.name = name;
    }

    /**
     * Returns the display name of the effect type.
     *
     * @return the effect type name
     */
    public String getName() {
        return name;
    }
}
