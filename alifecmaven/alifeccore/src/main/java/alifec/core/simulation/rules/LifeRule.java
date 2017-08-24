package alifec.core.simulation.rules;

import alifec.core.simulation.*;
import alifec.core.simulation.rules.ColonyRule;

/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

public class LifeRule implements ColonyRule {
    public boolean apply(Environment env, Colony c, Colony enemy, Cell mo, Movement mov, boolean mitosis) {
        //TODO: imporove it
        if (c == null || mo == null || mo.pos == null)
            throw new IllegalArgumentException("Illegal Argument");

        mo.ene -= Defs.LESS_LIVE;

        if (mo.ene <= 0.0f) {
            env.killMO(mo.pos.x, mo.pos.y);
            return true;
        }

        return false;
    }
}
