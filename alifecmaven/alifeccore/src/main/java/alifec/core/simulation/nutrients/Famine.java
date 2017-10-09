
package alifec.core.simulation.nutrients;

import alifec.core.simulation.Defs;
import alifec.core.simulation.Position;


/**
 * Generates an uniform distribution of nutrients
 */
public class Famine implements Nutrient {
    public static final int ID = 100;


    @Override
    public float getNutrient(int x, int y) {
        return (float) (Defs.MAX_NUTRI / 11.062);
    }

    @Override
    public String toString() {
        return "Famine";
    }

    @Override
    public int getID() {
        return ID;
    }

}
