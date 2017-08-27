package alifec.core.simulation;

import alifec.core.simulation.nutrients.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The Agar class contain all nutrient that each Microorganism can eat.
 * Don't modify this class
 */
public class Agar {

    private Logger logger = LogManager.getLogger(getClass());

    private int nutri_id = -1;
    private float[][] nutrients;

    private int dx = new Random().nextInt(50);
    private int dy = new Random().nextInt(50);

    public static final Nutrient nutrient[] = {
            new InclinedPlane(),
            new VerticalBar(),
            new Rings(),
            //			new Lattice(),
            new TwoGaussians(),
            new Famine()};

    /**
     * Default constructor: can´t instance this class out of this package!
     */
    public Agar() {
        nutrients = new float[50][50];
    }

    /**
     * @param p must be a point into (0,0) and (50,50)
     * @return
     */
    public float eat(int px, int py) {
        if (px > Defs.DIAMETER || px < 0 ||
                py > Defs.DIAMETER || py < 0) {
            logger.warn("The Position is not valid: (" + px + "," + py + ")");
            throw new IllegalArgumentException("The Position is not valid: (" + px + "," + py + ")");
        }
        if (nutri_id < 0) {
            logger.warn("The Nutrient id is not valid: " + nutri_id);
            throw new IllegalArgumentException("The Nutrient id is not valid: " + nutri_id);
        }

        int x = (nutrients.length + px + dx) % nutrients.length;
        int y = (nutrients.length + py + dy) % nutrients.length;

        float foot = 0.01f * nutrients[x][y];
        nutrients[x][y] -= foot;


        if (nutrients[x][y] < 0)
            nutrients[x][y] = 0;

        return foot;
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
    public void setNutrient(int id) {
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
                throw new IllegalArgumentException("IllegalArgument");
        }

        for (int i = 0; i < 50; i++)
            for (int j = 0; j < 50; j++)
                nutrients[i][j] = nutri.getNutrient(i, j);

        nutri_id = id;
    }

    /**
     * Get distribution of Nutrient
     *
     * @return an integer representing Nutrient distribution identifier.
     */
    public int getDistNutri() {
        return nutri_id;
    }

    /**
     * @return the among of nutrient in the position x,y.
     */
    public float getNutrient(int x, int y) {
        if (nutri_id == -1 ||
                x < 0 || x >= Defs.DIAMETER ||
                y < 0 || y >= Defs.DIAMETER)
            throw new IllegalArgumentException("Illegal Argument");


        int xx = (nutrients.length + x + dx) % nutrients.length;
        int yy = (nutrients.length + y + dy) % nutrients.length;
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
        List<String> res = new ArrayList<>();
        for (Nutrient n : nutrient) {
            res.add(n.toString());
        }
        return res;
    }
}
