
package alifec.core.simulation.nutrients;

import alifec.core.simulation.Defs;
import alifec.core.simulation.Position;


/**
 * It generates the Two Gaussians nutrient distribution
 */
public class TwoGaussians implements Nutrient {
    public static final int ID = 5;

    @Override
    public float getNutrient(int px, int py) {
        int R = Defs.RADIUS;
        float s1 = (float) Math.pow(Math.E, -((px - 0.6 * R) / (R / 4)) *
                ((px - 0.6 * R) / (R / 4)) -
                ((py - 0.6 * R) / (R / 4)) *
                        ((py - 0.6 * R) / (R / 4)));

        float s2 = (float) Math.pow(Math.E, -((px - 1.4 * R) / (R / 4)) *
                ((px - 1.4 * R) / (R / 4)) -
                ((py - 1.4 * R) / (R / 4)) *
                        ((py - 1.4 * R) / (R / 4)));

        return Defs.MAX_NUTRI * (s1 + s2);
    }

    @Override
    public String toString() {
        return "TwoGaussians";
    }

    @Override
    public int getID() {
        return ID;
    }
}
