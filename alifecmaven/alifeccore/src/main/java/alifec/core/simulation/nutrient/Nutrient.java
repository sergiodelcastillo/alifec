package alifec.core.simulation.nutrient;

/**
 * Created by Sergio Del Castillo on 16/10/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 *
 * To generate a Nutrient distribution in the Agar.
 */
public interface Nutrient {
    /**
     * @return distribution Identifier
     */
    int getId();

    /**
     * @return name of distribution!
     */
    String toString();

    /**
     * Move the distribution of nutrients
     */
    void moveRandom();


    /**
     * @return: a Nutrient in point p
     */
    float get(int x, int y);

    /**
     * eat the nutrient in the position x,y,
     * @param x
     * @param y
     * @param ene
     */
    void eat(int x, int y, float ene);

    void init();

    String getName();
}