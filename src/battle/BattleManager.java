package battle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import action.Action;
import action.ActionType;
import action.AttackAction;
import action.ItemAction;
import action.SwitchAction;
import effects.CritModificationEffect;
import effects.DizzyEffect;
import effects.Effect;
import effects.PoisonEffect;
import effects.StunEffect;
import items.Item;
import models.Goober;
import models.GooberMove;
import models.Trainer;
import models.TrainerRole;
import models.XPManager;

/**
 * File: BattleManager.java
 * Purpose:
 * 		Orchestrates turn-by-turn resolution of battles between two Trainers.
 * 		Uses {@link BattleState} to track high-level battle status and delegates stat changes to Goober / GooberState.
 */
public class BattleManager {
	/** Helper for hit / crit / damage calculations. */
	private BattleState state;
	private MoveCalculator calculator = new MoveCalculator();
	private BattleTurnResult currentResult;
	
    private final List<String> logs = new ArrayList<>();
	
	/**
	 * Starts a new battle with the given player and opponent Trainers.
	 * 
	 * @param player		player-controlled trainer
	 * @param opponent AI or remote opponent trainer
	 * 
	 * @return a new {@link BattleState} in the IN_PROGRESS phase
	 */
	public BattleManager(Trainer player, Trainer opponent) {
		state = new BattleState(player, opponent);
		applyStartOfBattlePassives(player, opponent);
		applyStartOfBattlePassives(opponent, player);
	}
	
	public BattleManager(Trainer player, Trainer opponent, MoveCalculator calculator) {
		this(player, opponent);
		this.calculator = calculator;
	}
	
	// --- PASSIVE LOGIC ---
	
	/** Applies static buff passives at the start of the match. */
	private void applyStartOfBattlePassives(Trainer self, Trainer enemy) {
		if (self.getRole() == TrainerRole.CS_STUDENT) {
			for (Goober g : enemy.getTeam()) {
				// Strength 0.05 (Weak poison), Duration 999 (Forever)
				g.addEffect(new PoisonEffect(999, g, 0.05));
			}
		}
		
		if (self.getRole() == TrainerRole.GAMBLER) {
			for (Goober g : self.getTeam()) {
				g.addEffect(new CritModificationEffect(999, g, 0.15));
			}
		}
	}
	
	/**
	 * Resolves a single turn of combat.
	 * <ol>
	 * <li> If battle is finished, do nothing. </li>
	 * <li>Apply start-of-turn effects and queued stuns.</li>
	 * <li>Determine turn order based on speed.</li>
	 * <li>Resolve the first action, then the second (if still valid).</li>
	 * <li>Check win conditions.</li>
	 * <li>Apply end-of-turn effects if battle is still in progress.</li>
	 * </ol>
	 * 
	 * @param 		playerAction   the player's chosen action
	 * @param 		opponentAction the opponent's chosen action
	 * @return 		true if the game is finished otherwise false
	 */

	public BattleTurnResult resolveTurn(Action playerAction, Action opponentAction) {
		currentResult = new BattleTurnResult(state.isFinished());
		if (state.isFinished()) return currentResult;
		
		Trainer player = state.getPlayer();
		Trainer opponent = state.getOpponent();
		
		tickEffects(player);
	    tickEffects(opponent);
	    
	    boolean pActed = false;
	    boolean oActed = false;
	    
	    if (isPriority(playerAction) && playerAction != null) {
	        executeAction(playerAction, player, opponent);
	        pActed = true;
	    }
	    if (isPriority(opponentAction) && opponentAction != null) {
	        executeAction(opponentAction, opponent, player);
	        oActed = true;
	    }
	    
	    if (!pActed && isSwitch(playerAction)) {
	        executeAction(playerAction, player, opponent);
	        pActed = true;
	    }
	    if (!oActed && isSwitch(opponentAction)) {
	        executeAction(opponentAction, opponent, player);
	        oActed = true;
	    }
		
		
		if (!pActed || !oActed) {
	        double pSpeed = player.getActiveGoober().getState().getSpeed();
	        double oSpeed = opponent.getActiveGoober().getState().getSpeed();

	        if (pSpeed >= oSpeed) {
	            if (!pActed && playerAction != null) {
	            	if (oActed) currentResult.addLog("");
	                executeAction(playerAction, player, opponent);
	                pActed = true;
	            }
	            if (!state.isFinished() && !oActed && opponentAction != null) {
	            	if (pActed) currentResult.addLog("");
	                executeAction(opponentAction, opponent, player);
	                oActed = true;
	            }
	        } else {
	            if (!oActed && opponentAction != null) {
	            	if (pActed) currentResult.addLog("");
	                executeAction(opponentAction, opponent, player);
	                oActed = true;
	            }
	            if (!state.isFinished() && !pActed && playerAction != null) {
	            	if (oActed) currentResult.addLog("");
	                executeAction(playerAction, player, opponent);
	                pActed = true;
	            }
	        }
	    }

        checkWinCondition();
        return currentResult;
	}
	
