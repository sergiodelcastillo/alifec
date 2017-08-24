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

    public boolean apply(Environment env, Colony c, Colony enemy, Cell mo, Movement mov, boolean mitosis) {
        //TODO: imporove it
        if (mo == null)
            throw new IllegalArgumentException("Illegal Argument");

        mo.ene += env.eat(mo.x, mo.y);
        return false;
    }
}
