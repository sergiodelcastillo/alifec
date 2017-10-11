package controller.java.battle;

import controller.java.Agar;
import controller.java.Validator;
import controller.java.nutrients.Nutrient;

import java.io.*;

/**
 * Created by Sergio Del Castillo
 * User: yeyo
 * Date: Jun 25, 2010
 * Time: 2:41:23 PM
 * Email: yeyo@druidalabs.com
 */
public abstract class AbstractBattle {
    protected String name1;
    protected String name2;
    protected String nutrient;

    protected int nutrientId;

    protected float energy1;
    protected float energy2;

    public AbstractBattle() {
    }

    /**
     * @param line represent a string with the follow pattern:
     *             name1, name2, NUTRIENTS, ene1, ene2
     *             This constructor initialize the private attributes with the line <b>line</b>.
     * @throws IllegalArgumentException if the line does not match with the following pattern:
     *                                  < name1, name2, NUTRIENTS, ene1, ene2 >
     *                                  name1: String representing the name of the first colony;
     *                                  name2: String representing the name of the second colony;
     *                                  NUTRIENTS: String representing the name of the nutrient distribution;
     *                                  ene1: float representing the energy of the first colony;
     *                                  ene2: float representing the energy of the second colony;
     */
    public AbstractBattle(String line) throws IllegalArgumentException {
        if (line == null || line.equals(""))
            throw new IllegalArgumentException("Bad line");

        String[] tmp = line.split(",");

        if (tmp.length != 5)
            throw new IllegalArgumentException("Bad line");

        try {
            name1 = tmp[0].trim();
            energy1 = Float.parseFloat(tmp[3]);

            name2 = tmp[1].trim();
            energy2 = Float.parseFloat(tmp[4]);

            setNutrientId(Integer.parseInt(tmp[2].trim()));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Bad line");
        }

        check();
    }

    public AbstractBattle(String n1, String n2, int nut, float  ene1, float ene2) throws IllegalArgumentException{
        this.name1 = n1;
        this.name2 = n2;
        this.energy1 = ene1;
        this.energy2 = ene2;
        this.nutrientId = nut;

        check();
    }

    private void check() throws IllegalArgumentException{
        if (!this.isValid())
            throw new IllegalArgumentException("Bad line");

        Nutrient nut = Agar.getNutrient(nutrientId);

        if (nut == null) {
            throw new IllegalArgumentException("Invalid nutrient id=" + nutrientId);
        }
        nutrient = nut.toString();
    }

    /**
     * validate the information of the battle.
     *
     * @return true if the name1, name2 and nutrient are not null and matches with the pattern:[a-zA-Z0-9]{3,}
     */
    public boolean isValid() {
        return Validator.validateColonyName(name1) &&
                Validator.validateColonyName(name2) &&
                Validator.validateNutrientId(nutrientId) &&
                energy1 >= 0.0f &&
                energy2 >= 0.0f;
    }

    /**
     * @param path is the path of the file to append.
     * @throws java.io.IOException
     */
    public void append(String path) throws IOException {
        FileWriter fw = new FileWriter(path, true);
        BufferedWriter pw = new BufferedWriter(fw);

        // print line.
        pw.append(this.toCSV());
        pw.append('\n');

        pw.close();
        fw.close();
    }

    /**
     * Delete the battle of the file of battles.
     *
     * @param path URL of battle to delete
     * @throws java.io.IOException
     */
    public boolean delete(String path) throws IOException {
        if (!new File(path).renameTo(new File(path + "_tmp"))) {
            return false;
        }

        String line;
        BufferedReader reader = new BufferedReader(new FileReader(path + "_tmp"));
        PrintWriter writer = new PrintWriter(path);

        while ((line = reader.readLine()) != null) {
            if (!line.equals(this.toCSV())) {
                writer.println(line);
            }
        }

        writer.close();
        reader.close();
        return new File(path + "_tmp").delete();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractBattle battle = (AbstractBattle) o;

        if (Float.compare(battle.energy1, energy1) != 0) return false;
        if (Float.compare(battle.energy2, energy2) != 0) return false;

        if (nutrientId != battle.nutrientId) {
            return false;
        }

        return (name1.equals(battle.name1) && name2.equals(battle.name2)) ||
                (name1.equals(battle.name2) && name2.equals(battle.name1));
    }

    @Override
    public int hashCode() {
        int result = name1 != null ? name1.hashCode() : 0;
        result = 31 * result + (name2 != null ? name2.hashCode() : 0);
        result = 31 * result + (energy1 != +0.0f ? Float.floatToIntBits(energy1) : 0);
        result = 31 * result + (energy2 != +0.0f ? Float.floatToIntBits(energy2) : 0);
        result = 31 * result + nutrientId;

        return result;
    }

    /**
     * @param name colony name
     * @return true if the name is equals to firstName or secondName.
     */
    public boolean contain(String name) {
        return name1.equals(name) || name2.equals(name);
    }

    public boolean contain(AbstractBattle r) {
        if (name1.equals(r.getFirstName()) && name2.equals(r.getSecondName()) && nutrientId == r.getNutrientId())
            return true;

        return name1.equals(r.getSecondName()) && name2.equals(r.getFirstName()) && nutrientId == r.getNutrientId();

    }

    /**
     * @return a string representing the a CSV line
     */
    public String toCSV() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(name1);
        buffer.append(",");
        buffer.append(name2);
        buffer.append(",");
        buffer.append(nutrientId);
        buffer.append(",");
        buffer.append(energy1);
        buffer.append(",");
        buffer.append(energy2);
        buffer.append(",");

        return buffer.toString();
    }

    /**
     * @return the colony name of the first opponent
     */
    public String getFirstName() {
        return name1;
    }

    /**
     * @return the colony name of the second opponent
     */
    public String getSecondName() {
        return name2;
    }

    /**
     * @return the among of energy of the first opponent
     */
    public float getFirstEnergy() {
        return energy1;
    }

    /**
     * @return the among of energy of the second opponent
     */
    public float getSecondEnergy() {
        return energy2;
    }


    /**
     * @return name of nutrient distribution
     */
    public int getNutrientId() {
        return nutrientId;
    }

    public String getNutrientName() {
        return nutrient;
    }

    public void setNutrient(String name, int id) {
        this.nutrient = name;
        this.nutrientId = id;
    }

    /**
     * Set the id and the name of the nutrient distribution
     *
     * @param id the id of the nutrient distribution
     */
    public void setNutrientId(int id) {
        Nutrient n = Agar.getNutrient(id);

        if (n != null) {
            this.nutrientId = id;
            this.nutrient = n.toString();
        }
    }

    public void setFirstName(String name1) {
        this.name1 = name1;
    }

    public void setSecondName(String name2) {
        this.name2 = name2;
    }

    public void setFirstEnergy(float energy1) {
        this.energy1 = energy1;
    }

    public void setSecondEnergy(float energy2) {
        this.energy2 = energy2;
    }

    /**
     * Get the winner name
     *
     * @return the name of the winner colony.
     */
    public abstract String getWinnerName();

    /**
     * Get the winner energy
     *
     * @return the energy of the winner colony
     */
    public abstract float getWinnerEnergy();

    public String toString() {
        return toLog();
    }

    protected String toLog() {
        return name1 + ", " + name2 + ", " + nutrient + "(" + nutrientId + ")";
    }
}
