package models;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import items.Item;
import items.ItemFactory;

class TrainerTest {

	private Trainer trainer;
	
	@BeforeEach
	void setUp() {
		// Build a small team using real Goobers from the factory so stats/moves are valid.
		Goober mon1 = GooberFactory.getGoober(GooberFactory.ALL_GOOBER_NAMES.get(0));
		Goober mon2 = GooberFactory.getGoober(GooberFactory.ALL_GOOBER_NAMES.get(1));
		trainer = new Trainer("TestTrainer", List.of(mon1, mon2));}
	
	@Test
	void constructor_setsActiveIndexZeroAndActiveGooberIsFirstTeamMember() {
		assertEquals(0, trainer.getActiveIndex(),"New Trainer should start with active index 0.");
		Goober expected = trainer.getTeam().get(0);
		Goober actual   = trainer.getActiveGoober();
		assertSame(expected, actual,"Active Goober should initially be the first Goober on the team.");
	}
	
	@Test
	void switchActive_validIndex_updatesActiveIndexAndActiveGoober() {
		// Precondition: start at index 0
		assertEquals(0, trainer.getActiveIndex());
		
		// Switch to the second Goober in the list (index 1)
		trainer.switchActive(1);
		
		assertEquals(1, trainer.getActiveIndex(),"Active index should be updated to the new value.");
		assertSame(trainer.getTeam().get(1), trainer.getActiveGoober(),"Active Goober should match the Goober at index 1.");
	}
	
	@Test
	void addItem_and_hasItem_trackInventoryCorrectly() {
		Item plankton = ItemFactory.createItem("Plankton");
		String name   = plankton.getName();
		assertFalse(trainer.hasItem(name),"Precondition: Trainer should not have the item before it is added.");
		trainer.addItem(plankton);
		assertTrue(trainer.hasItem(name),"After adding, trainer should report having that item.");
	}
	
	@Test
	void consumeItem_removesItemFromInventory() {
		Item plankton = ItemFactory.createItem("Plankton");
		String name   = plankton.getName();
		trainer.addItem(plankton);
		assertTrue(trainer.hasItem(name),"Precondition: trainer should have item after adding.");
		Item removed = trainer.consumeItem(name);

        assertNotNull(removed, "consumeItem should return the removed Item instance.");

        assertFalse(trainer.hasItem(name), "Item should no longer be in the inventory after consumption.");
	}
	
	@Test
	void consumeItem_onMissingItem_returnsFalseAndDoesNotThrow() {
		String nonExistentName = "DefinitelyNotARealItem";
		assertFalse(trainer.hasItem(nonExistentName),"Precondition: trainer should not have this fake item.");
		Item removed = trainer.consumeItem(nonExistentName);

        assertNull(removed, "consumeItem should return null when item not found.");
	}
	
}
