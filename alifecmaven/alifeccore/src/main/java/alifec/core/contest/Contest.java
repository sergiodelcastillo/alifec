
package alifec.core.contest;

import alifec.core.contest.oponentInfo.OpponentInfo;
import alifec.core.contest.oponentInfo.Opponent;
import alifec.core.contest.oponentInfo.OpponentStatistics;
import alifec.core.exception.*;
import alifec.core.persistence.ContestFileManager;
import alifec.core.persistence.TournamentFileManager;
import alifec.core.persistence.ZipHelper;
import alifec.core.persistence.config.ContestConfig;
import alifec.core.simulation.Agar;
import alifec.core.simulation.Environment;
import alifec.core.simulation.NutrientDistribution;
import alifec.core.simulation.nutrient.Nutrient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.*;


public class Contest {

    private Logger logger = LogManager.getLogger(getClass());

    private Environment environment;

    /**
     * List of tournaments.
     */
    private List<Tournament> tournaments;

    /**
     * current tournament.
     */
    private int selected = -1;


    /**
     * info of all opponents
     */
    private Opponent opponentsInfo;

    private ContestConfig config;

    private ContestFileManager persistence;

    public Contest(ContestConfig config) throws CreateContestException {
        try {
            this.config = config;
            this.persistence = new ContestFileManager(config.getContestPath());

            this.environment = new Environment(config);
            this.opponentsInfo = new Opponent(config);

            this.tournaments = new ArrayList<>();

            for (String name : persistence.listTournaments(config.getContestPath())) {
                tournaments.add(new Tournament(config, name));
            }

            //creating a  new Tournament!.
            Collections.sort(tournaments);

            selected = tournaments.size() - 1;

            //create new and empty tournament
            newTournament();

            opponentsInfo.addMissing(environment.getCompetitors());

            //TODO: evaluate the messages
        } catch (TournamentException e) {
            throw new CreateContestException("Error creating the contest: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new CreateContestException("Error creating the contest: Can load opponents information, please check the log for further details.", e);
        } catch (OpponentException e) {
            throw new CreateContestException("Can not load opponents information.", e);
        }
    }

    public void newTournament() throws TournamentException {
        String newT = getNextName();

        try {
            Tournament t = new Tournament(config, newT);
            t.setEnabled(true);

            if (selected >= 0)
                tournaments.get(selected).setEnabled(false);

            tournaments.add(t);
            selected = tournaments.indexOf(t);
        } catch (TournamentException ex) {
            logger.error(ex.getMessage(), ex);
            throw new TournamentException("Cannot load the tournament: " + newT);
        }
    }

    /**
     * Remove the selected tournament
     *
     * @return true if the current tournament can be removed!
     */
    public boolean removeSelected() {
        try {
            if (config.isCompetitionMode()) {
                persistence.delete(config.getBattlesFile(getSelected().getName()));
                tournaments.remove(selected);

                if (selected >= tournaments.size())
                    selected = tournaments.size() - 1;
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
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

    public List<NutrientDistribution> getCurrentNutrients() {
        List<NutrientDistribution> list = new ArrayList<>();

        List<Integer> current = config.getNutrients();
        Hashtable<Integer, Nutrient> allNutrients = Agar.getAllNutrient();

        for (int nutrientId : current) {
            list.add(new NutrientDistribution(nutrientId, allNutrients.get(nutrientId).getName()));

        }

        return list;
    }

    public List<NutrientDistribution> getAllNutrients() {
        List<NutrientDistribution> list = new ArrayList<>();

        Hashtable<Integer, Nutrient> allNutrients = Agar.getAllNutrient();

        for (Integer nutrientId : allNutrients.keySet()) {
            list.add(new NutrientDistribution(nutrientId, allNutrients.get(nutrientId).getName()));
        }

        return list;
    }


    /**
     * Reload the configuration. If the config is not valid then it is discarded.
     *
     * @return
     * @throws IOException
     */
    public boolean reloadConfig() throws IOException {
        try {
            config = ContestConfig.buildFromFile(config.getPath());
            return true;
        } catch (ConfigFileException ex) {
            logger.error(ex.getMessage(), ex);
            return false;
        }
    }


    /**
     * update the config file into the proyect.
     *
     * @param path  url of contest
     * @param name  name of contest
     * @param mode  mode of contest(programmer or competition)
     * @param pause default pause between battles
     * @return true if is successfully
     */
    public boolean updateConfigFile(String path, String name, int mode, int pause) {
        config.setPath(path);
        config.setContestName(name);
        config.setMode(mode);
        config.setPauseBetweenBattles(pause);

        try {
            config.save();
        } catch (ConfigFileException e) {
            logger.error("Error updating config file: " + e.getConfig(), e);
            return false;
        }
        return true;
    }

    public void setMode(int mode) throws IOException {
        this.config.setMode(mode);
        if (this.config.isProgrammerMode() &&
                mode == ContestConfig.COMPETITION_MODE) {
            lastTournament().save();
        }

    }

    /**
     * @return the selected id of tournaments.
     */
    public int getSelectedID() {
        return selected;
    }

    public synchronized Tournament lastTournament() {
        if (tournaments.size() == 0)
            return null;

        return tournaments.get(tournaments.size() - 1);
    }


    public Environment getEnvironment() {
        return environment;
    }


    public int getMode() {
        return config.getMode();
    }

    public String getName() {
        return config.getContestName();
    }

    public int getTimeWait() {
        return config.getPauseBetweenBattles();
    }

    /**
     * @return information
     * @throws CreateRankingException if can not create the ranking
     */
    public List<OpponentStatistics> getInfo() throws CreateRankingException {
        Hashtable<String, Integer> ranking = getRanking();
        List<OpponentStatistics> info = new ArrayList<>();

        Tournament t = lastTournament();
        Hashtable<String, Float> accumulated = t.getAccumulatedEnergy();

        for (OpponentInfo oi : opponentsInfo.getOpponents()) {
            boolean hasRanking = ranking.containsKey(oi.getName());
            boolean hasAccumulated = accumulated.containsKey(oi.getName());

            info.add(new OpponentStatistics(oi.getName(),
                    oi.getAuthor(),
                    oi.getAffiliation(),
                    hasRanking ? ranking.get(oi.getName()) : 0,
                    hasAccumulated ? accumulated.get(oi.getName()) : 0.0f));
        }

        // sort by accumulated points
        Collections.sort(info);

        //The biggest first
        Collections.reverse(info);

        return info;
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
     * It generates a zip file of the source code of the participants.
     * The destination file is "Contest-name" / Backup / backup-MMaahhmmss.zip
     * where MM: Month, yy: year, hh: hour, mm: minutes and ss: seconds today.
     *
     * @return true if is successfully
     */
    public boolean createBackUp() {

        //todo: it should be used in persistence
        // generate the back up...
        try {
            ZipHelper.zipContest(config);
            return true;
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
            return false;
        }
    }

    public boolean needRestore() {
        return tournaments.size() != 0 && lastTournament().existsTargetRunFile();
    }

    public String getPath() {
        return config.getPath();
    }

    public ContestConfig getConfig() {
        return config;
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

    public void updateNutrient(ContestConfig config, List<Integer> nutrients) throws ConfigFileException {
        config.setNutrients(nutrients);

        config.save();
    }

    public List<Battle> getMissingRunBattles() throws IOException {
        List<Battle> list = lastTournament().getMissingRunBattles();
        List<Battle> toDelete = new ArrayList<>();
        List<String> opponentNames = environment.getOpponentNames();

        // remove battles which have unavailable colonies
        for (Battle battle : list) {
            if (!opponentNames.contains(battle.getFirstColony()) ||
                    !opponentNames.contains(battle.getSecondColony())) {
                toDelete.add(battle);
            }
        }

        list.removeAll(toDelete);

        return list;
    }
}

