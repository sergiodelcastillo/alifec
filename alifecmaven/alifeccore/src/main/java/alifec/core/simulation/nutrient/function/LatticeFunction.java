package alifec.core.simulation.nutrient.function;

import alifec.core.simulation.Defs;

public class LatticeFunction implements Function {
    public static final int ID = 4;

    @Override
    public float apply(int x, int y) {
        int D = Defs.DIAMETER;

        return (x + y) % (D / 4) <= 1 || Math.abs(y - x) % (D / 3) <= 1 ?
                Math.min(1.277f * Defs.MAX_NUTRI * (D - x) * (D - y) / (D * D), Defs.MAX_NUTRI) : 0f;
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
