/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

package alifec.core.simulation.nutrients;

import alifec.core.simulation.Defs;

import java.awt.*;

/**
 * warning: don't modify this class
 */
public class InclinedPlane extends Nutrient {
    public static final int ID = 1;

    /**
     * It generates the Inclined Plane nutrient distribution
     */
    @Override
    public float getNutrient(Point p) {
        return (float) (Defs.MAX_NUTRI * (Defs.DIAMETER - p.x) * (Defs.DIAMETER - p.y) /
                (Defs.DIAMETER * Defs.DIAMETER) / 2.875);
    }


    @Override
    public String toString() {
        return "InclinedPlane";
    }

    @Override
    public int getID() {
        return ID;
    }
}
