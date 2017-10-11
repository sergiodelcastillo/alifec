package alifec.core.simulation.rules;

import alifec.core.simulation.Cell;
import alifec.core.simulation.Colony;
import alifec.core.simulation.Environment;
import alifec.core.simulation.Movement;

/**
 * The class defines the rules of the environment
 * Every sub-class defines a rule that the
 * environment will use when the microorganism is moved.
 *
 * @Warning: don't modify this class
 */
public interface ColonyRule {


    /**
     * The abstract method to apply the rules.
     */
    boolean apply(Environment env, Colony c, Colony enemy, Cell mo, Movement mov, boolean mitosis);

}
