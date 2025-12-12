package models;

import java.util.Arrays;
import java.util.List;

/**
 * Factory class responsible for creating {@link Goober} instances.
 * <p>
 * Centralizes Goober creation logic, including:
 * <ul>
 *   <li>Species-to-{@link GooberType} mapping</li>
 *   <li>Base stat adjustments per species</li>
 *   <li>Level safety checks</li>
 * </ul>
 * </p>
 * <p>
 * This ensures all balancing and construction rules live in one place.
 * </p>
 */
public class GooberFactory {

    /**
     * Central list of all available Goober species names.
     * <p>
     * Used for:
     * <ul>
     *   <li>Team selection UI</li>
     *   <li>Opponent generation</li>
     *   <li>Random Goober pools</li>
     * </ul>
     * </p>
     */
    public static final List<String> ALL_GOOBER_NAMES = Arrays.asList(
            "Tralalero Tralala",
            "DaBaby",
            "Peter Griffin",
            "John Pork",
            "Tung Tung Tung Sahur",
            "Doge",
            "Boss Baby",
            "Duolingo Bird",
            "William Shakespear",
            "Mahoraga",
            "LeBron",
            "Gigachad"
    );

    /** List of standard (non-special) Goobers available during selection and generation. */
    public static final List<String> NORMAL_GOOBERS = Arrays.asList(
            "Tralalero Tralala",
            "DaBaby",
            "Peter Griffin",
            "John Pork",
            "Tung Tung Tung Sahur",
            "Doge",
            "Boss Baby",
            "Duolingo Bird",
            "William Shakespear"
    );

    /** List of special Goobers with elevated stats or unique roles. */
    public static final List<String> SPECIAL_GOOBERS = Arrays.asList(
            "Mahoraga",
            "LeBron",
            "Gigachad"
    );

    /**
     * Creates a {@link Goober} of the given species name and level.
     * <p>
     * Species identity determines:
     * <ul>
     *   <li>{@link GooberType}</li>
     *   <li>Stat modifiers</li>
     *   <li>Growth behavior</li>
     * </ul>
     * </p>
     *
     * @param name  the species name (must exist in {@link #ALL_GOOBER_NAMES})
     * @param level the starting level (values less than 1 are clamped to 1)
     * @return a new {@link Goober} instance, or {@code null} if the name is unknown
     */
    public static Goober getGoober(String name, int level) {
        GooberType type = getTypeForName(name);
        if (type == null) {
            return null;
        }

        int hp = type.baseHp;
        int atk = type.baseAttack;
        double def = type.baseDefence;
        double crit = type.baseCrit;

        // Species-specific stat tuning
        switch (name) {

            // SUPPORTS
            case "Tralalero Tralala":
                hp += 10;
                atk -= 2;
                break;

            case "Boss Baby":
                hp -= 5;
                crit += 0.05;
                break;

            // TANKS
            case "DaBaby":
                hp += 5;
                atk += 4;
                break;

            case "Doge":
                def += 0.05;
                atk -= 3;
                break;

            // DAMAGERS
            case "Peter Griffin":
                hp += 20;
                def -= 0.03;
                break;

            case "Tung Tung Tung Sahur":
                atk += 6;
                hp -= 15;
                def -= 0.02;
                break;

            // ASSASSINS
            case "John Pork":
                hp += 8;
                break;

            case "Duolingo Bird":
                atk += 4;
                crit += 0.05;
                break;

            case "William Shakespear":
                crit += 0.10;
                atk -= 3;
                break;

            // SPECIALS
            case "Mahoraga":
                hp += 25;
                def += 0.03;
                break;

            case "LeBron":
                atk += 5;
                hp += 5;
                break;

            case "Gigachad":
                hp += 10;
                atk += 3;
                def += 0.02;
                break;
        }

        // Safety clamps
        hp = Math.max(10, hp);
        atk = Math.max(1, atk);
        def = Math.max(0.0, def);
        crit = Math.max(0.0, crit);

        int safeLevel = Math.max(1, level);

        return new Goober(
                name,
                type,
                hp,
                atk,
                crit,
                def,
                type.hpGrowth,
                type.attackGrowth,
                type.critGrowth,
                type.defenceGrowth,
                safeLevel
        );
    }

    /**
     * Convenience overload that creates a level 1 Goober.
     *
     * @param name the species name
     * @return a level 1 {@link Goober}, or {@code null} if unknown
     */
    public static Goober getGoober(String name) {
        return getGoober(name, 1);
    }

    /** @return immutable list of all Goober species names */
    public static List<String> getAllGooberNames() {
        return ALL_GOOBER_NAMES;
    }

    /** @return immutable list of special Goober species names */
    public static List<String> getAllSpecialGooberNames() {
        return SPECIAL_GOOBERS;
    }

    /** @return immutable list of normal Goober species names */
    public static List<String> getAllNormalGooberNames() {
        return NORMAL_GOOBERS;
    }

    /**
     * Resolves the {@link GooberType} associated with a species name.
     *
     * @param name the species name
     * @return the corresponding {@link GooberType}, or {@code null} if unknown
     */
    private static GooberType getTypeForName(String name) {
        switch (name) {
            case "Tralalero Tralala":
            case "Boss Baby":
                return GooberType.SUPPORT;

            case "DaBaby":
            case "Doge":
                return GooberType.TANK;

            case "Peter Griffin":
            case "Tung Tung Tung Sahur":
                return GooberType.DAMAGER;

            case "John Pork":
            case "Duolingo Bird":
            case "William Shakespear":
                return GooberType.ASSASSIN;

            case "Mahoraga":
            case "LeBron":
            case "Gigachad":
                return GooberType.SPECIAL;

            case "Lopunny":
                return GooberType.LOPUNNY;

            default:
                return null;
        }
    }
}
