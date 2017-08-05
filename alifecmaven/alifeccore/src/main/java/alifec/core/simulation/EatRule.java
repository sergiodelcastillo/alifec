package alifec.core.simulation; /**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

public class EatRule extends ColonyRule {

    public boolean apply(Colony c, Colony enemy, Cell mo, Movement mov, boolean mitosis) {
        if (mo == null)
            throw new IllegalArgumentException("Illegal Argument");

        mo.ene += env.agar.eat(mo.pos);
        return false;
    }
}
