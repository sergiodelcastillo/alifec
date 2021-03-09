package alifec.core.simulation.nutrient.function;


import alifec.core.simulation.Defs;

public class VerticalBarFunction implements Function {
    public static final int ID = 2;

    @Override
    public float apply(int x, int y) {
        //return (x > Defs.RADIUS - 5) && (x > Defs.RADIUS + 5) ? Defs.MAX_NUTRI / 4.2f : 0.0f;
        return (x > Defs.RADIUS + 5) ? Defs.MAX_NUTRI / 4.2f : 0.0f;
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
