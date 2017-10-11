package alifec.core.simulation.rules;

import alifec.core.simulation.Cell;
import alifec.core.simulation.Colony;
import alifec.core.simulation.Environment;
import alifec.core.simulation.Movement;

/**
 * The class defines the rules of the environment
 * Every sub-class defines a rule that the
 * environment will use when the microorganism is moved.
 */
public interface ColonyRule {
    enum Status {
        CURRENT_DEAD,
        OPPONENT_DEAD,
        NONE
    }

    /**
     * The abstract method to apply the rules.
     */
    Status apply(Environment env, Cell mo, Movement mov, boolean mitosis);


}
