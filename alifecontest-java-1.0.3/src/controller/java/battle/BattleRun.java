/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

package controller.java.battle;

import controller.java.Agar;
import controller.java.nutrients.Nutrient;
import data.java.Log;

public class BattleRun extends AbstractBattle {
    private static int count;

    private int id1 = -1;
    private int id2 = -1;

    private int winnerId = -1;

    private int id = ++count;

    public BattleRun() {
        super();
    }

    public BattleRun(String line) throws IllegalArgumentException {
        if (line == null || line.equals(""))
            throw new IllegalArgumentException("Bad line");

        String[] tmp = line.split(",");

        if (tmp.length < 3)
            throw new IllegalArgumentException("Bad line");

        try {
            name1 = tmp[0].trim();
            name2 = tmp[1].trim();
            setNutrientId(Integer.parseInt(tmp[2].trim()));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Bad line");
        }

        if (!this.isValid())
            throw new IllegalArgumentException("Bad line");

        Nutrient nut = Agar.getNutrient(nutrientId);

        if (nut == null) {
            throw new IllegalArgumentException("Invalid nutrient id=" + nutrientId);
        }
        nutrient = nut.toString();
    }

    public BattleRun(String op1, String op2, int i, float e1, float e2) {
        super(op1, op2, i, e1,e2);
    }


    public int getFistId() {
        return this.id1;
    }

    public int getSecondId() {
        return this.id2;
    }

    @Override
    public String getWinnerName() {
        return winnerId == id1 ? name1 : (winnerId == id2 ? name2 : null);

    }

    public boolean valid() {
        return super.isValid() && id1 >= 0 && id2 >= 0 && id1 != id2;
    }

    @Override
    public float getWinnerEnergy() {
        return winnerId == id1 ? energy1 : (winnerId == id2 ? energy2 : 0.0f);
    }

    public String toLog() {
        return name1 + "(" + id1 + "), " + name2 + "(" + id2 + "), " + nutrient + "(" + nutrientId + ")";
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

        return buffer.toString();
    }

    public void setNutrientId(int nutrientID) {
        this.nutrientId = nutrientID;
    }

    public void setFirstId(int ID1) {
        this.id1 = ID1;
    }

    public void setSecondId(int ID2) {
        this.id2 = ID2;
    }

    public int getNutrientId() {
        return nutrientId;
    }

    public int getWinnerId() {
        return winnerId;
    }

    public void setFirstOpponent(String name, int id) {
        this.name1 = name;
        this.id1 = id;
        this.energy1 = 0f;
    }

    public void setSecondOpponent(String name, int id) {
        this.name2 = name;
        this.id2 = id;
        this.energy2 = 0f;
    }

    public void setWinner(int id, float energy) throws IllegalArgumentException {
        if (id == this.id1) {
            this.energy1 = energy;
            this.winnerId = id;
        } else if (id == this.id2) {
            this.energy2 = energy;
            this.winnerId = id;
        } else {
            Log.printlnAndSave("invalid id: " + id);
            throw new IllegalArgumentException("Invalid id");
        }
    }

    /**
     * the unique identifier
     *
     * @return the identifier
     */
    public int getId() {
        return id;
    }

}
