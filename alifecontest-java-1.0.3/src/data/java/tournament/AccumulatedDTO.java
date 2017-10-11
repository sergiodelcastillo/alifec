package data.java.tournament;

/**
 * Created by Sergio Del Castillo
 * User: yeyo
 * Date: Jul 15, 2010
 * Time: 7:18:10 PM
 * Email: yeyo@druidalabs.com
 */
public class AccumulatedDTO implements Comparable<AccumulatedDTO> {

    /**
     * The name of the colony
     */
    private String name;

    /**
     * .
     * the accumulated energy
     */
    private float energy;

    public AccumulatedDTO(String name, float energy) {
        this.name = name;
        this.energy = energy;
    }

    @Override
    public int compareTo(AccumulatedDTO o) {
        return Float.compare(energy, o.energy);

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AccumulatedDTO that = (AccumulatedDTO) o;

        return name.equals(that.name);

    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public String getName() {
        return name;
    }

    public float getEnergy() {
        return energy;
    }


    public void add(float ene) {
        this.energy += ene;
    }

 
}
