package alifec.core.simulation.rules;

import alifec.core.simulation.*;

/**
 * @author: Sergio Del Castillo
 * @email: sergio.jose.delcastillo@gmail.com
 */

public class EatRule implements ColonyRule {

    /**
     * The current MO increment(eat) 1% of agar in the current position.
     *
     * @param env     the environment of the competition
     * @param mo      current MO
     * @param mov     the results of call the method MO.move
     * @param mitosis the results of call the method MO.mitosis
     * @return false
     */
    public Status apply(Environment env, Cell mo, Movement mov, boolean mitosis) {
        if (mo.isDied()) {
            env.killMO(mo.x, mo.y);
            return Status.CURRENT_DEAD;
        }

        Agar agar = env.getAgar();
        float eat = Defs.EAT_PERCENT * agar.getNutrient(mo.x, mo.y);

        env.getAgar().eat(mo.x, mo.y, eat);
        mo.ene += eat;

        return Status.NONE;
    }
}
