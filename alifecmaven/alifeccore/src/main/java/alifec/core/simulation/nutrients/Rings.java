/**
 * @author Yeyo
 * @mail: sergio.jose.delcastillo@gmail.com
 */
package alifec.core.simulation.nutrients;

import alifec.core.simulation.Defs;
import alifec.core.simulation.Position;


/**
 * warning: don't modify this class
 */
public class Rings extends Nutrient {
    public static final int ID = 3;
    /*
    * Genera una distribucion de nutrientes en forma 
    * de un anillo.
    */

    @Override
    public float getNutrient(int px, int py) {
        int R = Defs.RADIUS;

        boolean b = ((px - R) * (px - R) + (py - R) * (py - R) > 40) &&
                ((px - R) * (px - R) + (py - R) * (py - R) < 115);

        return b ? Defs.MAX_NUTRI / 1.008f : 0.0f;
    }

    @Override
    public String toString() {
        return "Rings";
    }

    @Override
    public int getID() {
        return ID;
    }
}
