package alifec.core.simulation.nutrient;

import alifec.core.simulation.Defs;

/**
 * Created by nacho on 15/11/17.
 */
public class TestNutrient implements Nutrient {

    private float[][] nutrients;

    public TestNutrient() {
        nutrients = new float[Defs.DIAMETER][Defs.DIAMETER];
    }

    @Override
    public int getId() {
        return 250;
    }

    @Override
    public void moveRandom() {

    }

    @Override
    public float get(int x, int y) {
        return nutrients[x][y];
    }

    @Override
    public void eat(int x, int y, float ene) {
        nutrients[x][y] -= ene;
        if (nutrients[x][y] < 0f) {
            nutrients[x][y] = 0f;
        }
    }

    @Override
    public void init() {
        for (int i = 0; i < Defs.DIAMETER; i++) {
            for (int j = 0; j < Defs.DIAMETER; j++) {
                nutrients[i][j] = i * j;
            }
        }
    }

    @Override
    public String getName() {
        return "TestNutrient";
    }

    @Override
    public float[][] getNutrients() {
        return nutrients;
    }

    @Override
    public int getDx() {
        return 1;
    }

    @Override
    public int getDy() {
        return -1;
    }
}
