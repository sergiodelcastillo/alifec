/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

package data.java;

/**
 * This class define the important constant to alifec.
 * A player can use them.
 */
public class Defs {
    /**
     * Petri dish radius.
     */
    public static final int RADIUS = 25;

    /**
     * Petri dish Diameter.
     */
    public static final int DIAMETER = 2 * RADIUS;


    /**
     * Number of MOs within each colony at start de simulations
     */
    public static final int MO_INITIAL = 50;

    /**
     * Relative maximum of the nutrient distribution.
     */
    public static final float MAX_NUTRI = 5000.0f;

    /**
     * Initial MO energy.
     */
    public static final float E_INITIAL = 1000;

    /**
     * Less energy to live.
     */
    public static final float LESS_LIVE = 5;

    /**
     * Less energy to move.
     */
    public static final float LESS_MOVE = 10;

    /**
     * Max length of strings.
     */
    public static final int MAX_LENGTH = 25;

    /**
     * Addition percent to eat
     */
    public static final float EAT_PERCENT = 0.01f;
}
