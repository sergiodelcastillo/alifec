package alifec.core.contest.oponentInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Sergio Del Castillo on 30/10/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class Ranking {
    private List<TournamentStatistics> list;
    private List<ColonyStatistics> calculated;

    public Ranking() {
        list = new ArrayList<>();
        calculated = new ArrayList<>();
    }

    public void addTournamentStats(TournamentStatistics ts) {
        list.add(ts);
    }


    public void calculate() {
        calculated.clear();

        for (TournamentStatistics ts : list) {
            for (ColonyStatistics cs : ts.getColonyStatistics()) {
                boolean found = false;
                for (ColonyStatistics c : calculated) {
                    if (c.equals(cs)) {
                        c.addPoints(cs.getPoints());
                        c.addEnergy(cs.getAccumulated());
                        found = true;
                        break;
                    }
                }

                if (!found) calculated.add(cs);
            }
        }

        Collections.sort(calculated);
        Collections.reverse(calculated);
    }

    public List<ColonyStatistics> getCalculated() {
        return calculated;
    }

    public List<TournamentStatistics> getDetailed() {
        return list;
    }
}
