/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */
package alifec.core.contest.tournament;

import alifec.core.contest.Contest;
import alifec.core.contest.ContestConfig;
import alifec.core.exception.CreateRankingException;
import alifec.core.exception.CreateTournamentException;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Vector;


public class TournamentManager {
    /**
     * Vector de tournaments.
     */
    private Vector<Tournament> tournaments = new Vector<Tournament>();
    /**
     * absolute path of tournaments.
     */
    private String PATH = "";

    /**
     * current tournament.
     */
    private int selected = -1;

    /**
     * Contest mode.
     */
    private int mode;

    public TournamentManager(String p, int m) throws IOException, CreateTournamentException {
        PATH = p;
        mode = m;

        String[] tournamentName = new File(PATH).list(new TournamentFilter());

        if (tournamentName != null) {

            for (String name : tournamentName) {
                Tournament t = new Tournament(name, PATH, mode);
                if (t.read())
                    tournaments.addElement(t);
            }
        }
        //creating a  new Tournament!.
        Collections.sort(tournaments);

        selected = tournaments.size() - 1;
//		selected = -1;

    }

    public Hashtable<String, Integer> getRanking() throws CreateRankingException {
        Hashtable<String, Integer> ranking = new Hashtable<String, Integer>();

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
        if (tournaments.size() > 0) {
            String NAME = tournaments.lastElement().NAME;

            Integer i = (new Integer(NAME.split("-")[1]) + 1);

            return "Tournament-" + ((i < 10) ? ("0" + i) : i);
        } else
            return "Tournament-01";
    }

    public void newTournament(Vector<String> colonies) throws CreateTournamentException {
        String newT = getNextName();

        if (ContestConfig.COMPETITION_MODE == mode) {
            if (!new File(PATH + File.separator + newT).mkdir())
                throw new CreateTournamentException("Can not create a new folder...");
        }

        try {
            Tournament t = new Tournament(newT, PATH, mode);
            t.setEnabled(true);

            for (String c : colonies) {
                t.addColony(c);
            }

            if (selected >= 0)
                tournaments.elementAt(selected).setEnabled(false);

            tournaments.addElement(t);
            selected = tournaments.indexOf(t);
        } catch (IOException ex) {
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
        String url = PATH + File.separator + t.NAME;

        //if the tournament is null
        if (t == null) {
            return false;
        }

        File file = new File(t.getBattleManager().getBattlesFileName());
        if (file.exists() && !file.delete())
            if (mode == ContestConfig.COMPETITION_MODE) return false;

        file = new File(url);
        if (file.exists() && !file.delete())
            if (mode == ContestConfig.COMPETITION_MODE) return false;


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
            return tournaments.elementAt(selected);
        } catch (Exception ex) {
            System.out.println("error:tournamentManager.getSelected()");
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

        return tournaments.lastElement();
    }

    /**
     * @param i: index of tournaments
     * @return tournament i.
     */
    public Tournament getTournament(int i) {
        return tournaments.elementAt(i);
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

        return tournaments.elementAt(selected);
    }

    public Tournament prev() {
        if (selected > 0)
            selected--;

        return tournaments.elementAt(selected);
    }

    public void setMode(int mode) {
        if (this.mode == ContestConfig.PROGRAMMER_MODE &&
                mode == ContestConfig.COMPETITION_MODE) {
            lastElement().getBattleManager().setMode(mode);
            lastElement().save(PATH);
        }
        this.mode = mode;
    }
}
