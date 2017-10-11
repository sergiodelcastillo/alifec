package controller.java.nutrients.function;

import data.java.Defs;

/**
 * Created by IntelliJ IDEA.
 * User: yeyo
 * Email: yeyo@druidalabs.com
 * Date: Nov 24, 2010
 * Time: 7:50:04 PM
 */
public class VerticalBarFunction implements Function {
    public static final int ID = 2;

    @Override
    public float apply(int x, int y) {
        return (x > Defs.RADIUS - 5) && (x > Defs.RADIUS + 5) ? Defs.MAX_NUTRI / 4.2f : 0.0f;
    }

    @Override
    public String getName() {
        return "VerticalBar";
    }

    @Override
    public int getId() {
        return ID;
    }
}
