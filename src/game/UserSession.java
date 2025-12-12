/**
 * File: UserSession.java
 * Purpose:
 *      Represents the runtime state of a single game playthrough.
 */

package game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ai.Difficulty;
import items.Item;
import items.ItemFactory;
import models.Goober;
import models.GooberFactory;
import models.Trainer;

/**
 * Manages the runtime state of a single playthrough.
 * <p>
 * This class acts as the central container for:
 * <ul>
 *   <li>Player identity and trainer data</li>
 *   <li>Procedurally generated campaign levels</li>
 *   <li>Currency and inventory progression</li>
 *   <li>Difficulty and gameplay mode settings</li>
 * </ul>
 * </p>
 * <p>
 * When saved, this data is serialized into {@link GameSaveState}.
 * </p>
 */
public class UserSession {

    /** The player's trainer. */
    private Trainer playerTrainer;

    /** Campaign level data. */
    private List<Level> levels = new ArrayList<>();

    /** Highest level index completed (0 = none). */
    private int highestCompleted;

    /** Player currency. */
    private int coins;

    /** Whether this session is single-player. */
    private boolean soloPlay;

    /** Difficulty setting for this session. */
    private Difficulty difficulty;

    /**
     * Constructs a new UserSession with default difficulty.
     */
    public UserSession() {
        difficulty = Difficulty.EASY;
    }

    /* ------------------------------------------------------------
     * Level Generation
     * ------------------------------------------------------------ */

    /**
     * Procedurally generates the single-player campaign.
     * <p>
     * Generation algorithm:
     * <ol>
     *   <li>Selects all trainers except the player's own</li>
     *   <li>Creates two combat rounds with shuffled order</li>
     *   <li>Scales Goober levels for increasing difficulty</li>
     *   <li>Adds a final boss level against the Joker</li>
     * </ol>
     * </p>
     */
    public void generateLevels() {
        levels.clear();

        List<String> names = new ArrayList<>(Trainer.NAMES);
        names.remove(playerTrainer.getName());

        int levelNum = 1;

        for (int round = 0; round < 2; round++) {
            Collections.shuffle(names);
            for (int i = 0; i < 3; i++) {
                int gooberLevel = (round * 30) + (i * 10);
                if (round == 0 && i == 0) gooberLevel++;

                Trainer trainer = new Trainer(
                        names.get(i),
                        getRandomGoobers(gooberLevel),
                        getRandomItems()
                );

                levels.add(new Level(trainer, levelNum++));
            }
        }

        addBossLevel(levelNum);
    }

    /**
     * Adds the final boss level.
     *
     * @param levelNum the sequence number of the boss level
     */
    private void addBossLevel(int levelNum) {
        List<Goober> team = new ArrayList<>();
        team.add(GooberFactory.getGoober("Lopunny", 67));

        Trainer joker = new Trainer("Joker", team, getRandomItems());
        levels.add(new Level(joker, levelNum));
    }

    /**
     * Generates a random team of three Goobers.
     *
     * @param level the Goober level
     * @return a list of Goobers
     */
    private List<Goober> getRandomGoobers(int level) {
        List<String> names = new ArrayList<>(Goober.NAMES);
        Collections.shuffle(names);

        List<Goober> goobers = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            goobers.add(GooberFactory.getGoober(names.get(i), level));
        }
        return goobers;
    }

    /**
     * Generates a random inventory of two items.
     *
     * @return an item list
     */
    private List<Item> getRandomItems() {
        List<String> names = new ArrayList<>(Item.NAMES);
        Collections.shuffle(names);

        List<Item> items = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            items.add(ItemFactory.createItem(names.get(i)));
        }
        return items;
    }

    /* ------------------------------------------------------------
     * Progression & Accessors
     * ------------------------------------------------------------ */

    public void setPlayerTrainer(Trainer t) {
        playerTrainer = t;
    }

    public Trainer getPlayerTrainer() {
        return playerTrainer;
    }

    /**
     * Retrieves a level by its 1-based index.
     *
     * @param idx the level number (1–7)
     * @return the {@link Level} or {@code null} if invalid
     */
    public Level getLevel(int idx) {
        if (idx < 1 || idx > levels.size()) return null;
        return levels.get(idx - 1);
    }

    public List<Level> getLevels() {
        return levels;
    }

    /**
     * Determines whether a level is unlocked.
     *
     * @param levelNum the level number
     * @return {@code true} if accessible
     */
    public boolean isLevelUnlocked(int levelNum) {
        if (levelNum < 1 || levelNum > levels.size()) return false;
        return levelNum == 1 || highestCompleted >= levelNum - 1;
    }

    /**
     * Marks a level as completed and updates progression.
     *
     * @param level the defeated level
     */
    public void markLevelCompleted(Level level) {
        level.markCompleted();
        highestCompleted = Math.max(highestCompleted, level.getLevelNum());
    }

    public int getHighestCompleted() {
        return highestCompleted;
    }

    public void setHighestCompleted(int highestCompleted) {
        this.highestCompleted = highestCompleted;
    }

    /**
     * Updates progression without modifying level state.
     *
     * @param levelNum the completed level number
     */
    public void updateHighestCompleted(int levelNum) {
        highestCompleted = Math.max(highestCompleted, levelNum);
    }

    /**
     * Debug helper to force progression forward.
     */
    public void incrementLevel() {
        highestCompleted = Math.min(levels.size(), highestCompleted + 1);
    }

    /* ------------------------------------------------------------
     * Currency & Settings
     * ------------------------------------------------------------ */

    public void addCoins(int n) {
        coins += n;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public boolean isSoloPlay() {
        return soloPlay;
    }

    public void setSoloPlay(boolean soloPlay) {
        this.soloPlay = soloPlay;
    }

    public void setLevels(List<Level> levels) {
        this.levels = new ArrayList<>(levels);
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty d) {
        difficulty = d;
    }

    /**
     * Static helper for generating random Goobers (used for testing/debug).
     *
     * @param level the Goober level
     * @return a random Goober list
     */
    public static List<Goober> getRandomGoobersStatic(int level) {
        List<String> names = new ArrayList<>(Goober.NAMES);
        Collections.shuffle(names);

        List<Goober> goobers = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            goobers.add(GooberFactory.getGoober(names.get(i), level));
        }
        return goobers;
    }
}
