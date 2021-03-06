package controller.java.nutrients;

import controller.java.nutrients.function.Function;
import data.java.Defs;

/**
 * Created by IntelliJ IDEA.
 * User: yeyo
 * Email: yeyo@druidalabs.com
 * Date: Nov 24, 2010
 * Time: 7:28:21 PM
 */
public class FunctionNutrient extends Nutrient {
    private Function function;

    private float[][] nutrients;
    private int rx = random.nextInt(Defs.DIAMETER);
    private int ry = random.nextInt(Defs.DIAMETER);
    private int dx, dy;

    public FunctionNutrient(Function function) {
        this.function = function;
        this.nutrients = new float[Defs.DIAMETER][Defs.DIAMETER];


    }

    public void init() {
        for (int i = 0; i < Defs.DIAMETER; i++) {
            for (int j = 0; j < Defs.DIAMETER; j++) {
                nutrients[i][j] = function.apply(i, j);
            }
        }
    }

    @Override
    public int getId() {
        return function.getId();
    }

    @Override
    public String toString() {
        return function.getName();
    }

    @Override
    public void moveRandom() {
        if (random.nextInt(100) < 7) {
            dx = random.nextInt(3) - 1;
            dy = random.nextInt(3) - 1;
        }

        if (random.nextInt(100) > 70) {
            rx = (Defs.DIAMETER + rx + dx) % Defs.DIAMETER;
            ry = (Defs.DIAMETER + ry + dy) % Defs.DIAMETER;
        }
    }

    @Override
    public float get(int x, int y) {
        int xx = (nutrients.length + x + rx) % nutrients.length;
        int yy = (nutrients.length + y + ry) % nutrients.length;

        return nutrients[xx][yy];
    }


    @Override
    public float eat(int x, int y, float percent) {
        int xx = (nutrients.length + x + rx) % nutrients.length;
        int yy = (nutrients.length + y + ry) % nutrients.length;

        float foot = nutrients[xx][yy] * percent;

        nutrients[xx][yy] = Math.max(0f, nutrients[xx][yy]-foot);

        return foot;
    }
}