	private boolean isPriority(Action a) {
		if (a == null) return false;
	    return a.getType() == ActionType.USE_ITEM || 
	           a.getType() == ActionType.TRAINER_ABILITY;
    }
	
	private boolean isSwitch(Action a) {
	    if (a == null) return false;
	    return a.getType() == ActionType.SWITCH;
	}
	
	private void tickEffects(Trainer t) {
        Goober g = t.getActiveGoober();
        if (g.isFainted()) return;
        
        g.getState().cycleEffects();
        g.getState().applyStun();
    }
	
	// Core Action Resolution // 

    /** Executes a single action for the given actor. */    
	private void executeAction(Action action, Trainer actor, Trainer defender) {
		if (state.isFinished() || action == null) return; 
		
		if (actor.getActiveGoober().isFainted() && action.getType() != ActionType.SWITCH) {
            currentResult.addLog(actor.getName() + "'s Goober is fainted and cannot move!");
            return;
        }
		
		if (action.getType() != ActionType.TRAINER_ABILITY && 
	            action.getType() != ActionType.SWITCH && 
	            actor.getActiveGoober().getState().isStunned()) {
	            currentResult.addLog(actor.getActiveGoober().getName() + " is stunned!");
	            return;
	        }
		
		switch (action.getType()) {
        case ATTACK:
            if (action instanceof AttackAction) resolveAttack((AttackAction) action, actor, defender);
            break;
        case USE_ITEM:
            if (action instanceof ItemAction) resolveItem((ItemAction) action, actor, defender);
            break;
        case SWITCH:
            if (action instanceof SwitchAction) resolveSwitch((SwitchAction) action, actor);
            break;
        case TRAINER_ABILITY:
            resolveTrainerAbility(actor, defender);
            break;
    }
		
	}
	
	// ATTACK// 
	
	/** Resolves an attack from the attacker on the defender.*/
	private void resolveAttack(AttackAction action, Trainer attacker, Trainer defender) {
		Goober atkGoober = attacker.getActiveGoober();
		Goober defGoober = defender.getActiveGoober();
		
		// Get the selected move from the attacker's usable moves
		GooberMove move = atkGoober.getMove(action.getMoveIndex());
		
		currentResult.addLog(atkGoober.getName() + " used " + move.getName() + "!");
		
		// Hit check: combine move's base hit chance with Goober's hitChance (dizzy)
		double modifiedHitChance = move.getHitChance() * atkGoober.getState().getHitChance();
		if (Math.random() > modifiedHitChance) {
            currentResult.addLog("...but it missed!");
            return;
        }
		
		// Crit check
		boolean critical = calculator.isCritical(atkGoober, move);
		int damage = calculator.calculateDamage(atkGoober, defGoober, move, critical);
		
		if (defender.getRole() == TrainerRole.WEEB && !defender.hasUsedPlotArmor()) {
			if (defGoober.getCurrentHp() - damage <= 0) {
				damage = Math.max(0, defGoober.getCurrentHp() - 1); 
				defender.usePlotArmor();
				currentResult.addLog("The Power of Anime prevented death!");
			}
		}
		
		if (damage > 0) {
			defGoober.takeDamage(damage);
            if (critical) currentResult.addLog("Critical Hit! Dealt " + damage + " dmg.");
            else currentResult.addLog("Dealt " + damage + " dmg.");
            
            if (attacker.getRole() == TrainerRole.NECROMANCER) {
                int healAmt = (int) Math.ceil(damage * 0.2);
                if (healAmt > 0) {
                    atkGoober.heal(healAmt);
                }
            }
            
            handleXpGain(attacker, defGoober);
	    }
		
		if (attacker.getRole() == TrainerRole.JOKER) {
			String effect;
			if (Math.random() < 0.33) {
				double rand = Math.random();
				if (rand < 0.33) {
					defGoober.addEffect(new StunEffect(1, defGoober, 0));
					effect = "Stun";
				}
				else if (rand < 0.66) {
					defGoober.addEffect(new PoisonEffect(1, defGoober, 0.05));
					effect = "Poison";
				}
				else {
					defGoober.addEffect(new DizzyEffect(1, defGoober, 0.3));
					effect = "Dizzy";
				}
				currentResult.addLog("Joker's chaos added effect " + effect + "!");
			}
		}
		
		if (move.getEffect() != null) {
			applyMoveEffect(move, attacker, defender);
		}
	}
	
