package alifec.core.simulation.nutrients;

import alifec.core.simulation.Defs;
import alifec.core.simulation.Position;


/**
 * It generates the Rings nutrient distribution
 */
public class Rings implements Nutrient {
    public static final int ID = 3;

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
