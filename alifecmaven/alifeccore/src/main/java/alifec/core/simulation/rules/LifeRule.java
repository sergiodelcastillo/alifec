package alifec.core.simulation.rules;

import alifec.core.simulation.Cell;
import alifec.core.simulation.Defs;
import alifec.core.simulation.Environment;
import alifec.core.simulation.Movement;


public class LifeRule implements ColonyRule {
    public Status apply(Environment env, Cell mo, Movement mov, boolean mitosis) {
        mo.ene -= Defs.LESS_LIVE;

        if (mo.isDied()) {
            env.killMO(mo.x, mo.y);
            return Status.CURRENT_DEAD;
        }

        return Status.NONE;
    }
}
