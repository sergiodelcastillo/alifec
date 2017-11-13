package alifec.core.contest;


import alifec.core.exception.BattleException;
import alifec.core.validation.BattleFromCsvValidator;
import alifec.core.validation.BattleRuntimeValidator;

import java.util.Objects;

/**
 * @author Sergio Del Castillo
 *         mail@: sergio.jose.delcastillo@gmail.com
 *         <p>
 *         Contains de history of battles.
 */
public class Battle implements Comparable<Battle> {
    private float firstEnergy;
    private String firstName;
    private int firstId = -1;

    private float secondEnergy;
    private String secondName;
    private int secondId = -1;

    private String nutrient;
    private int nutrientId = -1;

    public Battle(String line) throws BattleException {
        checkLineFromCSV(line);

        String[] tmp = line.split(",");

        firstName = tmp[0];
        secondName = tmp[1];
        nutrient = tmp[2];
        firstEnergy = Float.parseFloat(tmp[3]);
        secondEnergy = Float.parseFloat(tmp[4]);
    }

    public Battle(int op1, int op2, int nutri, String name1, String name2, String n)
            throws BattleException {

        this.firstId = op1;
        this.firstName = name1;

        this.secondId = op2;
        this.secondName = name2;

        this.nutrientId = nutri;
        this.nutrient = n;

        checkRuntime();
    }

    private void checkLineFromCSV(String line) throws BattleException {
        BattleFromCsvValidator validator = new BattleFromCsvValidator();

        try {
            validator.validate(line);
        } catch (Throwable ex) {
            throw new BattleException("Invalid battle: (" + this.toCsv() + ")", ex);
        }
    }

    private void checkRuntime() throws BattleException {
        BattleRuntimeValidator runtimeValidator = new BattleRuntimeValidator();

        try {
            runtimeValidator.validate(this);
        } catch (Throwable t) {
            throw new BattleException("Invalid battle: (" + this.toCsv() + ")", t);
        }
    }

    /**
     * @return name of first colony
     */
    public String getFirstColony() {
        return firstName;
    }

    /**
     * @return name of second colony
     */
    public String getSecondColony() {
        return secondName;
    }

    /**
     * @return name of nutrient
     */
    public String getNutrient() {
        return nutrient;
    }

    public Float getWinnerEnergy() {
        return (firstEnergy > 0) ? new Float(firstEnergy) : new Float(secondEnergy);
    }

    public int getWinnerId() {
        return (firstEnergy > 0) ? firstId : secondId;
    }

    public String getWinnerName() {
        return (firstEnergy > 0) ? firstName : secondName;
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof Battle) {
            Battle b = (Battle) o;

            return ((firstName.equals(b.firstName) && secondName.equals(b.secondName)) ||
                    (firstName.equals(b.secondName) && secondName.equals(b.firstName))) &&
                    nutrient.equals(b.nutrient);
        }
        return false;
    }

    public boolean contain(String name) {
        return firstName.equals(name) || secondName.equals(name);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(100);
        builder.append(firstName)
                .append(" vs ")
                .append(secondName)
                .append(" in ")
                .append(nutrient);

        return builder.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, secondName, nutrient);
    }

    @Override
    public int compareTo(Battle o) {
        int result = firstName.compareTo(o.firstName);

        if (result != 0) return result;

        result = secondName.compareTo(o.secondName);

        if (result != 0) return result;

        return nutrient.compareTo(o.nutrient);
    }

    public int getNutrientId() {
        return nutrientId;
    }

    public int getFirstColonyId() {
        return firstId;
    }

    public int getSecondColonyId() {
        return secondId;
    }

    public String toCsv() {
        StringBuilder builder = new StringBuilder(100);
        builder.append(firstName)
                .append(',')
                .append(secondName)
                .append(',')
                .append(nutrient)
                .append(',')
                .append(firstEnergy)
                .append(',')
                .append(secondEnergy);

        return builder.toString();
    }

    public void setWinner(int id, float energy) {
        if (firstId == id)
            this.firstEnergy = energy;

        if (secondId == id)
            this.secondEnergy = energy;

    }
}
