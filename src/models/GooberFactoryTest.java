package models;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

public class GooberFactoryTest {

    @Test
    void getGoober_hasExpectedProperties() {
    	Goober dababy = GooberFactory.getGoober("DaBaby", 1);
    	assertEquals("DaBaby", dababy.getName());
    	assertEquals(GooberType.TANK, dababy.getType());
    	assertEquals(dababy.getBaseMaxHp() , 115);
    	
    }
    
    @Test
    void getGoober_allInstance() {
    	Goober tralalero_tralala = GooberFactory.getGoober("Tralalero Tralala", 1);
    	assertEquals("Tralalero Tralala", tralalero_tralala.getName());
    	
    	Goober peter_griffin = GooberFactory.getGoober("Peter Griffin", 1);
    	assertEquals("Peter Griffin", peter_griffin.getName());
    	
    	Goober tung_tung_tung_sahur = GooberFactory.getGoober("Tung Tung Tung Sahur", 1);
    	assertEquals("Tung Tung Tung Sahur", tung_tung_tung_sahur.getName());
    	
    	Goober doge = GooberFactory.getGoober("Doge", 1);
    	assertEquals("Doge", doge.getName());
    	
    	Goober boss_baby = GooberFactory.getGoober("Boss Baby", 1);
    	assertEquals("Boss Baby", boss_baby.getName());
    	
    	Goober duolingo_bird = GooberFactory.getGoober("Duolingo Bird", 1);
    	assertEquals("Duolingo Bird", duolingo_bird.getName());
    	
    	Goober william_shakespear = GooberFactory.getGoober("William Shakespear", 1);
    	assertEquals("William Shakespear", william_shakespear.getName());
    	
    	Goober mahoraga = GooberFactory.getGoober("Mahoraga", 1);
    	assertEquals("Mahoraga", mahoraga.getName());
    	
    	Goober leBron = GooberFactory.getGoober("LeBron", 1);
    	assertEquals("LeBron", leBron.getName());
    	
    	Goober gigachad = GooberFactory.getGoober("Gigachad", 1);
    	assertEquals("Gigachad", gigachad.getName());
    }
    
    @Test
    void getGoober_returnsNewInstanceEachTime() {
    	Goober first = GooberFactory.getGoober("John Pork", 1);
    	Goober second = GooberFactory.getGoober("John Pork", 1);
    	// Same logical item, but not the same object
    	assertNotSame(first, second);}
    
    @Test
    void getAllGooberNames_containsRegisteredItems() {
    	List<String> names = GooberFactory.getAllGooberNames();
    	assertTrue(names.contains("DaBaby"));
    	assertTrue(names.contains("John Pork"));
    	assertTrue(names.contains("Doge"));
    	assertTrue(names.contains("LeBron"));
    }
    
    @Test
    void getNormalGooberNames_containsRegisteredItems() {
    	List<String> names = GooberFactory.getAllGooberNames();
    	assertTrue(names.contains("DaBaby"));
    	assertTrue(names.contains("John Pork"));
    	assertTrue(names.contains("Doge"));
    }
    
    @Test
    void getSpecialGooberNames_containsRegisteredItems() {
    	List<String> names = GooberFactory.getAllGooberNames();
    	assertTrue(names.contains("Gigachad"));
    	assertTrue(names.contains("Mahoraga"));
    	assertTrue(names.contains("LeBron"));
    }
    
    @Test
    void getUnknownGoober_throwsIllegalArgumentException() {
    	assertThrows(IllegalArgumentException.class,() -> GooberFactory.getGoober("DefinitelyNotAnGoober"));
    }
    
    

}
