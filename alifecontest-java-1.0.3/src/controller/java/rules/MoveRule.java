/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

package controller.java.rules;

import controller.java.Cell;
import controller.java.Environment;
import controller.java.Movement;
import controller.java.Position;
import data.java.Defs;

public class MoveRule implements ColonyRule {

    /**
     * The current mo can move at a empty relative position if the relative position is in dish.
     *
     * @param env     the environment of the competition
     * @param mo      current MO
     * @param mov     the results of call the method MO.move
     * @param mitosis the results of call the method MO.mitosis
     * @return
     */
    public StatusRule apply(Environment env, Cell mo, Movement mov, boolean mitosis) {
        if (!mov.isMoved()) {
            return StatusRule.NOTHING;
        }

        Position newPos = new Position(mo.x + mov.dx, mo.y + mov.dy);

        if (!env.inDish(newPos.x, newPos.y)) {
            mo.ene -= Defs.LESS_MOVE;

            if (mo.isDied()) {
                env.killMO(mo.x, mo.y);
                return StatusRule.MO_DEAD;
            }

            return StatusRule.NOTHING;
        }

        Cell cell = env.getCell(newPos.x, newPos.y);

        if (cell != null && cell.id != mo.id) {
            // delegate to  attack rule
            return StatusRule.NOTHING;
        }

        mo.ene -= Defs.LESS_MOVE;

        if (mo.isDied()) {
            env.killMO(mo.x, mo.y);
            return StatusRule.MO_DEAD;
        }

        if (!mov.isValid()) {
            return StatusRule.NOTHING;
        }

        env.moveMO(mo.x, mo.y, newPos.x, newPos.y);

        return StatusRule.NOTHING;
    }
}
