package alifec.core.simulation.nutrients;

import alifec.core.simulation.Defs;
import alifec.core.simulation.Position;

/**
 * It generates the Lattice nutrient distribution
 */
public class Lattice implements Nutrient {

    public static final int ID = 4;

    @Override
    public float getNutrient(int px, int py) {
        boolean b = ((px + py) % Defs.DIAMETER / 4 <= 1) ||
                ((py - px) % Defs.DIAMETER / 3 <= 1);

        return (float) (b ? 1.277 * Defs.MAX_NUTRI *
                (Defs.DIAMETER - px) *
                (Defs.DIAMETER - py) /
                (Defs.DIAMETER * Defs.DIAMETER) : 0.0);
    }

    @Override
    public String toString() {
        return "Lattice";
    }

    @Override
    public int getID() {
        return ID;
    }
}
