

package alifec.core.simulation.nutrients;

import alifec.core.simulation.Defs;
import alifec.core.simulation.Position;

/**
 * It generates the Vertical Bar nutrient distribution
 */
public class VerticalBar implements Nutrient {
    public static final int ID = 2;


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
