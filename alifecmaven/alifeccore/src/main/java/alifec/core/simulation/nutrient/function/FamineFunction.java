package alifec.core.simulation.nutrient.function;


import alifec.core.simulation.Defs;


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
