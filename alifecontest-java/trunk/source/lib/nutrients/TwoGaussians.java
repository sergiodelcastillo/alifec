/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */
package lib.nutrients;

import lib.Defs;

import java.awt.*;

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
    public float getNutrient(Point p) {
        int R = Defs.RADIUS;
        float s1 = (float) Math.pow(Math.E, -((p.x - 0.6 * R) / (R / 4)) *
                ((p.x - 0.6 * R) / (R / 4)) -
                ((p.y - 0.6 * R) / (R / 4)) *
                        ((p.y - 0.6 * R) / (R / 4)));

        float s2 = (float) Math.pow(Math.E, -((p.x - 1.4 * R) / (R / 4)) *
                ((p.x - 1.4 * R) / (R / 4)) -
                ((p.y - 1.4 * R) / (R / 4)) *
                        ((p.y - 1.4 * R) / (R / 4)));

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