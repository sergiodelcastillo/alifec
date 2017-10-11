/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

package controller.java.rules;

import controller.java.Cell;
import controller.java.Environment;
import controller.java.Movement;
import data.java.Defs;

import java.util.Random;

public class AttackRule implements ColonyRule {
    private static final Random random = new Random();
    /**
     * The current MO and its opponent will fight if:
     * 1) The destination of the MO is an opponent position.
     * 2) The movement is relative and not empty (0,0)
     *
     * @param env     the environment of the competition
     * @param mo      current MO
     * @param mov     the results of call the method MO.move
     * @param mitosis the results of call the method MO.mitosis
     * @return true if the mo is dead.
     */
    public StatusRule apply(Environment env, Cell mo, Movement mov, boolean mitosis) {
        if (!mov.isMoved()) {
            return StatusRule.NOTHING;
        }

        if (!mov.isValid()) {
            return StatusRule.NOTHING;
        }

        int x = mo.x + mov.dx;
        int y = mo.y + mov.dy;

        if (!env.inDish(x, y)) {
            return StatusRule.NOTHING;
        }

        Cell enemy = env.getCell(x, y);

        if (enemy == null || (enemy != null && enemy.id == mo.id)) {
            //delegate to move role.
            return StatusRule.NOTHING;
        }

        mo.ene -= Defs.LESS_MOVE;

        if (enemy.ene == mo.ene) {
            mo.ene += (0.001f * (random.nextInt(100) > 50 ? 1 : -1));
        }

        if (mo.isDied()) {
            env.killMO(mo.x, mo.y);
            return StatusRule.MO_DEAD;
        }

        if (enemy.ene > mo.ene) {
            doAttack(mo, enemy);
        } else {
            doAttack(enemy, mo);
        }

        if (mo.isDied()) {
            env.killMO(mo.x, mo.y);
               
/*            if (enemy.isDied()) {
                env.killMO(enemy.x, enemy.y);
                return StatusRule.ALL_DEAD;
            }*/

            return StatusRule.MO_DEAD;
        } else if (enemy.isDied()) {
            env.killMO(enemy.x, enemy.y);
            env.moveMO(mo.x, mo.y, enemy.x, enemy.y);
            return StatusRule.ENEMY_DEAD;
        }

        return StatusRule.NOTHING;
    }

    private void doAttack(Cell mo, Cell moWinner) {
        float diff = moWinner.ene - mo.ene;

        moWinner.ene -= 0.075f * mo.ene;
        mo.ene -= diff;
    }


}
