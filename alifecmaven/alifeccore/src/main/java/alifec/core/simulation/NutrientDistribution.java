package alifec.core.simulation;

import java.util.Objects;

/**
 * Created by Sergio Del Castillo on 28/10/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class NutrientDistribution {
    private final int index;
    private final String name;

    public NutrientDistribution(int index, String name) {
        this.index = index;
        this.name = name;
    }

    public int getId() {
        return index;
    }

    public String getNutrientName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NutrientDistribution)) return false;
        NutrientDistribution that = (NutrientDistribution) o;
        return index == that.index &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, name);
    }
}
