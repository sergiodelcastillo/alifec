package lib; /**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

/**
 * The class defines the rules of the environment
 * Every child of this class defines a rule that the
 * environment was using to apply to the colony of
 * microorganisms when they move.
 *
 * @Warning: don't modify this class
 */
public abstract class ColonyRule {
    /**
     * the environment
     */
    static Environment env;

    /**
     * The abstract method to apply the rules.
     */
    public abstract boolean apply(Colony c, Colony enemy, Cell mo, Movement mov, boolean mitosis);

}
