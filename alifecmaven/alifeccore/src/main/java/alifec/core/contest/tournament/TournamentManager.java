package alifec.core.contest.tournament;

import alifec.core.contest.ContestConfig;
import alifec.core.exception.CreateRankingException;
import alifec.core.exception.CreateTournamentException;
import alifec.core.persistence.filter.TournamentFilter;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by Sergio Del Castillo on 05/08/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class TournamentManager {

    Logger logger = Logger.getLogger(getClass());

    /**
     * List of tournaments.
     */
    private List<Tournament> tournaments = new ArrayList<>();

    /**
     * current tournament.
     */
    private int selected = -1;

    /**
     * Configuration loaded from file "config".
     */
    private ContestConfig config;

    public TournamentManager(ContestConfig config) throws IOException, CreateTournamentException {
        this.config = config;

        String[] tournamentName = new File(config.getContestPath()).list(new TournamentFilter());

        if (tournamentName != null) {

            for (String name : tournamentName) {
                Tournament t = new Tournament(config, name);

                if (t.read())
                    tournaments.add(t);
            }
        }
        //creating a  new Tournament!.
        Collections.sort(tournaments);

        selected = tournaments.size() - 1;
    }

    public Hashtable<String, Integer> getRanking() throws CreateRankingException {
        Hashtable<String, Integer> ranking = new Hashtable<>();

        for (Tournament t : tournaments) {
            Hashtable<String, Integer> tRanking = t.getRanking();

            for (String name : tRanking.keySet()) {
                Integer point = ranking.containsKey(name) ? ranking.remove(name) : new Integer(0);
                ranking.put(name, point + tRanking.get(name));
            }
        }

        return ranking;
    }


    /**
     * This method suppose  that the maximum of tournament of the
     * current contest in not longer than 100.
     *
     * @return String a: the name of next tournament.
     */
    private String getNextName() {
        Integer tournamentNumber = 1;

        if (tournaments.size() > 0) {
            String name = tournaments.get(tournaments.size() - 1).getName();
            name = name.replace(ContestConfig.TOURNAMENT_PREFIX, "");

            tournamentNumber = Integer.valueOf(name) + 1;
        }

        return ContestConfig.getTournamentFilename(tournamentNumber);
    }

    public void newTournament(List<String> colonies) throws CreateTournamentException {
        String newT = getNextName();

        if (config.isCompetitionMode()) {
            if (!new File(config.getTournamentPath(newT)).mkdir())
                throw new CreateTournamentException("Can not create a new folder...");
        }

        try {
            Tournament t = new Tournament(config, newT);
            t.setEnabled(true);

            for (String c : colonies) {
                t.addColony(c);
            }

            if (selected >= 0)
                tournaments.get(selected).setEnabled(false);

            tournaments.add(t);
            selected = tournaments.indexOf(t);
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
            throw new CreateTournamentException("Cannot load the tournament...");
        }
    }

    /**
     * Remove the selected tournament
     *
     * @return true if the current tournament can be removed!
     */
    public boolean removeSelected() {
        Tournament t = getSelected();

        File file = new File(t.getBattleManager().getBattlesFileName());
        if (file.exists() && !file.delete())
            if (config.isCompetitionMode()) return false;

        file = new File(config.getTournamentPath(t.getName()));
        if (file.exists() && !file.delete())
            if (config.isCompetitionMode()) return false;


        tournaments.remove(selected);

        if (selected >= tournaments.size())
            selected = tournaments.size() - 1;

        return true;
    }

    /**
     * @return the selected tournament.
     */
    public Tournament getSelected() {
        try {
            return tournaments.get(selected);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return null;
    }

    /**
     * @return the selected id of tournaments.
     */
    public int getSelectedID() {
        return selected;
    }

    public synchronized Tournament lastElement() {
        if (tournaments.size() == 0)
            return null;

        return tournaments.get(tournaments.size() - 1);
    }

    /**
     * @param i: index of tournaments
     * @return tournament i.
     */
    public Tournament getTournament(int i) {
        return tournaments.get(i);
    }

    /**
     * @return count of tournaments.
     */
    public int size() {
        return tournaments.size();
    }

    /**
     * Return the next tournament
     * if the selected tournament is the last this function return the last tournament
     *
     * @return next the next Tournament
     */
    public Tournament next() {
        if (selected < tournaments.size() - 1)
            selected++;

        return tournaments.get(selected);
    }

    public Tournament prev() {
        if (selected > 0)
            selected--;

        return tournaments.get(selected);
    }

    public void setMode(int mode) throws IOException {
        if (this.config.isProgrammerMode() &&
                mode == ContestConfig.COMPETITION_MODE) {
            lastElement().getBattleManager().setMode(mode);
            lastElement().save();
        }
        this.config.setMode(mode);
    }
}
