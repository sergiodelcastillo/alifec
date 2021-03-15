package alifec.core.simulation.rules;

import alifec.core.simulation.Cell;
import alifec.core.simulation.Environment;
import alifec.core.simulation.Movement;

/**
 * The class defines the rules of the environment
 * Every sub-class defines a rule that the
 * environment will use when the microorganism is moved.
 */
public interface ColonyRule {
    /**
     * The abstract method to apply the rules.
     */
    Status apply(Environment env, Cell mo, Movement mov, boolean mitosis);

    enum Status {
        CURRENT_DEAD,
        OPPONENT_DEAD,
        NONE
    }


}
