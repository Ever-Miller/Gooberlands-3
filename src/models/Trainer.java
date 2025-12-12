/** 
 * File: Trainer.java
 * Purpose:
 * 		Represents a Trainer who controls a team of {@link Goober} creatures in battle.
 * 		A Trainer maintains an ordered list of Goobers and tracks which Goober is currently active.
 * 
 * <p>A Trainer is responsible only for team structure and switching logic.
 * Battle-related logic (damage, turn flow, etc.) is handled by the {@link BattleManager}.
 */
package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import items.*;

public class Trainer implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/** The trainer's display name. */
	private final String name;
	/** The team of Goobers controlled by this Trainer. */
	private List<Goober> team;
	/** Index of the currently active Goober within the team list. */
	private int activeIndex;
	/** The inventory of items this Trainer can use during battle. */
	private List<Item> inventory;
	
	private TrainerRole role;
	private boolean abilityUsed = false;
	private boolean plotArmorUsed = false;
	
	public static final List<String> NAMES = List.of("Necromancer", "Gambler", "CS Student", "Weeb");
	
	
    /** 
     * Constructs a new Trainer with the given name and team of Goobers.
     * A defensive copy of the provided team list is created. 
     * 
     * @param name the trainer's name 
     * @param team the initial team of Goobers; must contain at least one Goober
     * 
     * @throws IllegalArgumentException if the team is null or empty     
     */
	public Trainer(String name, List<Goober> team) {
		this(name, team, new ArrayList<>());
	}   
	
	public Trainer(String name, List<Goober> team, List<Item> inventory) {
		if (team == null) {
			throw new IllegalArgumentException("Trainer team cannot be null.");
		}
		
		if (inventory == null) {
			throw new IllegalArgumentException("Inventory list cannot be null");
		}
		
		this.name = name;
		this.team = new ArrayList<>(team);
		this.inventory = new ArrayList<>(inventory);
		this.activeIndex = 0;
		
		this.role = assignRoleByName(name);
	}
	
	public Trainer(String name) {
		this(name, new ArrayList<>(), new ArrayList<>());
	}
	
	private TrainerRole assignRoleByName(String name) {
		for (TrainerRole r : TrainerRole.values()) {
			if (r.getName().equalsIgnoreCase(name)) return r;
		}
		return TrainerRole.CS_STUDENT;
	}

	/**
    * Returns the trainer's display name.
    *
    * @return the trainer's name
    */
	public String getName() { return name;	}
	
    /**
     * Returns the currently active {@link Goober}.
     *
     * @return the active Goober
     */
	public Goober getActiveGoober() { return team.get(activeIndex); }
	
    /**
     * Returns an unmodifiable view of the trainer's team list.
     * This prevents external modification of the internal team structure.
     *
     * @return an unmodifiable list of this trainer's Goobers
     */
	public List<Goober> getTeam() { return Collections.unmodifiableList(team); }
	
    /**
     * Returns the index of the currently active Goober.
     *
     * @return the active Goober index
     */
	public int getActiveIndex() { return activeIndex; }
	
	public TrainerRole getRole() { return role; }
	public boolean hasUsedAbility() { return abilityUsed; }
	public void setAbilityUsed(boolean used) { 
		if (role == TrainerRole.WEEB) {
			plotArmorUsed = used;
		}
		this.abilityUsed = used; 
	}
	
	public boolean hasUsedPlotArmor() { return plotArmorUsed; }
	public void usePlotArmor() { this.plotArmorUsed = true; }
	

    /**
     * Switches the active Goober to the one at the specified index.
     * A Goober cannot be switched to if it is fainted or if the index is invalid.
     *
     * @param newIndex the index of the Goober to switch to
     * @throws IllegalArgumentException if the index is out of range
     * @throws IllegalStateException if the selected Goober cannot battle
     */
	public void switchActive(int newIndex) {
		if (newIndex < 0 || newIndex >= team.size()) {
			throw new IllegalArgumentException("Invalid Goober Index: " + newIndex);
		}
		
		Goober choice = team.get(newIndex);
		
		if (choice == null) {
			throw new IllegalStateException("Cannot switch to null Goober");
		}
		
		if (choice.getCurrentHp() <= 0) {
			throw new IllegalStateException("Cannot switch to fainted Goober");
		}
		
		this.activeIndex = newIndex;
	}
	
    /**
     * Determines whether this Trainer has at least one Goober capable of battling.
     * A Goober is considered available if its HP is greater than zero.
     *
     * @return true if the Trainer has at least one non-fainted Goober, false otherwise
     */
	public boolean hasAvailableGoobers() {
		for (Goober g : team) {
			if (g.getCurrentHp() > 0) { return true; }
		}
		return false;
	}
	
    /**
     * Returns an unmodifiable view of this Trainer's inventory.
     * 
     * @return an unmodifiable list of items in the inventory
     */
	public List<Item> getInventory() {
		return Collections.unmodifiableList(inventory);
	}
	
	/**
	 * Adds an item to this Trainer's inventory.
	 * 
	 * @param item the item to add
	 * 
	 * @throws IllegalArgumentException if the item is null
	 */
	public void addItem(Item item) {
		if (item == null) {
			throw new IllegalArgumentException("Cannot add null item to inventory");
		}
		inventory.add(item);
	}
	
	/**
	 * Checks whether this Trainer currently has at least one item with the specified name in their inventory.
	 * 
	 * @param itemName the name of the item to search for
	 * 
	 * @return true if a matching item exists, false otherwise
	 */
	public boolean hasItem(String itemName) {
		for (Item item : inventory) {
			if (item.getName().equals(itemName)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Removes and returns the first item in the inventory that matches the given name
	 * If no such item exists, returns {@code null}.
	 * 
	 * @param itemName the name of the item to consume
	 * @return the consumed item, or null if none was found
	 */
	public Item consumeItem(String itemName) {
		for (int i = 0; i < inventory.size(); i++) {
			Item item = inventory.get(i);
			if (item.getName().equals(itemName)) {
				inventory.remove(i);
				return item;
			}
		}
		return null;
	}
	
	public Trainer copy() {
		List<Goober> teamCopy = new ArrayList<>();
		for (Goober g : team) {
			teamCopy.add(g.copy());
		}
		List<Item> invCopy = new ArrayList<>(inventory);
		
		Trainer copy = new Trainer(name, teamCopy, invCopy);
		copy.abilityUsed = this.abilityUsed;
		copy.plotArmorUsed = this.plotArmorUsed;
		
		if (activeIndex < teamCopy.size() && teamCopy.get(activeIndex).getCurrentHp() > 0) {
			copy.switchActive(activeIndex);
		}
		return copy;
	}
}
