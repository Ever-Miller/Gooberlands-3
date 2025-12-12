/**
 * File: TargetType.java
 * Purpose:
 *      Defines the possible target scopes for moves and effects
 *      in battle.
 */

package effects;

/**
 * Enumerates the different target scopes that a move or effect
 * can apply to.
 */
public enum TargetType {

    /** Targets the acting Goober itself. */
    SELF,

    /** Targets the opposing active Goober. */
    ENEMY,

    /** Targets all allied Goobers that are not fainted. */
    ALL_ALLIES,

    /** Targets all opposing Goobers that are not fainted. */
    ALL_ENEMIES
}
