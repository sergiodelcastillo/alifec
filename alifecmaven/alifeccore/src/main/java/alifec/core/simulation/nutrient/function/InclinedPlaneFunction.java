package alifec.core.simulation.nutrient.function;


import alifec.core.simulation.Defs;


public class InclinedPlaneFunction implements Function {
    public static final int ID = 1;

    @Override
    public float apply(int x, int y) {
        return (float) (Defs.MAX_NUTRI * (Defs.DIAMETER - x) * (Defs.DIAMETER - y) /
                (Defs.DIAMETER * Defs.DIAMETER) / 2.875);
    }

    @Override
    public String getName() {
        return "InclinedPlane";
    }

    @Override
    public int getId() {
        return ID;
    }
}
