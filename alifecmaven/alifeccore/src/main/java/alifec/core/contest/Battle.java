package alifec.core.contest;


import alifec.core.exception.BattleException;
import alifec.core.simulation.Competitor;
import alifec.core.simulation.NutrientDistribution;
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
    private static final String BATTLE_STRING_FORMAT = "%s vs %s in %s";
    private static final String BATTLE_CSV_FORMAT = "%s,%s,%s,%f,%f";

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

    public Battle(Competitor c1, Competitor c2, NutrientDistribution n1) throws BattleException {
        this(c1.getId(), c2.getId(), n1.getId(), c1.getColonyName(), c2.getColonyName(), n1.getNutrientName());
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
        return String.format(BATTLE_STRING_FORMAT, firstName, secondName, nutrient);
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
        return String.format(BATTLE_CSV_FORMAT, firstName, secondName, nutrient, firstEnergy, secondEnergy);
    }

    public void setWinner(int id, float energy) {
        if (firstId == id)
            this.firstEnergy = energy;

        if (secondId == id)
            this.secondEnergy = energy;
    }
}
