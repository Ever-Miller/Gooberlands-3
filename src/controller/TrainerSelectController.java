/**
 * File: TrainerSelectController.java
 * Purpose:
 *      Controls the trainer and team selection process.
 *      Handles user interactions, updates the trainer selection model,
 *      and coordinates view updates for the selection screen.
 */

package controller;

import java.util.ArrayList;
import java.util.List;

import game.TrainerSelectState;
import game.MultiplayerSetupState;   
import items.Item;                 
import models.Goober;
import models.Trainer;
import models.TrainerRole;
import view.TrainerSelect;

public class TrainerSelectController {
    private final TrainerSelectState model;
    private final MultiplayerSetupState multiplayer; 
    private TrainerSelect view;

    /**
     * Standard single-player constructor.
     *
     * @param model the trainer selection state model
     */
    public TrainerSelectController(TrainerSelectState model) {
        this(model, null);
    }

    /**
     * Multiplayer-aware constructor.
     *
     * @param model       trainer selection state model
     * @param multiplayer multiplayer setup state (null for solo play)
     */
    public TrainerSelectController(TrainerSelectState model, MultiplayerSetupState multiplayer) {
        this.model = model;
        this.multiplayer = multiplayer;
    }

    /**
     * Sets the view associated with this controller.
     *
     * @param view the TrainerSelect view
     */
    public void setView(TrainerSelect view) {
        this.view = view;
    }

    /** Handles a click on a team slot. */
    public void onSlotClicked(int slotIndex) {
        model.setEditingSlot(slotIndex);
    }

    /** Handles selection of a trainer role. */
    public void onRoleSelected(TrainerRole role) {
        model.setTrainerRole(role);
        if (view != null) {
            view.updateDisplay(role, model.getSelectedTeam());
        }
    }

    /** Randomize team. */
    public void onRandomizeClicked() {
        model.randomizeTeam();
        if (view != null) {
            view.updateDisplay(model.getSelectedTrainer(), model.getSelectedTeam());
        }
    }

    /** Handles selection of a goober for the currently active slot. */
    public void onGooberClicked(String gooberName) {
        boolean success = model.selectGooberForActiveSlot(gooberName);

        if (success && view != null) {
            view.refreshTeamDisplay(model.getSelectedTeam());
            view.updateStartButton(model.isReadyToStart());
        }
    }

    /**
     * Handles the start action when the user begins the game.
     * Single-player: run existing flow.
     * Multiplayer: send trainer to MultiplayerSetupState and wait.
     */
    public void onStartClicked() {
        if (!model.isReadyToStart()) {
            return;
        }

        // MULTIPLAYER PATH
        if (multiplayer != null) {
            // Build a Trainer from the selection
            TrainerRole role = model.getSelectedTrainer();
            List<Goober> team = model.getSelectedTeam();

            String name = (role != null) ? role.getName() : "Player";
            List<Item> items = new ArrayList<>(); // or some default inventory
            Trainer localTrainer = new Trainer(name, team, items);

            // Tell the multiplayer setup state
            multiplayer.onLocalReady(localTrainer);

            // Lock the button while we wait for opponent
            if (view != null) {
                view.updateStartButton(false);
            }
            return;
        }

        //  SINGLE-PLAYER (existing) PATH 
        if (view != null) {
            view.animateExit(() -> model.startGame());
        } else {
            model.startGame();
        }
    }

    /** Checks whether a specific goober is already selected. */
    public boolean isGooberSelected(String name) {
        return model.isGooberSelected(name);
    }

    /** Returns the currently selected trainer role. */
    public TrainerRole getSelectedTrainer() {
        try {
            return model.getSelectedTrainer();
        } catch (Exception e) {
            return null;
        }
    }

    /** Returns the list of goobers selected for the team. */
    public List<Goober> getSelectedTeam() {
        return model.getSelectedTeam();
    }

    /** Indicates whether the selection is complete and ready to start. */
    public boolean isReady() {
        return model.isReadyToStart();
    }
}