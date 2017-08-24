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
public class TwoGaussians extends Nutrient {
    public static final int ID = 5;
    /*
    * Genera una distribucion de nutrientes en forma de
    * dos gausianas ...
    */

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
