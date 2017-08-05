package alifec.core.simulation;

/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

public class LifeRule extends ColonyRule {
    public boolean apply(Colony c, Colony enemy, Cell mo, Movement mov, boolean mitosis) {
        if (c == null || mo == null || mo.pos == null)
            throw new IllegalArgumentException("Illegal Argument");

        mo.ene -= Defs.LESS_LIVE;

        if (mo.ene <= 0.0f) {
            env.killMO(mo.pos.x, mo.pos.y);
            return true;
        }

        return false;
    }
}
