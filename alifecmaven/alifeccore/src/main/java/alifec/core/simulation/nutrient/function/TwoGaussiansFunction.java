package alifec.core.simulation.nutrient.function;

import alifec.core.simulation.Defs;

public class TwoGaussiansFunction implements Function {
    public static final int ID = 5;

    @Override
    public float apply(int x, int y) {
        int R = Defs.RADIUS;
        float s1 = (float) Math.pow(Math.E, -((x - 0.6 * R) / (R / 4)) *
                ((x - 0.6 * R) / (R / 4)) -
                ((y - 0.6 * R) / (R / 4)) *
                        ((y - 0.6 * R) / (R / 4)));

        float s2 = (float) Math.pow(Math.E, -((x - 1.4 * R) / (R / 4)) *
                ((x - 1.4 * R) / (R / 4)) -
                ((y - 1.4 * R) / (R / 4)) *
                        ((y - 1.4 * R) / (R / 4)));

        return Defs.MAX_NUTRI * (s1 + s2);
    }

    @Override
    public String getName() {
        return "TwoGaussians";
    }

    @Override
    public int getId() {
        return ID;
    }
}
