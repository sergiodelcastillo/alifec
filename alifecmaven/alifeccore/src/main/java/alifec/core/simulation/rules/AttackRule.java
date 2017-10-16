package alifec.core.simulation.rules;

import alifec.core.simulation.*;

import java.util.Random;

/**
 * @author: Sergio Del Castillo
 * @email: sergio.jose.delcastillo@gmail.com
 */

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
    public Status apply(Environment env, Cell mo, Movement mov, boolean mitosis) {
        if (!mov.isMoved()) {
            return Status.NONE;
        }

        if (!mov.isValid()) {
            return Status.NONE;
        }

        int x = mo.x + mov.dx;
        int y = mo.y + mov.dy;

        if (!env.inDish(x, y)) {
            return Status.NONE;
        }

        Cell enemy = env.getCell(x, y);

        if (enemy == null || enemy.id == mo.id) {
            //delegate to move role.
            return Status.NONE;
        }

        mo.ene -= Defs.LESS_MOVE;

        if (Float.compare(enemy.ene , mo.ene) == 0) {
            mo.ene += (0.001f * (random.nextInt(2) == 0 ? 1 : -1));
        }

        if (mo.isDied()) {
            env.killMO(mo.x, mo.y);
            return Status.CURRENT_DEAD;
        }

        if (enemy.ene > mo.ene) {
            doAttack(mo, enemy);
        } else {
            doAttack(enemy, mo);
        }

        if (mo.isDied()) {
            env.killMO(mo.x, mo.y);

            return Status.CURRENT_DEAD;
        } else if (enemy.isDied()) {
            env.killMO(enemy.x, enemy.y);
            env.moveMO(mo.x, mo.y, enemy.x, enemy.y);
            return Status.OPPONENT_DEAD;
        }

        return Status.NONE;
    }

    private void doAttack(Cell mo, Cell moWinner) {
        float diff = moWinner.ene - mo.ene;

        moWinner.ene -= 0.075f * mo.ene;
        mo.ene -= diff;
    }

}
