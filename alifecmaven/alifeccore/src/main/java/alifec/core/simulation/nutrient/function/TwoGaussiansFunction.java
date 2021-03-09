package alifec.core.simulation.nutrient.function;

import alifec.core.simulation.Defs;

public class TwoGaussiansFunction implements Function {
    public static final int ID = 5;

    @Override
    public float apply(int x, int y) {
        int R = Defs.RADIUS;
        float s1 = (float) Math.pow(Math.E, -((x - 0.6f * R) / (R / 4f)) *
                ((x - 0.6f * R) / (R / 4f)) -
                ((y - 0.6f * R) / (R / 4f)) *
                        ((y - 0.6f * R) / (R / 4f)));

        float s2 = (float) Math.pow(Math.E, -((x - 1.4f * R) / (R / 4f)) *
                ((x - 1.4f * R) / (R / 4f)) -
                ((y - 1.4f * R) / (R / 4f)) *
                        ((y - 1.4f * R) / (R / 4f)));

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
