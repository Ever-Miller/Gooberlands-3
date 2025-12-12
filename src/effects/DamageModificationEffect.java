/**
 * File: DamageModificationEffect.java
 * Purpose:
 *      Represents a temporary modification to a Goober’s damage output
 *      through a stat-based effect.
 */

package effects;

import models.Goober;

/**
 * An effect that modifies a Goober's damage output.
 * <p>
 * This effect increases or decreases the amount of damage
 * a Goober deals for a fixed duration and is implemented
 * as a {@link StatModificationEffect}.
 * </p>
 */
public class DamageModificationEffect extends StatModificationEffect {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new damage modification effect with a specified target.
     *
     * @param duration the number of turns the effect lasts
     * @param target the {@link Goober} affected by this effect
     * @param strength the amount to increase or decrease
     *                 damage output by
     */
    public DamageModificationEffect(int duration, Goober target, double strength) {
        super(EffectType.DAMAGE_MODIFICATION, duration, target, strength);
    }

    /**
     * Creates a new damage modification effect without an initial target.
     * <p>
     * {@link #setTarget(Goober)} must be called before this
     * effect is applied.
     * </p>
     *
     * @param duration the number of turns the effect lasts
     * @param strength the amount to increase or decrease
     *                 damage output by
     */
    public DamageModificationEffect(int duration, double strength) {
        this(duration, null, strength);
    }
}
