package ai;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import action.Action;
import action.ActionType;
import action.ItemAction;
import action.SwitchAction;
import items.Item;
import items.ItemFactory;
import models.Goober;
import models.GooberFactory;
import models.Trainer;

class MiniMaxAgentTest {
	
	private MiniMaxAgent aiAgent;
    private Trainer aiTrainer;
    private Trainer playerTrainer;

    @BeforeEach
    void setUp() {
        aiAgent = new MiniMaxAgent(Difficulty.HARD);
    }
    
    private List<Goober> createTeam(String gooberName, int level) {
        List<Goober> team = new ArrayList<>();
        team.add(GooberFactory.getGoober(gooberName, level));
        return team;
    }
    
    @Test
    void AIKillshowTest() {
    	List<Goober> aiTeam = createTeam("DaBaby", 50);
    	List<Goober> playerTeam = createTeam("Doge", 50);
    	
    	playerTeam.get(0).setHealth(1);
    	
    	aiTrainer = new Trainer("AI", aiTeam);
        playerTrainer = new Trainer("Player", playerTeam);
    	
    	Action action = aiAgent.getBestAction(aiTrainer, playerTrainer);
    	
    	assertNotNull(action, "AI should return an action");
    	assertEquals(ActionType.ATTACK, action.getType(), "AI should attack to finish off low HP opponent");
    }
    
    @Test
    void AISwitchWhenFaintedTest() {
    	Goober deadGoober = GooberFactory.getGoober("Tralalero Tralala", 10);
        Goober aliveGoober = GooberFactory.getGoober("DaBaby", 10);
        
        deadGoober.takeDamage(9999);
        
        List<Goober> aiTeam = new ArrayList<>();
        aiTeam.add(deadGoober);
        aiTeam.add(aliveGoober);
        
        aiTrainer = new Trainer("AI", aiTeam);
        playerTrainer = new Trainer("Player", createTeam("Doge", 10));
        
        Action action = aiAgent.getBestAction(aiTrainer, playerTrainer);
        
        assertNotNull(action);
        assertEquals(ActionType.SWITCH, action.getType(), "AI must switch if active Goober is dead");
        
        SwitchAction sa = (SwitchAction) action;
        assertEquals(1, sa.getNewIndex(), "AI should switch to the alive Goober at index 1");
    }
    
    @Test
    void AIUsesHealItemWhenCriticalTest() {
        Goober aiGoober = GooberFactory.getGoober("DaBaby", 50);
        aiGoober.setHealth((int)(aiGoober.getMaxHp() * 0.1));
        
        List<Goober> aiTeam = new ArrayList<>();
        aiTeam.add(aiGoober);
        
        List<Item> inventory = new ArrayList<>();
        inventory.add(ItemFactory.createItem("Plankton")); // Heals 25
        
        aiTrainer = new Trainer("AI", aiTeam, inventory);
        playerTrainer = new Trainer("Player", createTeam("Doge", 10)); // Weak player so AI isn't threatened to attack immediately

        Action action = aiAgent.getBestAction(aiTrainer, playerTrainer);
        assertTrue(action.getType() == ActionType.USE_ITEM || action.getType() == ActionType.ATTACK);
        
        if (action.getType() == ActionType.USE_ITEM) {
            ItemAction ia = (ItemAction) action;
            assertEquals("Plankton", ia.getItemName());
        }
    }
    
    @Test
    void AIDoesNotModifyRealStateTest() {
        aiTrainer = new Trainer("AI", createTeam("DaBaby", 20));
        playerTrainer = new Trainer("Player", createTeam("Doge", 20));
        
        int initialAiHP = aiTrainer.getActiveGoober().getCurrentHp();
        int initialPlayerHP = playerTrainer.getActiveGoober().getCurrentHp();

        aiAgent.getBestAction(aiTrainer, playerTrainer);

        assertEquals(initialAiHP, aiTrainer.getActiveGoober().getCurrentHp(), "AI HP changed during simulation! Deep copy failed.");
        assertEquals(initialPlayerHP, playerTrainer.getActiveGoober().getCurrentHp(), "Player HP changed during simulation! Deep copy failed.");
    }
}
