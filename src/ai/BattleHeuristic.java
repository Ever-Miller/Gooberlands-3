/**
 * File: BattleHeuristic.java
 * Purpose:
 *      Provides a static utility for evaluating battle states
 *      from the AI's perspective using a weighted heuristic.
 */

package ai;

import items.Item;
import models.Goober;
import models.Trainer;

/**
 * A static utility class that calculates the relative advantage
 * of a game state for the AI.
 * <p>
 * This heuristic function is used for the {@link MiniMaxAgent}'s
 * MiniMax tree search. It produces a single integer score where
 * a higher value indicates a better situation for the AI, and
 * a lower value indicates a better situation for the player.
 * </p>
 */
public class BattleHeuristic {

    /**
     * Evaluates the current state of the battle from the perspective of the AI.
     * <p>
     * The score is calculated as a weighted linear combination of:
     * <ul>
     * <li><b>Win/Loss Condition:</b> +/- 1,000,000</li>
     * <li><b>Alive Goober Count:</b> 2,000 points per unit advantage</li>
     * <li><b>Status Effects:</b> +/- 300 for Stun, +/- 100 for Dizziness</li>
     * <li><b>Items:</b> 50 points per unit of item value</li>
     * <li><b>Health:</b> Adjustments based on team and active goober HP</li>
     * </ul>
     * </p>
     *
     * @param ai the AI-controlled trainer
     * @param player the human player opponent
     * @return an integer score favoring the AI when higher
     */
    public static int eval(Trainer ai, Trainer player) {
        int score = 0;

        // Terminal States
        if (!player.hasAvailableGoobers()) return Integer.MAX_VALUE;
        if (!ai.hasAvailableGoobers()) return Integer.MIN_VALUE;

        score += (countAlive(ai) - countAlive(player)) * 2000;
        score += (getTotalHpPercent(ai) - getTotalHpPercent(player)) * 5;
        score += (countAvailableItems(ai) - countAvailableItems(player)) * 50;

        Goober aiActive = ai.getActiveGoober();
        Goober playerActive = player.getActiveGoober();

        if (aiActive != null && playerActive != null) {
            score += (aiActive.getCurrentHp() - playerActive.getCurrentHp()) * 2;

            if (aiActive.getState().isStunned()) score -= 300;
            if (playerActive.getState().isStunned()) score += 300;

            if (aiActive.getState().isDizzy()) score -= 100;
            if (playerActive.getState().isDizzy()) score += 100;

            score += (aiActive.getAttack() - playerActive.getAttack()) * 5;
            score += (aiActive.getDefence() - playerActive.getDefence()) * 500;
            score += (aiActive.getCritChance() - playerActive.getCritChance()) * 250;
        }

        return score;
    }

    /**
     * Counts the number of goobers on a trainer's team that have not fainted.
     *
     * @param t the trainer to evaluate
     * @return the number of available goobers
     */
    private static int countAlive(Trainer t) {
        return (int) t.getTeam().stream().filter(g -> !g.isFainted()).count();
    }

    /**
     * Calculates total team health as a percentage of maximum health.
     *
     * @param t the trainer to evaluate
     * @return an integer health percentage (0–100)
     */
    private static int getTotalHpPercent(Trainer t) {
        int totalCurrent = 0;
        int totalMax = 0;

        for (Goober g : t.getTeam()) {
            totalCurrent += g.getCurrentHp();
            totalMax += g.getMaxHp();
        }

        if (totalMax == 0) return 0;
        return (int) (((double) totalCurrent / totalMax) * 100);
    }

    /**
     * Computes the heuristic value of a trainer's inventory
     * based on item cost.
     *
     * @param t the trainer to evaluate
     * @return the total item cost value
     */
    private static int countAvailableItems(Trainer t) {
        int total = 0;
        for (Item i : t.getInventory()) {
            total += i.getCost();
        }
        return total;
    }
}
