package alifec.core.simulation.nutrients;

import alifec.core.simulation.Defs;
import alifec.core.simulation.Position;


/**
 * It generates the Inclined Plane nutrient distribution
 */
public class InclinedPlane implements Nutrient {
    public static final int ID = 1;


    @Override
    public float getNutrient(int px, int py) {
        return (float) (Defs.MAX_NUTRI * (Defs.DIAMETER - px) * (Defs.DIAMETER - py) /
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
