/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

package lib.nutrients;

import lib.Defs;

import java.awt.*;


public class Famine extends Nutrient {
    public static final int ID = 100;

    /*
    * Genera una distribucion de Nutrientes uniforme!!
    */
    @Override
    public float getNutrient(Point p) {
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
