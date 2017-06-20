/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

package lib.nutrients;

import java.awt.*;


/**
 * To generate a Nutrient distribution in the Agar.
 * warning: don't modify this class
 */
public abstract class Nutrient {

    /**
     * @return: a Nutrient in point p
     */
    public abstract float getNutrient(Point p);

    /**
     * @return distribution Identifier
     */
    public abstract int getID();

    /**
     * @return name of distribution!
     */
    public abstract String toString();
}
