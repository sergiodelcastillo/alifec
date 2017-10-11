/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

package controller.java.rules;

import controller.java.Cell;
import controller.java.Environment;
import controller.java.Movement;

public class EatRule implements ColonyRule {

    /**
     * The current MO increment(eat) 1% of agar in the current position. 
     * @param env the environment of the competition
     * @param mo current MO
     * @param mov the results of call the method MO.move
     * @param mitosis the results of call the method MO.mitosis
     * @return false
     */
    public StatusRule apply(Environment env, Cell mo, Movement mov, boolean mitosis) {
        mo.ene += env.getAgar().eat(mo.x, mo.y);
        return StatusRule.NOTHING;
    }
}
