package controller.java.tournament;

/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

import controller.java.contest.ContestMode;
import data.java.AllFilter;
import data.java.Config;
import data.java.Log;
import data.java.contest.RankingDTO;
import exceptions.CreateRankingException;
import exceptions.CreateTournamentException;
import exceptions.LoadTournamentException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;


public class TournamentManager {
    /**
     * Vector de tournaments.
     */
    private ArrayList<Tournament> tournaments;

    /**
     * selected tournament.
     */
    private int selected = -1;

    /**
     * Contest mode.
     */
    private int mode;

    public TournamentManager(int m, boolean load) throws LoadTournamentException {
        mode = m;
        tournaments = new ArrayList<Tournament>();

        if (load) load();
    }

    private void load() throws LoadTournamentException {
        try {
            String[] tNames = AllFilter.listTournaments();

            for (String name : tNames) {
                Tournament t = new Tournament(name, mode);

                try {
                    t.load();
                    tournaments.add(t);
                } catch (IOException ex) {
                    Log.printlnAndSave("Cant load " + Config.getInstance().getAbsoluteBattleFile(t.getName()), ex);
                }
            }

            Collections.sort(tournaments);

            selected = tournaments.size() - 1;
        } catch (IOException ex) {
            throw new LoadTournamentException("Cant load the tournaments", ex);
        }
    }

    public ArrayList<RankingDTO> getRanking() throws CreateRankingException {
        ArrayList<RankingDTO> result = new ArrayList<RankingDTO>();

        for (Tournament t : tournaments) {
            for (RankingDTO r : t.getRanking()) {
                addToRanking(result, r);
            }
        }
        Collections.sort(result);
        Collections.reverse(result);
        return result;
    }

    private void addToRanking(ArrayList<RankingDTO> result, RankingDTO r) {
        for (RankingDTO ranking : result) {
            if (r.equals(ranking)) {
                ranking.add(r.getPoints());
                return;
            }
        }
        result.add(r);
    }


    /**
     * This method suppose  that the maximum of tournament of the
     * current contest in not longer than 100.
     *
     * @return String a: the name of next tournament.
     */
    private String getNextName() {
        if (tournaments.size() > 0) {
            String NAME = tournaments.get(tournaments.size() - 1).getName();

            Integer i = Integer.parseInt(NAME.split("-")[1]) + 1;

            return "Tournament-" + ((i < 10) ? ("0" + i) : i);
        } else
            return "Tournament-01";
    }

    /**
     * Create a new tournament and add the colonies to this.
     *
     * @param colonies a list of colonies to add
     * @throws CreateTournamentException if the mode is COMPETITION_MODE and can not create the new tournament folder.
     */
    public void newTournament(ArrayList<String> colonies) throws CreateTournamentException {
        try {
            Tournament t = new Tournament(getNextName(), mode, colonies);
            t.setEnabled(true);

            if (selected >= 0)
                tournaments.get(selected).setEnabled(false);

            tournaments.add(t);
            selected = tournaments.indexOf(t);
        } catch (IOException ex) {
            throw new CreateTournamentException("Creating the tournament [FAIL]", ex);
        }
    }

    /**
     * Remove the selected tournament
     *
     * @return true if the current tournament can be removed!
     */
    public boolean removeSelected() {
        Tournament t = getSelected();
        if (t == null) {
            return false;
        }

        if (ContestMode.COMPETITION_MODE == mode && !t.deleteFolder()) {
            return false;
        }

        if (!tournaments.remove(t)) {
            return false;
        }

        if (selected >= tournaments.size())
            --selected; // = tournaments.size() - 1;

        return true;
    }

    /**
     * @return the selected tournament.
     */
    public Tournament getSelected() {
        try {
            return tournaments.get(selected);
        } catch (IndexOutOfBoundsException ex) {
            Log.println("Tournament#getSelected exception. For more information see the log.");
            Log.save("Tournament.getSelected ", ex);
        }
        return null;
    }

    /**
     * @return the selected id of tournaments.
     */
    public int getSelectedIndex() {
        return selected;
    }

    public Tournament getLastTournament() {
        if (tournaments.size() == 0) {
            return null;
        }

        return tournaments.get(tournaments.size() - 1);
    }

    /**
     * @param i index of tournament
     * @return the tournament i
     * @throws IndexOutOfBoundsException
     */
    public Tournament get(int i) throws IndexOutOfBoundsException {
        return tournaments.get(i);
    }

    /**
     * @return count of tournaments.
     */
    public int getSize() {
        return tournaments.size();
    }

    /**
     * Return the next tournament
     * if the selected tournament is the last this function return the last tournament
     *
     * @return next the next Tournament
     */
    public Tournament next() {
        if (selected < tournaments.size() - 1) {
            selected++;
        }

        return tournaments.get(selected);
    }

    public Tournament prev() {
        if (selected > 0)
            selected--;

        return tournaments.get(selected);
    }

    public void setMode(int aMode) throws IOException {
        this.mode = aMode;

        for (Tournament tournament : tournaments) {
            tournament.setMode(aMode);
        }
    }

    public int getMode() {
        return mode;
    }

    public void update() throws IOException {
        for(Tournament tournament: tournaments){
            tournament.update();
        }
    }
}
