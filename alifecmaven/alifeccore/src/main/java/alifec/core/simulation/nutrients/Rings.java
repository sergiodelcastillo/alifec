/**
 * @author Yeyo
 * @mail: sergio.jose.delcastillo@gmail.com
 */
package alifec.core.simulation.nutrients;

import alifec.core.simulation.Defs;

import java.awt.*;

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
    public float getNutrient(Point p) {
        int R =
Defs.RADIUS;
        boolean b = ((p.x - R) * (p.x - R) + (p.y - R) * (p.y - R) > 40) &&
                ((p.x - R) * (p.x - R) + (p.y - R) * (p.y - R) < 115);

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
