package alifec.core.simulation;

import alifec.core.exception.NutrientException;
import alifec.core.simulation.nutrient.*;
import alifec.core.simulation.nutrient.function.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;


/**
 * The Agar class contain all nutrient that microorganisms can eat.
 */
public class Agar {

    private Logger logger = LogManager.getLogger(getClass());

    private static Hashtable<Integer, Nutrient> nutrients;

    static {
        nutrients = new Hashtable<>();

        nutrients.put(InclinedPlaneFunction.ID, new FunctionBasedNutrient(new InclinedPlaneFunction()));
        nutrients.put(VerticalBarFunction.ID, new FunctionBasedNutrient(new VerticalBarFunction()));
        nutrients.put(RingsFunction.ID, new FunctionBasedNutrient(new RingsFunction()));
        nutrients.put(LatticeFunction.ID, new FunctionBasedNutrient(new LatticeFunction()));
        nutrients.put(TwoGaussiansFunction.ID, new FunctionBasedNutrient(new TwoGaussiansFunction()));
        nutrients.put(FamineFunction.ID, new FunctionBasedNutrient(new FamineFunction()));
        nutrients.put(BallsNutrient.ID, new BallsNutrient());
    }
    public static final boolean existsId(Integer id){
        if (id == null) return false;

        return nutrients.get(id) != null;
    }

    private Nutrient current;

    public Agar() {
        this.current = null;
    }

    public void eat(int x, int y, float ene) {
        this.current.eat(x, y, ene);
    }

    public void moveRandom() {
        if (current != null) current.moveRandom();
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
    public void setNutrient(int id) throws NutrientException {
        Nutrient temp = nutrients.get(id);

        if (temp == null)
            throw new NutrientException("There is not nutrient distribution with id = " + id + ".");

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
        if (current == null) return 0f;

        return current.get(x, y);
    }

    /**
     * @param nut the name of nutrient
     * @return the nutrient identifier
     */
    public static Nutrient getNutrientByName(String nut) {
        for (Nutrient nutrient : nutrients.values()) {
            if (nutrient.toString().equals(nut)) return nutrient;
        }

        return null;
    }
    public static Hashtable<Integer, Nutrient> getAllNutrient(){
        return nutrients;
    }

    /**
     * @return a list of all nutrients.
     */
    public List<String> getNutrientsName() {
        Collection<Nutrient> nutrient = nutrients.values();
        List<String> res = new ArrayList<>(nutrient.size());

        for (Nutrient n : nutrient) {
            res.add(n.toString());
        }

        return res;
    }
}
