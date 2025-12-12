/**
 * File: StatModificationEffect.java
 * Purpose:
 *      Provides a base implementation for effects that temporarily
 *      modify a Goober’s combat statistics.
 */

package effects;

import models.Goober;

/**
 * An abstract stat-based effect that applies a temporary modification
 * to a Goober's statistics.
 * <p>
 * This effect applies its stat change once and automatically reverts
 * the modification when its duration expires.
 * </p>
 */
public class StatModificationEffect extends Effect {

    private static final long serialVersionUID = 1L;

    /** Tracks whether the stat modification has already been applied. */
    private boolean applied = false;

    /** The effective stat change actually applied to the target. */
    private double effectiveChange;

    /**
     * Constructs a stat modification effect with a specified target.
     *
     * @param type the type of stat modification
     * @param duration the number of turns the effect lasts
     * @param target the {@link Goober} affected by this effect
     * @param strength the magnitude of the stat change
     */
    public StatModificationEffect(
            EffectType type,
            int duration,
            Goober target,
            double strength) {

        super(type, duration, target, strength);
    }

    /**
     * Constructs a stat modification effect without an initial target.
     * <p>
     * {@link #setTarget(Goober)} must be called before the
     * effect is applied.
     * </p>
     *
     * @param type the type of stat modification
     * @param duration the number of turns the effect lasts
     * @param strength the magnitude of the stat change
     */
    public StatModificationEffect(EffectType type, int duration, double strength) {
        this(type, duration, null, strength);
    }

    /**
     * Applies the stat modification effect.
     * <p>
     * The stat change is applied once and remains active until the
     * duration expires, at which point the change is reverted
     * automatically.
     * </p>
     *
     * @return {@code true} if the effect has expired and should be removed,
     *         {@code false} otherwise
     * @throws IllegalStateException if the effect is applied without a target
     */
    @Override
    public boolean apply() {
        if (target == null) {
            throw new IllegalStateException(
                    this.getClass().getName() + " applied with null target"
            );
        }

        if (!applied) {
            effectiveChange = target.getState().incrStat(type, strength);
            applied = true;
        }

        duration -= 1;

        if (duration <= 0) {
            target.getState().revertStatChange(type, effectiveChange);
            return true;
        }

        return false;
    }

    /**
     * Creates a copy of this stat modification effect.
     *
     * @return a new {@link StatModificationEffect} with the same
     *         type, duration, and strength
     */
    @Override
    public Effect copy() {
        return new StatModificationEffect(this.type, this.duration, this.strength);
    }
}
