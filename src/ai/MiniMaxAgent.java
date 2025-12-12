/**
 * File: MiniMaxAgent.java
 * Purpose:
 *      Implements an AI agent that selects optimal battle actions
 *      using the Minimax algorithm with Alpha-Beta pruning.
 */

package ai;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import action.Action;
import action.ActionType;
import action.AttackAction;
import action.ItemAction;
import action.SwitchAction;
import action.TrainerAction;
import battle.MoveCalculator;
import effects.Effect;
import effects.StunEffect;
import items.Item;
import models.Goober;
import models.GooberMove;
import models.Trainer;

/**
 * An AI agent that uses the Minimax algorithm with Alpha-Beta pruning
 * to determine the optimal move.
 * <p>
 * This agent simulates future game states by creating deep copies of
 * {@link Trainer} objects and applying theoretical actions. It
 * approximates damage, healing, status effects, and trainer abilities
 * to evaluate future outcomes.
 * </p>
 */
public class MiniMaxAgent {

    private final Difficulty difficulty;
    private final MoveCalculator calc = new MoveCalculator();

    /**
     * Constructs a MiniMaxAgent with a specified difficulty configuration.
     *
     * @param difficulty the difficulty settings for the AI
     */
    public MiniMaxAgent(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    /**
     * Determines the best action for the AI to take based on the current
     * game state.
     * <p>
     * Depending on the {@link Difficulty}, the agent may intentionally
     * select a random suboptimal move to simulate human-like mistakes.
     * </p>
     *
     * @param ai the AI-controlled trainer
     * @param player the human player opponent
     * @return the selected {@link Action}
     */
    public Action getBestAction(Trainer ai, Trainer player) {

        // Random error injection for lower difficulties
        if (new Random().nextDouble() < difficulty.randomMoveChance()) {
            return randomAction(ai);
        }

        MoveScore best = minimax(
                ai,
                player,
                difficulty.getDepth(),
                Integer.MIN_VALUE,
                Integer.MAX_VALUE,
                true
        );

        if (best.action == null) {
            return fallbackAction(ai);
        }

        return best.action;
    }

    /**
     * Generates a random valid action for the AI.
     *
     * @param ai the AI trainer
     * @return a random {@link Action}, or a fallback action if none exist
     */
    private Action randomAction(Trainer ai) {
        List<Action> actions = getPossibleActions(ai);
        if (actions.isEmpty()) {
            return fallbackAction(ai);
        }
        return actions.get(new Random().nextInt(actions.size()));
    }

    /**
     * Provides a safe fallback action when no valid moves are found.
     *
     * @param t the trainer taking the action
     * @return a {@link SwitchAction} if forced, otherwise a basic {@link AttackAction}
     */
    private Action fallbackAction(Trainer t) {
        if (t.getActiveGoober().isFainted()) {
            for (int i = 0; i < t.getTeam().size(); i++) {
                if (!t.getTeam().get(i).isFainted()) {
                    return new SwitchAction(t, i);
                }
            }
        }
        return new AttackAction(t, 0);
    }

    /**
     * Recursive Minimax algorithm with Alpha-Beta pruning.
     *
     * @param ai the AI trainer
     * @param player the player trainer
     * @param depth remaining search depth
     * @param alpha best value the maximizer can guarantee
     * @param beta best value the minimizer can guarantee
     * @param isMax true if it is the AI's turn
     * @return a {@link MoveScore} containing the best move and score
     */
    private MoveScore minimax(Trainer ai, Trainer player, int depth, int alpha, int beta, boolean isMax) {

        // Terminal or leaf node
        if (depth == 0 || !ai.hasAvailableGoobers() || !player.hasAvailableGoobers()) {
            return new MoveScore(null, BattleHeuristic.eval(ai, player));
        }

        Trainer activeTrainer = isMax ? ai : player;

        // Forced switch handling
        if (activeTrainer.getActiveGoober().isFainted()) {
            List<Action> forced = getForcedSwitchActions(activeTrainer);
            if (forced.isEmpty()) {
                return new MoveScore(null, BattleHeuristic.eval(ai, player));
            }
            return evaluateMoves(forced, ai, player, depth, alpha, beta, isMax);
        }

        return evaluateMoves(getPossibleActions(activeTrainer), ai, player, depth, alpha, beta, isMax);
    }

    /**
     * Evaluates all candidate moves and applies Alpha-Beta pruning.
     *
     * @param moves list of legal actions
     * @param ai AI trainer state
     * @param player player trainer state
     * @param depth remaining search depth
     * @param alpha best maximizer guarantee
     * @param beta best minimizer guarantee
     * @param isMax true if maximizing
     * @return the best {@link MoveScore} found
     */
    private MoveScore evaluateMoves(
            List<Action> moves,
            Trainer ai,
            Trainer player,
            int depth,
            int alpha,
            int beta,
            boolean isMax) {

        Action bestAction = null;
        int bestVal = isMax ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (Action action : moves) {

            Trainer nextAi = ai.copy();
            Trainer nextPlayer = player.copy();

            if (isMax) {
                applySimulatedAction(action, nextAi, nextPlayer);
            } else {
                applySimulatedAction(action, nextPlayer, nextAi);
            }

            MoveScore result = minimax(nextAi, nextPlayer, depth - 1, alpha, beta, !isMax);

            if (isMax) {
                if (result.score > bestVal) {
                    bestVal = result.score;
                    bestAction = action;
                }
                alpha = Math.max(alpha, bestVal);
            } else {
                if (result.score < bestVal) {
                    bestVal = result.score;
                    bestAction = action;
                }
                beta = Math.min(beta, bestVal);
            }

            if (beta <= alpha) {
                break;
            }
        }

        return new MoveScore(bestAction, bestVal);
    }

    /**
     * Determines all valid actions a trainer may perform.
     *
     * @param t the active trainer
     * @return a list of possible {@link Action}s
     */
    private List<Action> getPossibleActions(Trainer t) {
        List<Action> actions = new ArrayList<>();
        Goober active = t.getActiveGoober();

        if (!active.getState().isStunned()) {
            for (int i = 0; i < active.getUsableMoves().size(); i++) {
                actions.add(new AttackAction(t, i));
            }
        }

        List<String> seenItems = new ArrayList<>();
        for (Item item : t.getInventory()) {
            if (!seenItems.contains(item.getName())) {
                actions.add(new ItemAction(t, t, item.getName()));
                seenItems.add(item.getName());
            }
        }

        for (int i = 0; i < t.getTeam().size(); i++) {
            if (i != t.getActiveIndex() && !t.getTeam().get(i).isFainted()) {
                actions.add(new SwitchAction(t, i));
            }
        }

        if (!t.hasUsedAbility()) {
            actions.add(new TrainerAction(t));
        }

        return actions;
    }

    /**
     * Generates forced switch actions when the active goober is fainted.
     *
     * @param t the trainer who must switch
     * @return a list of {@link SwitchAction}s
     */
    private List<Action> getForcedSwitchActions(Trainer t) {
        List<Action> actions = new ArrayList<>();
        for (int i = 0; i < t.getTeam().size(); i++) {
            if (!t.getTeam().get(i).isFainted()) {
                actions.add(new SwitchAction(t, i));
            }
        }
        return actions;
    }

    /**
     * Simulates the execution of an action on copied trainer states.
     *
     * @param action the action to simulate
     * @param actor the acting trainer
     * @param enemy the opposing trainer
     */
    private void applySimulatedAction(Action action, Trainer actor, Trainer enemy) {

        if (action == null) return;

        if (action.getType() == ActionType.ATTACK) {

            AttackAction atk = (AttackAction) action;
            Goober ag = actor.getActiveGoober();
            Goober dg = enemy.getActiveGoober();
            GooberMove move = ag.getMove(atk.getMoveIndex());

            int predictedDamage = calc.calculateDamage(ag, dg, move, false);
            double critChance = Math.min(ag.getCritChance() + move.getCritChance(), 1.0);
            int expectedDamage = (int) (predictedDamage * move.getHitChance() * (1 + 0.5 * critChance));

            dg.takeDamage(expectedDamage);

            if (move.getEffect() != null && move.getHitChance() > 0.5) {
                Effect e = move.getEffect().copy();
                e.setTarget(dg);
                dg.addEffect(e);
                e.apply();
            }

        } else if (action.getType() == ActionType.USE_ITEM) {

            ItemAction ia = (ItemAction) action;
            Item item = actor.consumeItem(ia.getItemName());
            if (item != null) {
                enemy.getActiveGoober().heal((int) item.getMagnitude());
            }

        } else if (action.getType() == ActionType.SWITCH) {

            actor.switchActive(((SwitchAction) action).getNewIndex());

        } else if (action.getType() == ActionType.TRAINER_ABILITY) {

            actor.setAbilityUsed(true);
        }
    }

    /**
     * Small helper class representing an evaluated move and its score.
     */
    private static class MoveScore {
        Action action;
        int score;

        MoveScore(Action action, int score) {
            this.action = action;
            this.score = score;
        }
    }
}
