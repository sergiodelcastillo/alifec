package alifec.core.simulation.rules; /**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

import alifec.core.simulation.Cell;
import alifec.core.simulation.Colony;
import alifec.core.simulation.Environment;
import alifec.core.simulation.Movement;

/**
 * The class defines the rules of the environment
 * Every child of this class defines a rule that the
 * environment was using to apply to the colony of
 * microorganisms when they move.
 *
 * @Warning: don't modify this class
 */
public interface ColonyRule {


    /**
     * The abstract method to apply the rules.
     */
    boolean apply(Environment env, Colony c, Colony enemy, Cell mo, Movement mov, boolean mitosis);

}
