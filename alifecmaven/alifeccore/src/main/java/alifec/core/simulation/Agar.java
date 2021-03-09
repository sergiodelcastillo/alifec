package alifec.core.simulation;

import alifec.core.simulation.nutrient.Nutrient;

import java.util.Objects;

/**
 * The Agar class contains the nutrients that microorganisms can eat.
 */
public class Agar {
    private Nutrient current;

    public Agar() {
        this.current = null;
    }


    public void eat(int x, int y, float ene) {
        this.current.eat(x, y, ene);
    }

    public void moveRandom() {
        if (Objects.nonNull(current)) current.moveRandom();
    }

    /**
     * use this method to set the distribution of nutrients
     */
    public void setNutrient(Nutrient temp) {
        current = temp;
        current.init();
    }

    /**
     * Get distribution of Nutrient
     *
     * @return an integer representing Nutrient distribution identifier.
     */
    public int getDistNutri() {
        return current.getId();
    }

    /**
     * @return the among of nutrient in the position x,y.
     */
    public float getNutrient(int x, int y) {
        if (Objects.isNull(current)) return 0f;

        return current.get(x, y);
    }

    public Nutrient getCurrent() {
        return current;
    }

}
