/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

package alifec.core.simulation.nutrients;

import alifec.core.simulation.Defs;
import alifec.core.simulation.Position;

/**
 * warning: don't modify this class
 */
public class VerticalBar extends Nutrient {
    public static final int ID = 2;
    /*
    * Genera una distribucion de nutrientes en forma de una
    * barra vertical.
    */

    @Override
    public float getNutrient(int px, int py) {
        boolean b = (px > Defs.RADIUS - 5) && (px > Defs.RADIUS + 5);

        return (float) (b ? Defs.MAX_NUTRI / 4.2 : 0.0);
    }


    @Override
    public String toString() {
        return "VerticalBar";
    }

    @Override
    public int getID() {
        return ID;
    }
}
