package controller.java.nutrients;

import java.util.Random;

/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */


/**
 * To generate a Nutrient distribution in the Agar.
 * warning: don't modify this class
 */
public abstract class Nutrient {
    protected static final Random random = new Random();
    /**
     * @return distribution Identifier
     */
    public abstract int getId();

    /**
     * @return name of distribution!
     */
    public abstract String toString();

    /**
     * Move the distribution of nutrients
     */
    public abstract void moveRandom();


    /**
     * @return: a Nutrient in point p
     */
    public abstract float get(int x, int y);

    /**
     * eat the nutrient in the position x,y,
     * @param x
     * @param y
     */
    public abstract float eat(int x, int y, float percent);

    public abstract void init();
}
