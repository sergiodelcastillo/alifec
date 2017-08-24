package alifec.core.simulation.rules;

import alifec.core.simulation.*;
import alifec.core.simulation.rules.ColonyRule;

/**
 * @author: Sergio Del Castillo
 * @email: sergio.jose.delcastillo@gmail.com
 */

public class AttackRule implements ColonyRule {
    public boolean apply(Environment env, Colony c, Colony enemy, Cell mo, Movement mov, boolean mitosis) {
        //TODO: imporove it
        if (mov == null || mo == null || enemy == null || c == null)
            throw new IllegalArgumentException("Illegal Argument");

        if (!mov.isMoved() || !mov.isValid()) return false;

        int x = mo.x + mov.dx;
        int y = mo.y + mov.dy;

        if (!env.inDish(x, y) || !canCompete(env, mo.x, mo.y, x, y))
            return false;

        Cell enemyMO = env.getMO(x, y);
        mo.ene -= Defs.LESS_MOVE;

        if (mo.ene <= 0.0f) {
            env.killMO(mo.x, mo.y);
            return true;
        }

        if (enemyMO.ene == mo.ene)
            mo.ene += 0.01f * (new java.util.Random().nextInt(2) == 0 ? 1 : -1);

        if (enemyMO.ene > mo.ene) {
            float diff = enemyMO.ene - mo.ene;
            enemyMO.ene = enemyMO.ene + 0.075f * mo.ene;
            mo.ene = mo.ene - diff;

            if (mo.ene <= 0.0f) {
                env.killMO(mo.x, mo.y);
                return true;
            }
        } else {
            float diff = mo.ene - enemyMO.ene;
            mo.ene = mo.ene + 0.075f * enemyMO.ene;
            enemyMO.ene = enemyMO.ene - diff;

            if (enemyMO.ene <= 0.0f) {
                env.killMO(enemyMO.x, enemyMO.y);
                env.moveMO(mo.x, mo.y, enemyMO.x, enemyMO.y);
                return true;
            }
        }

        return false;
    }

    private boolean canCompete(Environment env, int ax, int ay, int x, int y) {
        return env.getMO(ax, ay) != null &&
                env.getMO(x, y) != null &&
                env.getMO(ax, ay).id != env.getMO(x, y).id;
    }
}
