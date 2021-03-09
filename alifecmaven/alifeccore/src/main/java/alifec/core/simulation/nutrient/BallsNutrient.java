package alifec.core.simulation.nutrient;

import alifec.core.simulation.Defs;

import java.util.Random;


public class BallsNutrient implements Nutrient {
    public static final int ID = 7;

    int dx1, dx2, dy1, dy2;
    int rx1, rx2, ry1, ry2;

    int d = 8;
    float d2 = d * d;
    private Random random = new Random();
    private Random random2 = new Random();

    private float[][] ball1;
    private float[][] ball2;
    private float[][] result;

    public BallsNutrient() {
        ball1 = new float[Defs.DIAMETER][Defs.DIAMETER];
        ball2 = new float[Defs.DIAMETER][Defs.DIAMETER];
        result = new float[Defs.DIAMETER][Defs.DIAMETER];
    }

    private void init(float[][] ball) {
        for (int i = 0; i < Defs.DIAMETER; i++) {
            for (int j = 0; j < Defs.DIAMETER; j++) {
                float ds = (i - Defs.RADIUS) * (i - Defs.RADIUS) + (j - Defs.RADIUS) * (j - Defs.RADIUS);
                ball[i][j] = Math.max(0, (ds < 2 * d2) ? (((d2 - ds) / (1.2f * d2) * Defs.MAX_NUTRI)) : 0f);
            }
        }
        dx1 = dx2 = dy1 = dy2 = 0;
        rx1 = rx2 = ry1 = ry2 = Defs.RADIUS;
        updateResult();
    }

    @Override
    public void moveRandom() {
        if (dx1 + Defs.RADIUS == rx1 &&
                dx2 + Defs.RADIUS == rx2 &&
                dy1 + Defs.RADIUS == ry1 &&
                dy2 + Defs.RADIUS == ry2) {
            do {
                rx1 = random.nextInt(Defs.DIAMETER);
                ry1 = random.nextInt(Defs.DIAMETER);
            } while (((rx1 - Defs.RADIUS) * (rx1 - Defs.RADIUS) + (ry1 - Defs.RADIUS) * (ry1 - Defs.RADIUS)) >
                    (d - Defs.RADIUS) * (d - Defs.RADIUS));

            do {
                rx2 = random2.nextInt(Defs.DIAMETER);
                ry2 = random2.nextInt(Defs.DIAMETER);
            } while (((rx2 - Defs.RADIUS) * (rx2 - Defs.RADIUS) + (ry2 - Defs.RADIUS) * (ry2 - Defs.RADIUS)) >
                    (d - Defs.RADIUS) * (d - Defs.RADIUS));
        }
        if (random.nextInt(3) > 0) dx1 = relative(dx1, rx1);
        if (random2.nextInt(6) > 0) dx2 = relative(dx2, rx2);
        if (random.nextInt(6) > 0) dy1 = relative(dy1, ry1);
        if (random2.nextInt(3) > 0) dy2 = relative(dy2, ry2);

        //update result matrix
        updateResult();
    }

    private int relative(int dx, int rx) {
        if ((dx + Defs.RADIUS) == rx) return dx;
        return (dx + Defs.RADIUS) < rx ? dx + 1 : dx - 1;
    }

    /*@Override
    public float get(int x, int y) {
        int xx1 = (ball1.length + x - dx1) % ball1.length;
        int yy1 = (ball1.length + y - dy1) % ball1.length;
        int xx2 = (ball2.length + x - dx2) % ball2.length;
        int yy2 = (ball2.length + y - dy2) % ball2.length;

        return Math.min(ball1[xx1][yy1] + ball1[xx2][yy2], Defs.MAX_NUTRI);
    }*/

    public float get(int x, int y) {
        return result[x][y];
    }

    @Override
    public void eat(int x, int y, float eat) {
        int xx1 = (ball1.length + x - dx1) % ball1.length;
        int yy1 = (ball1.length + y - dy1) % ball1.length;
        int xx2 = (ball2.length + x - dx2) % ball2.length;
        int yy2 = (ball2.length + y - dy2) % ball2.length;


        if (eat <= ball1[xx1][yy1]) {
            ball1[xx1][yy1] -= eat;
        } else {
            float less = eat - ball1[xx1][yy1];
            ball1[xx1][yy1] = 0f;
            ball2[xx2][yy2] -= less;
        }

        if (ball1[xx1][yy1] < 0f) ball1[xx1][yy1] = 0f;
        if (ball2[xx1][yy1] < 0f) ball2[xx1][yy1] = 0f;

        //keep result updated
        result[x][y] = Math.min(ball1[xx1][yy1] + ball1[xx2][yy2], Defs.MAX_NUTRI);
    }

    @Override
    public void init() {
        init(ball1);
        init(ball2);
        updateResult();
    }

    private void updateResult() {
        for (int x = 0; x < Defs.DIAMETER; x++) {
            for (int y = 0; y < Defs.DIAMETER; y++) {
                int xx1 = (ball1.length + x - dx1) % ball1.length;
                int yy1 = (ball1.length + y - dy1) % ball1.length;
                int xx2 = (ball2.length + x - dx2) % ball2.length;
                int yy2 = (ball2.length + y - dy2) % ball2.length;

                result[x][y] = Math.min(ball1[xx1][yy1] + ball1[xx2][yy2], Defs.MAX_NUTRI);
            }
        }
    }

    @Override
    public String getName() {
        return "Balls";
    }

    @Override
    public float[][] getNutrients() {
        return result;
    }

    @Override
    public int getDx() {
        return 0;
    }

    @Override
    public int getDy() {
        return 0;
    }

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public String toString() {
        return getName();
    }

}
