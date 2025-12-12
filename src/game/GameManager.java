/**
 * File: GameManager.java
 * Purpose:
 *      Orchestrates the global game loop and manages high-level
 *      architectural concerns including state transitions,
 *      session management, persistence, and integration with
 *      the JavaFX rendering system.
 */

package game;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import ai.Difficulty;
import items.Item;
import javafx.stage.Stage;
import models.Goober;
import models.Trainer;
import view.AudioManager;

/**
 * Manages the global game lifecycle.
 * <p>
 * Responsibilities include:
 * <ul>
 *   <li><b>State Management:</b> Transitions between {@link GameState}s</li>
 *   <li><b>Session Management:</b> Maintains the active {@link UserSession}</li>
 *   <li><b>Persistence:</b> Saving and loading progress to disk</li>
 *   <li><b>Rendering Bridge:</b> Connecting logic to the JavaFX {@link Stage}</li>
 * </ul>
 * </p>
 */
public class GameManager {

    private static GameManager INSTANCE;

    private GameState currentState;

    /** The active user session containing player data. */
    public UserSession session;

    /** Primary JavaFX stage. */
    private Stage stage;

    /** The save slot currently loaded (-1 if unsaved). */
    private int currentSlot = -1;

    /**
     * Constructs the GameManager.
     *
     * @param stage the primary JavaFX application stage
     */
    public GameManager(Stage stage) {
        this.stage = stage;
        INSTANCE = this;
        loadConfig();
    }

    /**
     * Returns the singleton instance of the GameManager.
     *
     * @return the GameManager instance
     */
    public static GameManager getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the active JavaFX stage.
     *
     * @return the application stage
     */
    public Stage getStage() {
        return stage;
    }

    /**
     * Returns the current user session.
     *
     * @return the active {@link UserSession}
     */
    public UserSession getSession() {
        return session;
    }

    /**
     * Transitions the game into a new {@link GameState}.
     * <p>
     * Calls {@code enter(session)} on the new state after assignment.
     * </p>
     *
     * @param newState the new game state
     */
    public void setState(GameState newState) {
        currentState = newState;
        if (currentState != null) {
            currentState.enter(session);
        }
    }

    /**
     * Starts a new single-player campaign.
     * <p>
     * Initializes a fresh {@link UserSession}, generates levels,
     * assigns difficulty, and transitions to the intro state.
     * </p>
     *
     * @param selectedTrainer the trainer chosen by the player
     * @param saveSlot the save slot index
     * @param difficulty the selected AI difficulty
     */
    public void startSinglePlayer(Trainer selectedTrainer, int saveSlot, Difficulty difficulty) {
        session = new UserSession();
        session.setPlayerTrainer(selectedTrainer);
        session.setDifficulty(difficulty);
        session.generateLevels();
        session.setSoloPlay(true);
        currentSlot = saveSlot;

        setState(new IntroState(this));
    }

    /**
     * Starts a battle for the specified level if unlocked.
     *
     * @param levelNum the 1-based level index
     */
    public void startBattleForLevel(int levelNum) {
        if (session == null) return;

        if (!session.isLevelUnlocked(levelNum)) {
            return;
        }

        setState(new GameBattleState(this, levelNum));
    }

    /**
     * Saves the current session and configuration to disk.
     */
    public void saveOnClose() {
        saveConfig();

        if (currentSlot == -1) return;

        try (ObjectOutputStream oos =
                     new ObjectOutputStream(Files.newOutputStream(getSavePath(currentSlot)))) {
            oos.writeObject(new GameSaveState(session));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Loads a saved game from a slot.
     *
     * @param slot the save slot index
     * @return true if loading succeeded, false otherwise
     */
    public boolean loadGame(int slot) {
        Path path = getSavePath(slot);

        if (Files.exists(path)) {
            try (ObjectInputStream ois =
                         new ObjectInputStream(Files.newInputStream(path))) {

                GameSaveState snap = (GameSaveState) ois.readObject();
                UserSession s = new UserSession();
                snap.copyTo(s);

                session = s;
                currentSlot = slot;

                setState(new MapState(this));
                return true;

            } catch (Exception ex) {
                System.out.println("Save file " + slot + " is corrupt. Deleting.");
                try {
                    Files.deleteIfExists(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }
        }
        return false;
    }

    /**
     * Deletes a save file associated with a slot.
     *
     * @param slotNum the save slot number
     * @return true if deleted, false otherwise
     */
    public boolean deleteSave(int slotNum) {
        Path savePath = getSavePath(slotNum);
        try {
            return Files.deleteIfExists(savePath);
        } catch (IOException e) {
            System.err.println("Error deleting save file: " + e.getMessage());
            return false;
        }
    }

    /**
     * Loads persisted configuration settings from disk.
     */
    private void loadConfig() {
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream("config.properties")) {
            props.load(in);
            double vol = Double.parseDouble(props.getProperty("volume", "0.5"));
            AudioManager.getInstance().setMasterVolume(vol);
        } catch (Exception e) {
            System.out.println("No config file found, using defaults.");
        }
    }

    /**
     * Saves configuration settings to disk.
     */
    public void saveConfig() {
        Properties props = new Properties();
        props.setProperty("volume",
                String.valueOf(AudioManager.getInstance().getMasterVolume()));

        try (FileOutputStream out = new FileOutputStream("config.properties")) {
            props.store(out, "Gooberlands Global Settings");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the save path for a slot.
     *
     * @param slot the slot index
     * @return the save file path
     */
    private Path getSavePath(int slot) {
        return Paths.get("save_" + slot + ".dat");
    }

    /**
     * Ensures a session exists for debugging or fallback scenarios.
     */
    public void ensureSession() {
        if (session == null) {
            session = new UserSession();
            session.setPlayerTrainer(createDefaultTrainer());
        }
    }

    /**
     * Creates a default trainer used for testing.
     *
     * @return a basic {@link Trainer}
     */
    private Trainer createDefaultTrainer() {
        List<Goober> team = UserSession.getRandomGoobersStatic(10);
        List<Item> items = new ArrayList<>();
        return new Trainer("Player", team, items);
    }

    /**
     * Returns the current game state.
     *
     * @return the active {@link GameState}
     */
    public GameState getState() {
        return currentState;
    }
}
