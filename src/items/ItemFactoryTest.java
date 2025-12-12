package items;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

public class ItemFactoryTest {

    @Test
    void createKnownItem_hasExpectedProperties() {
    	Item plankton = ItemFactory.createItem("Plankton");
    	assertEquals("Plankton", plankton.getName());
    	assertEquals(ItemType.HEAL, plankton.getType());
    	assertTrue(plankton.isTargetSelf());
    	assertEquals(25, plankton.getMagnitude());
    	assertEquals(2, plankton.getCost());
    }
    
    @Test
    void createItem_returnsNewInstanceEachTime() {
    	Item first = ItemFactory.createItem("Plankton");
    	Item second = ItemFactory.createItem("Plankton");
    	// Same logical item, but not the same object
    	assertNotSame(first, second);}
    
    @Test
    void getAllItemNames_containsRegisteredItems() {
    	List<String> names = ItemFactory.getAllItemNames();
    	assertTrue(names.contains("Plankton"));
    	assertTrue(names.contains("Job Application"));
    	assertTrue(names.contains("The Annoying Orange"));
    }
    
    @Test
    void createUnknownItem_throwsIllegalArgumentException() {
    	assertThrows(IllegalArgumentException.class,() -> ItemFactory.createItem("DefinitelyNotAnItem"));
    }
    
    @Test
    void getItemCost_matchesRegistryCost() {
    	int cost = ItemFactory.getItemCost("An Annoying Orange");
    	// According to ItemFactory registry, this is cost 3
    	assertEquals(3, cost);
    }
    

}
