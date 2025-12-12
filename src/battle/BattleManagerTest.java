package battle;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import action.*;
import items.Item;
import items.ItemFactory;
import models.*;

class BattleManagerTest {

	private BattleManager manager;
	private Trainer player;
	private Trainer opponent;
	
	@BeforeEach
	void setUp() {
		// Use known species from GooberFactory so they have valid moves.
		Goober playerMon1 = GooberFactory.getGoober(GooberFactory.ALL_GOOBER_NAMES.get(0));
		Goober playerMon2 = GooberFactory.getGoober(GooberFactory.ALL_GOOBER_NAMES.get(1));
		Goober enemyMon1 = GooberFactory.getGoober(GooberFactory.ALL_GOOBER_NAMES.get(2));
		
		player = new Trainer("Player", List.of(playerMon1, playerMon2));
		opponent = new Trainer("Opponent", List.of(enemyMon1));
		manager = new BattleManager(player, opponent);
	}
	
	@Test
	void resolveTurn_switchAction_changesActiveGoober() {
		// Pre-condition: player starts on index 0
		assertEquals(0, player.getActiveIndex());
		Action playerAction = new SwitchAction(player, 1); // switch to second Goober
		Action opponentAction = null; // opponent does nothing
		BattleTurnResult finished = manager.resolveTurn(playerAction, opponentAction);
		assertEquals(1, player.getActiveIndex(), "Player should now have Goober at index 1 active.");
		assertSame(player.getTeam().get(1), player.getActiveGoober(),"Active Goober should be the one at index 1.");
	}
	
	@Test
	void resolveTurn_itemAction_healItemRestoresHpAndIsConsumed() {
		// Give player a healing item (HEAL 25, targetSelf = true)
		Item healItem = ItemFactory.createItem("Plankton");
		player.addItem(healItem);
		
		Goober active = player.getActiveGoober();
		int maxHp = active.getMaxHp();
		
		// Hurt the active Goober first
		active.takeDamage(30);
		int hpAfterDamage = active.getCurrentHp();
		assertTrue(hpAfterDamage < maxHp, "Precondition: Goober should be damaged.");
		
		// Use the healing item; opponent does nothing this turn
		Action playerAction = new ItemAction(player, player, healItem.getName());
		Action opponentAction = null;
		BattleTurnResult finished = manager.resolveTurn(playerAction, opponentAction);
		int hpAfterTurn = active.getCurrentHp();
		assertTrue(hpAfterTurn > hpAfterDamage,"HP should increase after using a healing item.");
		assertFalse(player.hasItem(healItem.getName()),"Item should be consumed and removed from inventory.");
	}
	
	@Test
	void resolveTurn_setsPlayerWinWhenOpponentHasNoAvailableGoobers() {
		// Knock out the opponent's only Goober BEFORE resolving the turn
		Goober enemyActive = opponent.getActiveGoober();
		enemyActive.takeDamage(enemyActive.getMaxHp()); // enough to faint
		
		assertFalse(manager.getState().isFinished(),"Battle should start as IN_PROGRESS.");
		BattleTurnResult finished = manager.resolveTurn(null, null); // no-op actions
		assertTrue(manager.getState().isFinished(), "BattleState should be marked finished.");
		assertSame(player, manager.getState().getWinner(),"Player should be declared the winner when opponent team has no available Goobers.");
	}
	
	@Test
	void resolveTurn_setsOpponentWinWhenPlayerHasNoAvailableGoobers() {
		for (Goober g : player.getTeam()) {
	        g.takeDamage(g.getMaxHp()); // enough to faint each Goober
	    }
		assertFalse(manager.getState().isFinished(),"Battle should start as IN_PROGRESS.");
		BattleTurnResult finished = manager.resolveTurn(null, null); // no-op actions
		assertTrue(manager.getState().isFinished(), "BattleState should be marked finished.");
		assertSame(opponent, manager.getState().getWinner(),"Opponent should be declared the winner when player team has no available Goobers.");
	}
	
	@Test
	void resolveTurn_doesNothingIfBattleAlreadyFinished() {
		// Force the state to a finished phase
		manager.getState().setPhase(BattleState.BattlePhase.PLAYER_WIN);
		Goober playerActiveBefore = player.getActiveGoober();
		int    playerHpBefore = playerActiveBefore.getCurrentHp();
		Action playerAction = new AttackAction(player, 0);
		Action opponentAction = new AttackAction(opponent, 0);
		BattleTurnResult finished = manager.resolveTurn(playerAction, opponentAction);
		assertSame(BattleState.BattlePhase.PLAYER_WIN, manager.getState().getPhase(),"Battle phase should remain PLAYER_WIN.");
		assertSame(player, manager.getState().getWinner(),"Winner should remain unchanged.");
		assertEquals(playerHpBefore, playerActiveBefore.getCurrentHp(),"HP should not change when the battle is already finished.");
		
	}
	
}
