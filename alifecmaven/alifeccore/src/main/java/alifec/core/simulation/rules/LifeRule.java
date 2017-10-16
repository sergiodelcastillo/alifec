package alifec.core.simulation.rules;

import alifec.core.simulation.*;


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
