/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

package controller.java.rules;

import controller.java.Cell;
import controller.java.Environment;
import controller.java.Movement;
import data.java.Defs;

public class LifeRule implements ColonyRule {

    /**
     * The mo will less energy for live.
     *
     * @param env     the environment of the competition
     * @param mo      current MO
     * @param mov     the results of call the method MO.move
     * @param mitosis the results of call the method MO.mitosis
     * @return true if current MO is dead
     */
    public StatusRule apply(Environment env, Cell mo, Movement mov, boolean mitosis) {
        mo.ene -= Defs.LESS_LIVE;

        if (mo.isDied()) {
            env.killMO(mo.x, mo.y);
            return StatusRule.MO_DEAD;
        }

        return StatusRule.NOTHING;
    }
}
