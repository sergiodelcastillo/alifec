package alifec.core.simulation.rules;

import alifec.core.simulation.Cell;
import alifec.core.simulation.Colony;
import alifec.core.simulation.Environment;
import alifec.core.simulation.Movement;
import alifec.core.simulation.rules.ColonyRule;

/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

public class EatRule implements ColonyRule {

    /**
     * The current MO increment(eat) 1% of agar in the current position.
     * @param env the environment of the competition
     * @param mo current MO
     * @param mov the results of call the method MO.move
     * @param mitosis the results of call the method MO.mitosis
     * @return false
     */
    public Status apply(Environment env, Cell mo, Movement mov, boolean mitosis) {
        mo.ene += env.getAgar().eat(mo.x, mo.y);
        return Status.NONE;
    }
}
