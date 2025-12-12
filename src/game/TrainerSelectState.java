/**
 * File: TrainerSelectState.java
 * Purpose:
 *      Represents the game state where the player selects their
 *      trainer role and assembles a team of Goobers.
 */

package game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import ai.Difficulty;
import controller.TrainerSelectController;
import items.ItemFactory;
import javafx.scene.Scene;
import models.Goober;
import models.GooberFactory;
import models.Trainer;
import models.TrainerRole;
import view.TrainerSelect;

/**
 * A game state that manages the trainer and team selection screen.
 * <p>
 * This state handles user input for choosing a {@link TrainerRole},
 * selecting {@link Goober} entities, enforcing team composition rules,
 * and initializing the {@link UserSession} once the draft is complete.
 * </p>
 */
public class TrainerSelectState implements GameState {

    private GameManager gm;

    /** The temporary team selection (fixed size of 3). */
    private Goober[] team = new Goober[3];

    /** The currently edited team slot index. */
    private int currentEditingSlot = -1;

    /** The selected trainer role. */
    private TrainerRole currentTrainerRole;

    /** Save slot associated with this playthrough. */
    private int saveSlot;

    /** Selected AI difficulty. */
    private Difficulty selectedDifficulty;

    /**
     * Constructs a TrainerSelectState.
     *
     * @param gm the main game manager
     * @param saveSlot the save slot for this game
     * @param difficulty the selected difficulty
     */
    public TrainerSelectState(GameManager gm, int saveSlot, Difficulty difficulty) {
        this.gm = gm;
        this.saveSlot = saveSlot;
        this.selectedDifficulty = difficulty;
    }

    /**
     * Constructs a TrainerSelectState with default difficulty.
     *
     * @param gm the main game manager
     * @param saveSlot the save slot for this game
     */
    public TrainerSelectState(GameManager gm, int saveSlot) {
        this(gm, saveSlot, Difficulty.EASY);
    }

    public TrainerSelectState(GameManager gm2) {
    	this.gm = gm2;
    	this.saveSlot = -1; // "no save" for this usage 
    	this.selectedDifficulty = Difficulty.EASY;
	}

	/**
     * Sets the selected trainer role.
     *
     * @param role the chosen {@link TrainerRole}
     */
    public void setTrainerRole(TrainerRole role) {
        this.currentTrainerRole = role;
    }

    /**
     * Sets which team slot is currently being edited.
     *
     * @param index the slot index (0–2)
     */
    public void setEditingSlot(int index) {
        if (index >= 0 && index < 3) {
            this.currentEditingSlot = index;
        }
    }

    /**
     * Assigns a Goober to the currently active slot.
     * <p>
     * Duplicate Goobers are not allowed.
     * </p>
     *
     * @param gooberName the name of the Goober to assign
     * @return {@code true} if assignment succeeded, {@code false} otherwise
     */
    public boolean selectGooberForActiveSlot(String gooberName) {
        if (currentEditingSlot == -1) return false;

        for (int i = 0; i < team.length; i++) {
            if (i != currentEditingSlot &&
                team[i] != null &&
                team[i].getName().equals(gooberName)) {
                return false;
            }
        }

        Goober candidate = GooberFactory.getGoober(gooberName);
        if (candidate != null) {
            team[currentEditingSlot] = candidate;
            return true;
        }
        return false;
    }

    /**
     * Finalizes the team draft and starts the game.
     * <p>
     * Creates a {@link Trainer} with the selected Goobers and
     * starter inventory, then transitions to the campaign flow.
     * </p>
     */
    public void startGame() {
        if (!isReadyToStart()) return;

        List<Goober> finalTeam = new ArrayList<>();
        for (Goober g : team) {
            if (g != null) finalTeam.add(g);
        }

        Trainer player = new Trainer(
                currentTrainerRole.getName(),
                finalTeam,
                ItemFactory.createStarterInventory()
        );

        gm.startSinglePlayer(player, saveSlot, selectedDifficulty);
    }

    /**
     * Randomly selects a trainer role and team composition.
     */
    public void randomizeTeam() {
        Random rand = new Random();
        TrainerRole[] roles = {
                TrainerRole.NECROMANCER,
                TrainerRole.CS_STUDENT,
                TrainerRole.GAMBLER,
                TrainerRole.WEEB
        };
        currentTrainerRole = roles[rand.nextInt(roles.length)];

        List<String> specials = GooberFactory.getAllSpecialGooberNames();
        team[0] = GooberFactory.getGoober(
                specials.get(rand.nextInt(specials.size()))
        );

        List<String> normals = new ArrayList<>(
                GooberFactory.getAllNormalGooberNames()
        );
        Collections.shuffle(normals);

        if (normals.size() >= 2) {
            team[1] = GooberFactory.getGoober(normals.get(0));
            team[2] = GooberFactory.getGoober(normals.get(1));
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Resets all draft-related state and initializes the
     * trainer selection UI.
     * </p>
     *
     * @param session the active user session
     */
    @Override
    public void enter(UserSession session) {
        team = new Goober[3];
        currentTrainerRole = null;
        currentEditingSlot = -1;

        if (gm.getStage() != null) {
            TrainerSelectController controller =
                    new TrainerSelectController(this);
            TrainerSelect view = new TrainerSelect(
                    gm.getStage(),
                    controller
            );

            Scene scene = new Scene(view, 1140, 640);
            gm.getStage().setScene(scene);
        }
    }

    /**
     * Returns the currently selected team.
     *
     * @return the selected Goober list
     */
    public List<Goober> getSelectedTeam() {
        return Arrays.asList(team);
    }

    /**
     * Checks whether all required selections have been made.
     *
     * @return {@code true} if ready to start, {@code false} otherwise
     */
    public boolean isReadyToStart() {
        if (currentTrainerRole == null) return false;
        for (Goober g : team) {
            if (g == null) return false;
        }
        return true;
    }

    /**
     * Checks if a Goober is already selected.
     *
     * @param name the Goober name
     * @return {@code true} if selected, {@code false} otherwise
     */
    public boolean isGooberSelected(String name) {
        for (Goober g : team) {
            if (g != null && g.getName().equals(name)) return true;
        }
        return false;
    }

    /**
     * Returns the selected trainer role.
     *
     * @return the chosen {@link TrainerRole}
     */
    public TrainerRole getSelectedTrainer() {
        return currentTrainerRole;
    }
}
