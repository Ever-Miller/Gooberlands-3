/**
 * File: DefenceModificationEffect.java
 * Purpose:
 *      Represents a temporary modification to a Goober’s defensive
 *      stat through a stat-based effect.
 */

package effects;

import models.Goober;

/**
 * An effect that modifies a Goober's defence value.
 * <p>
 * This effect increases or decreases the Goober’s defence
 * for a fixed number of turns and is implemented as a
 * {@link StatModificationEffect}.
 * </p>
 */
public class DefenceModificationEffect extends StatModificationEffect {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new defence modification effect with a specified target.
     *
     * @param duration the number of turns the effect lasts
     * @param target the {@link Goober} affected by this effect
     * @param strength the amount to increase or decrease
     *                 defence by
     */
    public DefenceModificationEffect(int duration, Goober target, double strength) {
        super(EffectType.DEFENCE_MODIFICATION, duration, target, strength);
    }

    /**
     * Creates a new defence modification effect without an initial target.
     * <p>
     * {@link #setTarget(Goober)} must be called before this
     * effect is applied.
     * </p>
     *
     * @param duration the number of turns the effect lasts
     * @param strength the amount to increase or decrease
     *                 defence by
     */
    public DefenceModificationEffect(int duration, double strength) {
        this(duration, null, strength);
    }
}
