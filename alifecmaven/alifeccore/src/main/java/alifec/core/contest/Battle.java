package alifec.core.contest;


/**
 * @author Sergio Del Castillo
 *         mail@: sergio.jose.delcastillo@gmail.com
 *         <p>
 *         Contains de history of battles.
 */
public class Battle implements Comparable<Battle> {
    private float energy_1;
    private float energy_2;
    private String name_1;
    private String name_2;
    private String nutrient;

    public Battle(String line) {
        if (line == null || line.isEmpty())
            throw new IllegalArgumentException("The line (" + line + ") is empty");

        String[] tmp = line.split(",");

        if (tmp.length != 5)
            throw new IllegalArgumentException("The line (" + line + ") should have 5 columns");

        name_1 = tmp[0];
        name_2 = tmp[1];
        nutrient = tmp[2];
        energy_1 = Float.parseFloat(tmp[3]);
        energy_2 = Float.parseFloat(tmp[4]);
    }

    public Battle(BattleResult b) {
        this.name_1 = b.name1;
        this.name_2 = b.name2;
        this.nutrient = b.nutrient;
        this.energy_1 = b.energy1();
        this.energy_2 = b.energy2();
    }


    /**
     * @return name of first colony
     */
    public String getFirstColony() {
        return name_1;
    }

    /**
     * @return name of second colony
     */
    public String getSecondColony() {
        return name_2;
    }

    /**
     * @return name of nutrient
     */
    public String getNutrient() {
        return nutrient;
    }

    public Float getWinnerEnergy() {
        return (energy_1 > 0) ? new Float(energy_1) : new Float(energy_2);
    }

    public String getWinnerName() {
        return (energy_1 > 0) ? name_1 : name_2;
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof Battle) {
            Battle b = (Battle) o;

            return ((name_1.equals(b.name_1) && name_2.equals(b.name_2)) ||
                    (name_1.equals(b.name_2) && name_2.equals(b.name_1))) &&
                    nutrient.equals(b.nutrient);
        }
        return false;
    }

    public boolean contain(String name) {
        return name_1.equals(name) || name_2.equals(name);
    }

    @Override
    public String toString() {
        return name_1 + "," + name_2 + "," + nutrient + "," +
                energy_1 + "," + energy_2 + ",";
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Float.floatToIntBits(this.energy_1);
        hash = 23 * hash + Float.floatToIntBits(this.energy_2);
        hash = 23 * hash + (this.name_1 != null ? this.name_1.hashCode() : 0);
        hash = 23 * hash + (this.name_2 != null ? this.name_2.hashCode() : 0);
        hash = 23 * hash + (this.nutrient != null ? this.nutrient.hashCode() : 0);
        return hash;
    }

    @Override
    public int compareTo(Battle o) {
        int result = name_1.compareTo(o.name_1);

        if (result != 0) return result;

        result = name_2.compareTo(o.name_2);

        if (result != 0) return result;

        return nutrient.compareTo(o.nutrient);
    }
}