	private void resolveTrainerAbility(Trainer actor, Trainer enemy) {
		if (actor.hasUsedAbility()) return; 
		actor.setAbilityUsed(true);
		currentResult.addLog(actor.getName() + " used their Trainer Ability!");
		
		switch (actor.getRole()) {
		case NECROMANCER:
			// RAISE DEAD: Revive random fainted Goober with 50% HP
			List<Goober> fainted = new ArrayList<>();
			for (Goober g : actor.getTeam()) if (g.isFainted()) fainted.add(g);
			
			if (!fainted.isEmpty()) {
				Goober toRevive = fainted.get(new Random().nextInt(fainted.size()));
				toRevive.setHealth(toRevive.getMaxHp() / 2);
			}
			break;
			
		case GAMBLER:
			// RED OR BLACK: 50% chance to Deal 50% Enemy CurrHP OR Take 25% Self CurrHP
			if (Math.random() > 0.5) {
				// Win: Enemy takes damage
				Goober target = enemy.getActiveGoober();
				int dmg = (int) (target.getCurrentHp() * 0.5);
				enemy.getActiveGoober().takeDamage(dmg);
				handleXpGain(actor, target);
				currentResult.addLog("Gambling pays off! Deal 50% damage!");
			} else {
				// Loss: Self takes damage
				int dmg = (int) (actor.getActiveGoober().getCurrentHp() * 0.25);
				actor.getActiveGoober().takeDamage(dmg);
				currentResult.addLog("Should have bet more! Take 25% damage!");
			}
			break;
			
		case CS_STUDENT:
			// SUDO KILL: Execute if < 20% HP, else 0
			Goober target = enemy.getActiveGoober();
			double hpPercent = (double)target.getCurrentHp() / target.getMaxHp();
			if (hpPercent < 0.20) {
				target.takeDamage(9999); // Force kill
				handleXpGain(actor, target);
			} else {
				// Deal 0 damage (do the math)
				target.takeDamage(0);
				currentResult.addLog("It's simple math!");
			}
			break;
			
		case WEEB:
			// POWER OF FRIENDSHIP: Full Heal + Cleanse active unit
			Goober active = actor.getActiveGoober();
			active.heal(active.getMaxHp());
			active.getEffects().clear(); // Remove all statuses
			active.getState().unStun();
			break;
			
		case JOKER:
			// THERE'S NO LAWS: 69 damage + Stun
			Goober jTarget = enemy.getActiveGoober();
			jTarget.takeDamage((int) (jTarget.getCurrentHp() * 0.69));
			jTarget.addEffect(new StunEffect(1, jTarget, 0));
			break;
		}
	}
	
