package alifec.core.contest;


import alifec.core.exception.CreateBattleException;
import alifec.core.validation.BattleFromCsvValidator;
import alifec.core.validation.BattleRuntimeValidator;

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

    public Battle(String line) throws CreateBattleException {
        checkLineFromCSV(line);

        String[] tmp = line.split(",");

        firstName = tmp[0];
        secondName = tmp[1];
        nutrient = tmp[2];
        firstEnergy = Float.parseFloat(tmp[3]);
        secondEnergy = Float.parseFloat(tmp[4]);
    }

    public Battle(int op1, int op2, int nutri, String name1, String name2, String n)
            throws CreateBattleException {

        this.firstId = op1;
        this.firstName = name1;

        this.secondId = op2;
        this.secondName = name2;

        this.nutrientId = nutri;
        this.nutrient = n;

        checkRuntime();
    }

    private void checkLineFromCSV(String line) throws CreateBattleException {
        BattleFromCsvValidator validator = new BattleFromCsvValidator();

        if (!validator.validate(line)) {
            throw new CreateBattleException("Invalid battle: (" + this.toCsv() + ")");
        }
    }

    private void checkRuntime() throws CreateBattleException {
        BattleRuntimeValidator runtimeValidator = new BattleRuntimeValidator();

        if (!runtimeValidator.validate(this))
            throw new CreateBattleException("Invalid battle: (" + this.toCsv() + ")");
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
        int hash = 7;
        hash = 23 * hash + Float.floatToIntBits(this.firstEnergy);
        hash = 23 * hash + Float.floatToIntBits(this.secondEnergy);
        hash = 23 * hash + (this.firstName != null ? this.firstName.hashCode() : 0);
        hash = 23 * hash + (this.secondName != null ? this.secondName.hashCode() : 0);
        hash = 23 * hash + (this.nutrient != null ? this.nutrient.hashCode() : 0);
        return hash;
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
