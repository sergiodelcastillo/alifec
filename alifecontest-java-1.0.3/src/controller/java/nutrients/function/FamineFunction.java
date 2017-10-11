package controller.java.nutrients.function;

import data.java.Defs;

/**
 * Created by IntelliJ IDEA.
 * User: yeyo
 * Email: yeyo@druidalabs.com
 * Date: Nov 24, 2010
 * Time: 7:53:11 PM
 */
public class FamineFunction implements Function {
    public static final int ID = 100;

    @Override
    public float apply(int x, int y) {
        return Defs.MAX_NUTRI / 11.062f;
    }

    @Override
    public String getName() {
        return "Famine";
    }

    @Override
    public int getId() {
        return ID;
    }
}
