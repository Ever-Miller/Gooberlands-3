/**
 * File: BattleController.java
 * Purpose:
 *      Acts as the controller for battle interactions.
 *      Handles player input, communicates with the BattleManager,
 *      updates the view, and processes battle outcomes and rewards.
 */

package controller;

import java.util.List;

import action.Action;
import action.AttackAction;
import action.ItemAction;
import action.SwitchAction;
import battle.BattleManager;
import battle.BattleTurnResult;
import game.GameBattleState;
import game.GameManager;
import game.Level;
import game.MapState;
import items.Item;
import models.Goober;
import models.Trainer;
import models.TrainerRole;
import view.BattlePage;

public class BattleController {
    private final BattleManager battleManager;
    private final GameManager gameManager;
    private BattlePage view;
    private final int levelIdx;

    /**
     * Constructs a BattleController for a specific battle instance.
     *
     * @param gameManager  the main game manager
     * @param battleManager the battle manager handling battle logic
     * @param levelIdx     the index of the level being played
     */
    public BattleController(GameManager gameManager, BattleManager battleManager, int levelIdx) {
        this.gameManager = gameManager;
        this.battleManager = battleManager;
        this.levelIdx = levelIdx;
    }

    /**
     * Sets the battle view associated with this controller.
     *
     * @param view the BattlePage view
     */
    public void setView(BattlePage view) {
        this.view = view;
    }

    /**
     * Returns the player trainer.
     *
     * @return the player trainer
     */
    public Trainer getPlayer() {
        return battleManager.getState().getPlayer();
    }

    /**
     * Returns the opponent trainer.
     *
     * @return the opponent trainer
     */
    public Trainer getOpponent() {
        return battleManager.getState().getOpponent();
    }

    /**
     * Handles selection of an attack move by the player.
     *
     * @param moveIndex the index of the selected move
     */
    public void onAttackSelected(int moveIndex) {
        submitAction(new AttackAction(getPlayer(), moveIndex));
    }

    /**
     * Handles selection of a team switch by the player.
     *
     * @param teamIndex the index of the goober to switch to
     */
    public void onSwitchSelected(int teamIndex) {
        submitAction(new SwitchAction(getPlayer(), teamIndex));
    }

    /**
     * Handles the use of an item by the player.
     *
     * @param item the item being used
     */
    public void onItemUsed(Item item) {
        Trainer target = item.isTargetSelf() ? getPlayer() : getOpponent();
        submitAction(new ItemAction(getPlayer(), target, item.getName()));
    }

    /**
     * Handles the player choosing to run from the battle.
     * Transitions the game back to the map state.
     */
    public void onRunSelected() {
        gameManager.setState(new MapState(gameManager));
    }

    /**
     * Submits a player action to the view for processing.
     *
     * @param playerAction the action selected by the player
     */
    private void submitAction(Action playerAction) {
        if (view != null) {
            view.submitPlayerAction(playerAction);
        }
    }

    /**
     * Returns the winning trainer of the battle, if any.
     *
     * @return the winning trainer, or null if no winner yet
     */
    public Trainer getWinner() {
        return battleManager.getState().getWinner();
    }

    /**
     * Checks whether the battle has concluded.
     *
     * @return true if the game is finished, false otherwise
     */
    public boolean gameIsFinished() {
        return battleManager.getState().isFinished();
    }

    /**
     * Generates and returns the AI's action for the current turn.
     *
     * @return the AI-selected action
     */
    public Action getAiAction() {
        return ((GameBattleState) gameManager.getState()).generateAIAction();
    }

    /**
     * Resolves a single battle turn using the provided actions.
     *
     * @param playerAction   the player's chosen action
     * @param opponentAction the opponent's chosen action
     * @return the result of the resolved turn
     */
    public BattleTurnResult resolveTurn(Action playerAction, Action opponentAction) {
        return battleManager.resolveTurn(playerAction, opponentAction);
    }

    /**
     * Returns the accumulated battle log messages.
     *
     * @return a list of battle log entries
     */
    public List<String> getLogs() {
        return battleManager.getLogs();
    }

    /**
     * Determines whether the player's trainer ability may be used.
     * Applies role-specific restrictions when necessary.
     *
     * @return true if the trainer ability is usable, false otherwise
     */
    public boolean isTrainerAbilityUsable() {
        Trainer p = getPlayer();
        if (p.hasUsedAbility()) return false;

        if (p.getRole() == TrainerRole.NECROMANCER) {
            boolean hasFainted = false;
            for (Goober g : p.getTeam()) {
                if (g.isFainted()) {
                    hasFainted = true;
                    break;
                }
            }
            if (!hasFainted) return false;
        }

        return true;
    }

    /**
     * Processes post-battle rewards for the player upon victory.
     * Awards coins, updates level completion data, unlocks progression,
     * and saves the game state.
     */
    public void processBattleRewards() {
        if (getWinner() == getPlayer()) {
            int completionLevel = 0;
            for (Goober g : getPlayer().getTeam()) {
                if (!g.isFainted()) {
                    completionLevel++;
                }
            }

            // Give Coins
            gameManager.getSession().addCoins(completionLevel);

            // Update Level Data
            Level currentLevel = gameManager.getSession().getLevel(levelIdx);
            if (currentLevel != null) {
                currentLevel.updateCompletionLevel(completionLevel);

                // Unlock Next Level
                if (completionLevel > 0) {
                    gameManager.getSession().updateHighestCompleted(levelIdx);
                }
            }

            // Save Game
            gameManager.saveOnClose();
        }
    }

    /**
     * Resets the ability-used status for both trainers.
     */
    public void resetAbilityUsed() {
        getPlayer().setAbilityUsed(false);
        getOpponent().setAbilityUsed(false);
    }

    /**
     * Attempts to automatically switch the player's active goober
     * if the current one has fainted.
     *
     * @return the name of the newly active goober, or null if no switch occurred
     */
    public String attemptAutoSwitch() {
        Trainer p = getPlayer();
        if (p.getActiveGoober().isFainted() && p.hasAvailableGoobers()) {
            for (int i = 0; i < p.getTeam().size(); i++) {
                if (!p.getTeam().get(i).isFainted()) {
                    p.switchActive(i);
                    return p.getActiveGoober().getName();
                }
            }
        }
        return null;
    }
}
