package alifec.core.contest;

import alifec.core.contest.oponentInfo.Opponent;
import alifec.core.contest.oponentInfo.OpponentInfo;
import alifec.core.contest.oponentInfo.Ranking;
import alifec.core.contest.oponentInfo.TournamentStatistics;
import alifec.core.event.Event;
import alifec.core.event.EventBus;
import alifec.core.event.Listener;
import alifec.core.exception.*;
import alifec.core.persistence.ContestFileManager;
import alifec.core.persistence.ZipFileManager;
import alifec.core.persistence.config.ContestConfig;
import alifec.core.simulation.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Contest implements Listener {

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
    private ZipFileManager zipPersistence;

    public Contest(ContestConfig config) throws CreateContestException {
        try {
            this.config = config;
            this.persistence = new ContestFileManager(config.getContestPath());
            this.zipPersistence = new ZipFileManager(config);

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

            //register to listen battle events
            EventBus.register(this);

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
            Tournament t = new Tournament(config, newT, environment.getOpponentNames());

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

        return config.getTournamentFilename(tournamentNumber);
    }

    /**
     * Reload the configuration. If the config is not valid then it is discarded.
     *
     * @return
     * @throws IOException
     */
    public boolean reloadConfig() throws IOException {
        try {
            config = new ContestConfig(config.getBundle());
            config.validate();
            return true;
        } catch (ConfigFileException | ValidationException ex) {
            logger.error(ex.getMessage(), ex);
            return false;
        }
    }


    /**
     * update the config file into the proyect.
     *
     * @param name  name of contest
     * @param mode  mode of contest(programmer or competition)
     * @param pause default pause between battles
     * @return true if is successfully
     * @deprecated Use the method updateConfigFile(ContestConfig)
     */
    @Deprecated
    public boolean updateConfigFile(String name, int mode, int pause, List<Integer> nutrients) {
        config.setContestName(name);
        config.setMode(mode);
        config.setPauseBetweenBattles(pause);
        config.setNutrients(nutrients);

        try {
            config.save();
        } catch (ConfigFileWriteException e) {
            logger.error("Error updating config file: " + e.getConfig(), e);
            return false;
        }
        return true;
    }

    public void updateConfigFile(ContestConfig c) throws ConfigFileWriteException {
        c.save();

        config.setContestName(c.getContestName());
        config.setMode(c.getMode());
        config.setPauseBetweenBattles(c.getPauseBetweenBattles());
        config.setNutrients(c.getNutrients());

        logger.info("Contest configuration updated.");
        logger.info(config.toString());
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

    public void setMode(int mode) throws IOException {
        this.config.setMode(mode);
        if (this.config.isProgrammerMode() &&
                mode == ContestConfig.COMPETITION_MODE) {
            lastTournament().save();
        }

    }

    public String getName() {
        return config.getContestName();
    }

    public int getTimeWait() {
        return config.getPauseBetweenBattles();
    }


    public Ranking getRanking() throws CreateRankingException {
        Ranking ranking = new Ranking();

        for (Tournament t : tournaments) {
            TournamentStatistics ts = t.getTournamentStatistics();

            for (OpponentInfo op : opponentsInfo.getOpponents()) {
                ts.addOpponentInfo(op.getName(), op.getAuthor(), op.getAffiliation());
            }

            ranking.addTournamentStats(ts);
        }

        ranking.calculate();

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

        //todo: improve the use of exceptions
        // generate the back up...
        try {
            zipPersistence.zipContest();
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

    public List<Battle> getMissingRunBattles() throws TournamentException {
        List<Battle> list = lastTournament().getMissingRunBattles(true);
        List<String> opponentNames = environment.getOpponentNames();

        // remove battles which have unavailable colonies
        List<Battle> toDelete = new ArrayList<>();
        for (Battle battle : list) {
            if (!opponentNames.contains(battle.getFirstColony()) ||
                    !opponentNames.contains(battle.getSecondColony())) {
                toDelete.add(battle);
            }
        }

        list.removeAll(toDelete);


        return list;
    }

    public Battle getUnsuccessfulBattle() throws TournamentException {
        return lastTournament().getUnsuccessfulBattle();
    }

    @Override
    public void handle(Event event) {
        lastTournament().handle(event);
    }
}

