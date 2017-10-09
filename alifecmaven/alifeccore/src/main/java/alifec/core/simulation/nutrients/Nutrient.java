
package alifec.core.simulation.nutrients;


/**
 * To generate a Nutrient distribution in the Agar.
 */
public interface Nutrient {

    /**
     * @return: a Nutrient in point p
     */
    float getNutrient(int x, int y);

    /**
     * @return distribution Identifier
     */
    int getID();

    /**
     * @return name of distribution!
     */
    String toString();
}
