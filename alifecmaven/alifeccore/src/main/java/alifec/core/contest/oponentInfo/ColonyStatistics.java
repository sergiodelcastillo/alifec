package alifec.core.contest.oponentInfo;

import java.util.Comparator;
import java.util.Objects;

/**
 * Created by Sergio Del Castillo on 19/08/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class ColonyStatistics implements Comparable<ColonyStatistics>, Comparator<ColonyStatistics> {
    private String name;
    private String author;
    private String affiliation;
    private int points;
    private float accumulated;

    public ColonyStatistics(String name, String author, String affiliation, int points, float accumulated) {
        this.name = name;
        this.author = author;
        this.affiliation = affiliation;
        this.points = points;
        this.accumulated = accumulated;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAffiliation() {
        return affiliation;
    }

    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int point) {
        this.points = point;
    }

    public float getAccumulated() {
        return accumulated;
    }

    @Override
    public int compareTo(ColonyStatistics o) {
        int comp = Integer.compare(points, o.points);

        if (comp != 0) return comp;

        return Float.compare(accumulated, o.accumulated);
    }

    public void addEnergy(float energy) {
        accumulated += energy;
    }

    public void addPoints(int point) {
        this.points += point;
    }

    @Override
    public int compare(ColonyStatistics o1, ColonyStatistics o2) {
        return o1.compareTo(o2);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (Objects.isNull(o) || getClass() != o.getClass()) return false;
        ColonyStatistics that = (ColonyStatistics) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