	private void applyMoveEffect(GooberMove move, Trainer attacker, Trainer defender) {
		List<Goober> targets = new ArrayList<>();
		
		Effect eff = move.getEffect();
		String effectName = move.getEffect().getType().getName();
		if (effectName.equals("Defence Modification")) {
			effectName = (eff.getStrength() < 0) ? "Defence Reduction" : "Defence Increase";
		}
		else if (effectName.equals("Damage Modification")) {
			effectName = (eff.getStrength() < 0) ? "Damage Reduction" : "Damage Increase";
		}
		else if (effectName.equals("Crit Modification")){
			effectName = "Crit Increase";
		}
		currentResult.addLog("Applied " + effectName + "!");
		switch (move.getTargetType()) {
			case SELF: targets.add(attacker.getActiveGoober()); break;
			case ENEMY: targets.add(defender.getActiveGoober()); break;
			case ALL_ALLIES: 
				for(Goober g : attacker.getTeam()) if(!g.isFainted()) targets.add(g); 
				break;
			case ALL_ENEMIES: 
				for(Goober g : defender.getTeam()) if(!g.isFainted()) targets.add(g); 
				break;
		}
		for (Goober t : targets) { 
			Effect e = move.getEffect().copy();
			e.setTarget(t);
			t.addEffect(e);
			
			boolean finished = e.apply();
	        
	        if (finished) {
	            t.getState().getEffects().remove(e);
	        }
		}
		
		
	}
	
	// ITEM// 
	/** Resolves an item use action.*/
	private void resolveItem(ItemAction action, Trainer actor, Trainer defender) {
		// Remove the item from inventory
		String itemName = action.getItemName();
		Item item = actor.consumeItem(itemName);
		if (item == null) {
			currentResult.addLog(actor.getName() + " tried to use " + itemName + " but couldn't find it!");
            return;
		}
		
		// Determine which trainer's Goober is targeted
		Trainer targetTrainer = item.isTargetSelf() ? actor : defender;
		Goober targetGoober = targetTrainer.getActiveGoober();
		
		currentResult.addLog(actor.getName() + " used " + itemName + " on " + targetGoober.getName() + ".");
		
		switch (item.getType()) {
			case HEAL:
				targetGoober.heal((int) (targetGoober.getMaxHp() * item.getMagnitude()));
				break;
			
			case DAMAGE:
				targetGoober.takeDamage((int) (targetGoober.getCurrentHp() * item.getMagnitude()));
				handleXpGain(actor, targetGoober);
				break;
			
			case STUN:
				// Apply a stun effect for 'magnitude' turns
				StunEffect stun = new StunEffect((int) item.getMagnitude(), targetGoober, 0.0);
				targetGoober.addEffect(stun);
				break;
		}
		}
	
		// XP Helper
	
		/**
		 * Checks if the victim has fainted and awards XP to the killer if applicable.
		 * Only the human Player trainer gains XP from kills.
		 */
		private void handleXpGain(Trainer killer, Goober victim) {
			// Check if the victim actually fainted
			if (!victim.isFainted()) {
				return;
			}
			
			currentResult.addLog(victim.getName() + " fainted!");
			
			// Check if the killer is the Player (AI opponents don't need XP)
			if (!killer.equals(state.getPlayer())) {
				return;
			}
			
			// Calculate XP yield
			int xpAmount = XPManager.xpToGive(victim.getLevel());
			
			// Award to the killer's active Goober
			for (Goober g : killer.getTeam()) {
				if (!g.isFainted()) {
					g.gainXp(xpAmount);
				}
			}
			currentResult.addLog("Living goobers gained " + xpAmount + " XP!");
		}
	
	// SWITCH//
	
	/** Resolves a switch action: changes the actor's active Goober.*/
	private void resolveSwitch(SwitchAction action, Trainer actor) {
		int newIndex = action.getNewIndex();
        actor.switchActive(newIndex);
        Goober newG = actor.getActiveGoober();
        
        currentResult.addLog(actor.getName() + " switched to " + newG.getName() + "!");
	}
	
	// WIN CONDITION//
	
	/** Checks whether the battle has a winner and updates the state phase.*/
	private void checkWinCondition() {
		boolean pAlive = state.getPlayer().hasAvailableGoobers();
		boolean oAlive = state.getOpponent().hasAvailableGoobers();
		
		if (!pAlive) state.setPhase(BattleState.BattlePhase.OPPONENT_WIN);
		else if (!oAlive) state.setPhase(BattleState.BattlePhase.PLAYER_WIN);
	}
	
	public BattleState getState() {
		return state;
	}
	
	public List<String> getLogs() {
		return currentResult.getLogs();
	}
	
	public void setLogs(List<String> newLogs) {
	    logs.clear();
	    if (newLogs != null) {
	        logs.addAll(newLogs);
	    }
	}
}
