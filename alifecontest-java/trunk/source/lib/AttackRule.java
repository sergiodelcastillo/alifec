package lib; /**
 * @author: Sergio Del Castillo
 * @email: sergio.jose.delcastillo@gmail.com
 */

public class AttackRule extends ColonyRule {
    public boolean apply(Colony c, Colony enemy, Cell mo, Movement mov, boolean mitosis) {
        if (mov == null || mo == null || enemy == null || c == null)
            throw new IllegalArgumentException("Illegal Argument");

        if (!mov.isMoved() || !mov.isValid()) return false;

        int x = mo.pos.x + mov.dx;
        int y = mo.pos.y + mov.dy;

        if (!env.inDish(x, y) || !canCompete(mo.pos, x, y))
            return false;

        Cell enemyMO = env.microorganism[x][y];
        mo.ene -= Defs.LESS_MOVE;

        if (mo.ene <= 0.0f) {
            env.killMO(mo.pos.x, mo.pos.y);
            return true;
        }

        if (enemyMO.ene == mo.ene)
            mo.ene += 0.01f * (new java.util.Random().nextInt(2) == 0 ? 1 : -1);

        if (enemyMO.ene > mo.ene) {
            float diff = enemyMO.ene - mo.ene;
            enemyMO.ene = enemyMO.ene + 0.075f * mo.ene;
            mo.ene = mo.ene - diff;

            if (mo.ene <= 0.0f) {
                env.killMO(mo.pos.x, mo.pos.y);
                return true;
            }
        } else {
            float diff = mo.ene - enemyMO.ene;
            mo.ene = mo.ene + 0.075f * enemyMO.ene;
            enemyMO.ene = enemyMO.ene - diff;

            if (enemyMO.ene <= 0.0f) {
                env.killMO(enemyMO.pos.x, enemyMO.pos.y);
                env.moveMO(mo.pos, enemyMO.pos);
                return true;
            }
        }

        return false;
    }

    private boolean canCompete(java.awt.Point a, int x, int y) {
        return env.microorganism[a.x][a.y] != null &&
                env.microorganism[x][y] != null &&
                env.microorganism[a.x][a.y].id != env.microorganism[x][y].id;
    }
}
