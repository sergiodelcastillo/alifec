package alifec.core.simulation;

import alifec.core.simulation.nutrients.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The Agar class contain all nutrient that microorganisms can eat.
 */
public class Agar {

    private Logger logger = LogManager.getLogger(getClass());

    private int nutrientId;
    private float[][] nutrients;

    private int dx;
    private int dy;

    public static final Nutrient nutrient[] = {
            new InclinedPlane(),
            new VerticalBar(),
            new Rings(),
            new Lattice(),
            new TwoGaussians(),
            new Famine()
    };


    public Agar() {
        this.nutrients = new float[Defs.DIAMETER][Defs.DIAMETER];
        this.dx = new Random().nextInt(Defs.DIAMETER);
        this.dy = new Random().nextInt(Defs.DIAMETER);
        this.nutrientId = -1;
    }

    /**
     * @param px it is an integer between 0 and 50
     * @param py it is an integer between 0 and 50
     * @return The energy that the MO have eaten in the position (px, py).
     */
    public void eat(int px, int py, float eat) {
        if (px > Defs.DIAMETER || px < 0 ||
                py > Defs.DIAMETER || py < 0) {
            logger.warn("The Position (" + px + "," + py + ") is not valid.");
            throw new IllegalArgumentException("The Position (" + px + "," + py + ") is not valid.");
        }

        int x = (Defs.DIAMETER + px + dx) % Defs.DIAMETER;
        int y = (Defs.DIAMETER + py + dy) % Defs.DIAMETER;

        nutrients[x][y] -= eat;


        if (nutrients[x][y] < 0)
            nutrients[x][y] = 0;
    }

    public void moveRandom() {
        dx += new Random().nextInt(3) - 1;
        dy += new Random().nextInt(3) - 1;

        if (dx == Defs.DIAMETER || dx == -Defs.DIAMETER) dx = 0;
        if (dy == Defs.DIAMETER || dy == -Defs.DIAMETER) dy = 0;

    }

    /**
     * use this method to set the distribution of nutrients
     *
     * @param id: Distribution ID:
     *            1 : Inclined Plane
     *            2 : Vertical Var
     *            3 : Rings
     *            4 : Lattice
     *            5 : Two Gaussians
     *            100: Famine
     */
    public boolean setNutrient(int id) {
        Nutrient nutri;

        switch (id) {
            case InclinedPlane.ID:
                nutri = new InclinedPlane();
                break;
            case VerticalBar.ID:
                nutri = new VerticalBar();
                break;
            case Rings.ID:
                nutri = new Rings();
                break;
            case Lattice.ID:
                nutri = new Lattice();
                break;
            case TwoGaussians.ID:
                nutri = new TwoGaussians();
                break;
            case Famine.ID:
                nutri = new Famine();
                break;
            default:
                throw new IllegalArgumentException("There is not nutrient distribution with id = " + id + ".");
        }

        for (int i = 0; i < Defs.DIAMETER; i++)
            for (int j = 0; j < Defs.DIAMETER; j++)
                nutrients[i][j] = nutri.getNutrient(i, j);

        nutrientId = id;
        return true;
    }

    /**
     * Get distribution of Nutrient
     *
     * @return an integer representing Nutrient distribution identifier.
     */
    public int getDistNutri() {
        return nutrientId;
    }

    /**
     * @return the among of nutrient in the position x,y.
     */
    public float getNutrient(int x, int y) {
        if (x < 0 || x >= Defs.DIAMETER)
            throw new IllegalArgumentException("Value " + x + "is wrong. The x value must be 0<=x<=50.");
        if (y < 0 || y >= Defs.DIAMETER)
            throw new IllegalArgumentException("Value " + y + "is wrong. The y value must be 0<=y<=50.");

        int xx = (Defs.DIAMETER + x + dx) % Defs.DIAMETER;
        int yy = (Defs.DIAMETER + y + dy) % Defs.DIAMETER;
        try {
            return nutrients[xx][yy];
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return 0;
        }
    }

    /**
     * @param nut the name of nutrient
     * @return the nutrient identifier
     */
    public int getNutrientID(String nut) {
        for (Nutrient n : nutrient) {
            if (nut.toLowerCase().equals(n.toString().toLowerCase())) {
                return n.getID();
            }
        }
        return -1;
    }

    /**
     * @return a list of all nutrients.
     */
    public List<String> getNutrients() {
        List<String> res = new ArrayList<>(Defs.DIAMETER);
        for (Nutrient n : nutrient) {
            res.add(n.toString());
        }
        return res;
    }
}
