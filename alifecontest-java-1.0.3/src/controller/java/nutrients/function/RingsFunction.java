package controller.java.nutrients.function;

import data.java.Defs;

/**
 * Created by IntelliJ IDEA.
 * User: yeyo
 * Email: yeyo@druidalabs.com
 * Date: Nov 24, 2010
 * Time: 7:47:48 PM
 */
public class RingsFunction implements Function {
    public static final int ID = 3;

    @Override
    public float apply(int x, int y) {
        int R = Defs.RADIUS;
        boolean b = ((x - R) * (x - R) + (y - R) * (y - R) > 40) &&
                ((x - R) * (x - R) + (y - R) * (y - R) < 115);

        return b ? Defs.MAX_NUTRI / 1.15f : 0.0f;
    }

    @Override
    public String getName() {
        return "Rings";
    }

    @Override
    public int getId() {
        return ID;
    }

}
