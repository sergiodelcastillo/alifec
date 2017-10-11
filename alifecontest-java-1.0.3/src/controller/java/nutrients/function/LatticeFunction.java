package controller.java.nutrients.function;

import data.java.Defs;

/**
 * Created by IntelliJ IDEA.
 * User: yeyo
 * Email: yeyo@druidalabs.com
 * Date: Nov 24, 2010
 * Time: 7:27:34 PM
 */
public class LatticeFunction implements Function {
    public static final int ID = 4;

    @Override
    public float apply(int x, int y) {
        int D = Defs.DIAMETER;

        return (x + y) % (D / 4) <= 1 || Math.abs(y - x) % (D / 3) <= 1 ?
                Math.min(Defs.MAX_NUTRI * (D - x) * (D - y) / (D * D) * 1.277f, Defs.MAX_NUTRI) : 0f;
    }

    @Override
    public String getName() {
        return "Lattice";
    }

    @Override
    public int getId() {
        return ID;
    }
}
