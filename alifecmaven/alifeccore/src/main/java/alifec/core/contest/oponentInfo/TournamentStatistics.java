package alifec.core.contest.oponentInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Sergio Del Castillo on 28/10/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class TournamentStatistics {
    private final List<ColonyStatistics> list;

    public TournamentStatistics() {
        list = new ArrayList<>();
    }

    public List<ColonyStatistics> getList() {
        return list;
    }

    public boolean contains(String colony) {
        for (ColonyStatistics stats : list) {
            if (stats.getName().equals(colony)) return true;
        }
        return false;
    }

    public void addWinner(String name, String author, String affiliation, float energy) {
        boolean found = false;
        for (ColonyStatistics statistics : list) {
            if (statistics.getName().equals(name)) {
                statistics.addEnergy(energy);
                found = true;
            }
        }

        if (!found) {
            list.add(new ColonyStatistics(name, author, affiliation, 0, energy));
        }
    }

    public void calculate() {
        //todo: test!!
        Collections.sort(list);
        Collections.reverse(list);

        int points = 3;

        if (list.isEmpty() || list.get(0).getAccumulated() <= 0.0f) return;

        for (ColonyStatistics statistics : list) {
            if (statistics.getAccumulated() > 0.0f)
                statistics.setPoints(points);

            --points;

            if (points == 0) return;
        }
    }

    public float getMaxEnergy() {
        if (list.isEmpty()) return 0.0f;

        return Collections.max(list).getAccumulated();
    }

    public List<ColonyStatistics> getColonyStatistics() {
        return list;
    }

    public void addOpponentInfo(String name, String author, String affiliation) {
        for (ColonyStatistics c : list) {
            if (c.getName().equals(name)) {
                c.setAuthor(author);
                c.setAffiliation(affiliation);
            }
        }
    }
}
