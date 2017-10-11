/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

package controller.java;

import controller.java.nutrients.*;
import controller.java.nutrients.function.*;
import data.java.Config;
import data.java.Defs;
import data.java.Log;
import exceptions.LoadNutrientsException;
import exceptions.SaveNutrientsException;

import java.io.*;
import java.util.*;

/**
 * The Agar class contain the nutrients that the microorganisms can eat.
 */
public class Agar {
    private static final Hashtable<Integer, Nutrient> NUTRIENTS;

    static {
        NUTRIENTS = new Hashtable<Integer, Nutrient>();

        NUTRIENTS.put(InclinedPlaneFunction.ID, new FunctionNutrient(new InclinedPlaneFunction()));
        NUTRIENTS.put(VerticalBarFunction.ID, new FunctionNutrient(new VerticalBarFunction()));
        NUTRIENTS.put(RingsFunction.ID, new FunctionNutrient(new RingsFunction()));
        NUTRIENTS.put(LatticeFunction.ID, new FunctionNutrient(new LatticeFunction()));
        NUTRIENTS.put(TwoGaussiansFunction.ID, new FunctionNutrient(new TwoGaussiansFunction()));
        NUTRIENTS.put(FamineFunction.ID, new FunctionNutrient(new FamineFunction()));
        NUTRIENTS.put(BallsNutrient.ID, new BallsNutrient());
    }

    public static Nutrient getNutrient(int id) {
        return NUTRIENTS.get(id);
    }

    public static Enumeration<Nutrient> getAllNutrients() {
        return NUTRIENTS.elements();
    }

    public static void createNutrientFile(String path) throws IOException {
        ArrayList<Integer> list = new ArrayList<Integer>();

        for (Enumeration<Integer> e = NUTRIENTS.keys(); e.hasMoreElements();) {
            list.add(e.nextElement());
        }

        System.out.println("Adding Nutrients");

        File f = new File(path);
        FileWriter writer = new FileWriter(f, false);
        BufferedWriter buffer = new BufferedWriter(writer);

        for (Integer id : list) {
            buffer.append(String.valueOf(id)).append("\n");
            System.out.println(getNutrient(id).toString() + " [OK]");
        }

        buffer.close();
        writer.close();
    }

    public static int getCount() {
        return NUTRIENTS.size();
    }

    private Hashtable<Integer, Nutrient> available;
    private Nutrient nutrient;

    public Agar() throws LoadNutrientsException {
        load();
    }

    /**
     * @param x
     * @param y
     * @return eat Defs.EAT_PERCENT percent of the nutrient in the position (x,y).
     * @see Defs#EAT_PERCENT
     */
    public float eat(int x, int y) {
        return nutrient.eat(x, y, Defs.EAT_PERCENT);
    }

    public void moveRandom() {
        nutrient.moveRandom();
    }

    /**
     * Use this method to eat the distribution of nutrients
     *
     * @param id: Distribution ID:
     *            1 : Inclined Plane
     *            2 : Vertical Var
     *            3 : Rings
     *            4 : Lattice
     *            5 : Two Gaussian
     *            100: Famine
     * @return if was successful
     */
    public boolean setNutrient(int id) {
        this.nutrient = available.get(id);

        if (nutrient != null) {
            nutrient.init();
            return true;
        }
        return false;
    }

    /**
     * Get distribution of Nutrient
     *
     * @return an integer representing Nutrient distribution identifier.
     */
    public int getNutrientId() {
        return nutrient == null ? -1 : nutrient.getId();
    }

    /**
     * @param x
     * @param y
     * @return the among of nutrient in the position x,y.
     */
    public float getNutrient(int x, int y) {
        return nutrient == null ? 0.0f : nutrient.get(x, y);
    }

    /**
     * @return a list of all nutrients.
     */
    public Enumeration<Nutrient> getNutrients() {
        return available.elements();
    }

    public void load() throws LoadNutrientsException {
        try {
            Log.println("Loading Nutrients");
            available = new Hashtable<Integer, Nutrient>();

            FileReader fr = new FileReader(Config.getInstance().getAbsoluteNutrient());
            BufferedReader in = new BufferedReader(fr);
            String line;

            while ((line = in.readLine()) != null) {

                try {
                    Nutrient nut = getNutrient(Integer.parseInt(line.trim()));

                    if (nut != null) {
                        available.put(nut.getId(), nut);
                        Log.println(nut.toString() + " [OK]");
                    }
                } catch (NumberFormatException ex) {
                    Log.println("Ignoring: " + line);
                }

            }
            fr.close();
            in.close();
            Log.println("");
        } catch (IOException ex) {
            throw new LoadNutrientsException("Cant Load the nutrients", ex);
        }
    }

    public void saveByName(ArrayList<String> list) throws SaveNutrientsException {
        save(getIds(list));
    }

    void save(ArrayList<Integer> ids) throws SaveNutrientsException {
        try {
            Log.println("Updating Nutrients");
            File f = new File(Config.getInstance().getAbsoluteNutrient());
            FileWriter writer = new FileWriter(f, false);
            BufferedWriter buffer = new BufferedWriter(writer);

            for (Integer id : ids) {
                buffer.append(String.valueOf(id)).append("\n");
                try {
                    Log.println(getNutrient(id).toString() + " [OK]");
                } catch (NullPointerException ignored) {

                }
            }

            buffer.close();
            writer.close();
        } catch (IOException ex) {
            throw new SaveNutrientsException("Cant save the nutrients", ex);
        }
    }

    ArrayList<Integer> getIds(ArrayList<String> list) {
        ArrayList<Integer> ids = new ArrayList<Integer>();

        for (String s : list) {
            for (Enumeration<Nutrient> nut = NUTRIENTS.elements(); nut.hasMoreElements();) {
                Nutrient nutrient = nut.nextElement();
                if (nutrient.toString().equals(s)) {
                    ids.add(nutrient.getId());
                    break;
                }
            }
        }
        return ids;
    }

    public boolean contain(int id) {
        return available != null && available.containsKey(id);
    }
}
