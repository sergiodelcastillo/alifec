/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */
package lib.nutrients;

import lib.Defs;

import java.awt.*;

/**
 * warning: don't modify this class.
 */
public class Lattice extends Nutrient {

    public static final int ID = 4;

    @Override
    public float getNutrient(Point p) {
        boolean b = ((p.x + p.y) % Defs.DIAMETER / 4 <= 1) ||
                ((p.y - p.x) % Defs.DIAMETER / 3 <= 1);

        return (float) (b ? 1.277 * Defs.MAX_NUTRI *
                (Defs.DIAMETER - p.x) *
                (Defs.DIAMETER - p.y) /
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
