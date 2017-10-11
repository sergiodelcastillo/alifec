package controller.java.nutrients;

import data.java.Defs;

import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: yeyo
 * Email: yeyo@druidalabs.com
 * Date: Nov 24, 2010
 * Time: 8:16:48 PM
 */
public class BallsNutrient extends Nutrient {
    public static final int ID = 7;

    int dx1, dx2, dy1, dy2;
    int rx1, rx2, ry1, ry2;

    int d = 7;
    float d2 = d * d;
    private static Random random2 = new Random();
    private float ball1[][];
    private float ball2[][];

    public BallsNutrient() {
        ball1 = new float[Defs.DIAMETER][Defs.DIAMETER];
        ball2 = new float[Defs.DIAMETER][Defs.DIAMETER];
    }

    private void init(float[][] ball) {
        for (int i = 0; i < Defs.DIAMETER; i++) {
            for (int j = 0; j < Defs.DIAMETER; j++) {
                float ds = (i - Defs.RADIUS) * (i - Defs.RADIUS) + (j - Defs.RADIUS) * (j - Defs.RADIUS);
                ball[i][j] = Math.max(0, (ds < 2 * d2) ? (((d2 - ds) / (1.2f*d2) * Defs.MAX_NUTRI)) : 0f);
            }
        }
        dx1 = dx2 = dy1 = dy2 = 0;
        rx1 = rx2 = ry1 = ry2 = Defs.RADIUS;
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
        if (random.nextInt(3) > 0) dx1 = relative(dx1 , rx1);
        if (random2.nextInt(6) > 0) dx2 = relative(dx2 , rx2);
        if (random.nextInt(6) > 0) dy1 = relative(dy1 , ry1);
        if (random2.nextInt(3) > 0) dy2 = relative(dy2 , ry2);
    }

    private int relative(int dx, int rx) {
        if ((dx + Defs.RADIUS)== rx) return dx;
        return( dx + Defs.RADIUS) < rx ? dx + 1 : dx - 1;
    }

    @Override
    public float get(int x, int y) {
        int xx1 = (ball1.length + x - dx1) % ball1.length;
        int yy1 = (ball1.length + y - dy1) % ball1.length;
        int xx2 = (ball2.length + x - dx2) % ball2.length;
        int yy2 = (ball2.length + y - dy2) % ball2.length;

        return Math.min(ball1[xx1][yy1] + ball1[xx2][yy2], Defs.MAX_NUTRI);
    }

    @Override
    public float eat(int x, int y, float percent) {
        int xx1 = (ball1.length + x - dx1) % ball1.length;
        int yy1 = (ball1.length + y - dy1) % ball1.length;
        int xx2 = (ball2.length + x - dx2) % ball2.length;
        int yy2 = (ball2.length + y - dy2) % ball2.length;

        float foot = get(x, y) * percent;

        if (foot <= ball1[xx1][yy1]) {
            ball1[xx1][yy1] = Math.max(0f, ball1[xx1][yy1] - foot);
        } else {
            float less = foot - ball1[xx1][yy1];
            ball1[xx1][yy1] = 0f;
            ball2[xx2][yy2] = Math.max(0f, ball2[xx2][yy2] - less);
        }

        return foot;
    }

    @Override
    public void init() {
        init(ball1);
        init(ball2);
    }

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public String toString() {
        return "Balls";
    }
}
