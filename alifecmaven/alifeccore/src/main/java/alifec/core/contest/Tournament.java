
package alifec.core.contest;

import alifec.core.contest.oponentInfo.TournamentStatistics;
import alifec.core.event.Event;
import alifec.core.event.Listener;
import alifec.core.event.impl.BattleFinishEvent;
import alifec.core.event.impl.BattleMovementEvent;
import alifec.core.event.impl.BattleStartsEvent;
import alifec.core.exception.BattleException;
import alifec.core.exception.TournamentException;
import alifec.core.persistence.SimulationFileManager;
import alifec.core.persistence.SimulationFileManagerImpl1;
import alifec.core.persistence.SimulationFileManagerList;
import alifec.core.persistence.TournamentFileManager;
import alifec.core.persistence.config.ContestConfig;
import alifec.core.simulation.Competitor;
import alifec.core.simulation.NutrientDistribution;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Tournament implements Comparable<Tournament>, Listener {

    private Logger logger = LogManager.getLogger(getClass());

    private List<Battle> battles;

    private List<String> colonies;

    private final String name;

    private TournamentFileManager persistence;
    private SimulationFileManager sPersistence;

    private boolean isEnabled = false;

    private ContestConfig config;

    public Tournament(ContestConfig config, String name) throws TournamentException {
        this.config = config;
        this.name = name;

        this.battles = new ArrayList<>();
        this.colonies = new ArrayList<>();

        try {
            this.persistence = new TournamentFileManager(config.getBattlesFile(name), config.isCompetitionMode());

            if (config.isCompetitionMode()) {
                //load battles
                loadAllBattles();
            }

            this.sPersistence = new SimulationFileManagerList(config.getSimulationRunFile(name), config.isCompetitionMode());

        } catch (IOException e) {
            throw new TournamentException("Can not create the file: " + name, e);
        }
    }

    public Tournament(ContestConfig config, String name, List<String> colonies) throws TournamentException {
        this(config, name);
        this.colonies.addAll(colonies);
    }

    /**
     * @return the maximum accumulated energy
     */
    public float getMaxEnergy() {
        return getTournamentStatistics().getMaxEnergy();
    }

    public List<Battle> getBattles() {
        return battles;
    }

    public boolean addBattle(Battle battle) {
        try {
            if (config.isCompetitionMode()) {
                persistence.append(battle);
            }

            battles.add(battle);

            if (!colonies.contains(battle.getFirstColony()))
                colonies.add(battle.getFirstColony());

            if (!colonies.contains(battle.getSecondColony()))
                colonies.add(battle.getSecondColony());

        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
            return false;
        }
        return true;
    }


    public boolean delete(String name) {
        try {
            List<Battle> toDelete = new ArrayList<>();

            for (Battle b : battles) {
                if (b.contain(name)) toDelete.add(b);
            }

            persistence.deleteFromBattlesFile(toDelete);
            battles.removeAll(toDelete);
            colonies.remove(name);

            return true;
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }

        return false;
    }

    @Override
    public int compareTo(Tournament o) {
        return name.compareTo(o.name);
    }

    public TournamentStatistics getTournamentStatistics() {
        TournamentStatistics tStats = new TournamentStatistics();

        for (Battle b : battles) {
            tStats.addWinner(b.getWinnerName(), null, null, b.getWinnerEnergy());
        }

        for (String c : colonies) {
            tStats.addWinner(c, null, null, 0.0f);
        }

        tStats.calculate();

        return tStats;
    }

    public TournamentStatistics getAccumulatedEnergy() {
        TournamentStatistics tournamentStatistics = new TournamentStatistics();

        for (Battle b : battles) {
            tournamentStatistics.addWinner(b.getWinnerName(), null, null, b.getWinnerEnergy());
        }

        for (String c : colonies) {
            tournamentStatistics.addWinner(c, null, null, 0.0f);
        }

        return tournamentStatistics;
    }


    public List<String> getColonyNames() {
        return colonies;
    }

    public void loadAllBattles() throws IOException {
        this.battles.addAll(persistence.readAll());

        for (Battle battle : battles) {
            if (!colonies.contains(battle.getFirstColony()))
                colonies.add(battle.getFirstColony());

            if (!colonies.contains(battle.getSecondColony()))
                colonies.add(battle.getSecondColony());
        }
    }

    public void save() throws IOException {
        persistence.saveAll(battles);
    }

    public void setEnabled(boolean b) {
        isEnabled = b;
    }

    public boolean isEnabled() {
        return isEnabled;
    }


    public String getName() {
        return name;
    }


    public boolean contains(Battle br) {
        for (Battle b : this.battles) {
            if (b.equals(br))
                return true;
        }
        return false;
    }

    public void deleteTargetRunFile() throws IOException {
        persistence.deleteFile(config.getBattlesTargetRunFile(name));
    }

    public void saveTargetRun(List<Battle> battles) throws IOException {
        persistence.saveAll(config.getBattlesTargetRunFile(name), battles);
    }

    public boolean existsTargetRunFile() {
        return persistence.existsFile(config.getBattlesTargetRunFile(name));
    }

    public List<Battle> generateAllBattles(List<Competitor> competitors, List<NutrientDistribution> nutrients, boolean duplicate) {
        //todo: create tests.
        List<Battle> list = new ArrayList<>();

        for (int i = 0, competitorsSize = competitors.size(); i < competitorsSize; i++) {
            Competitor c1 = competitors.get(i);
            for (int i1 = i + 1, competitorsSize1 = competitors.size(); i1 < competitorsSize1; i1++) {
                Competitor c2 = competitors.get(i1);
                for (NutrientDistribution n : nutrients) {
                    try {
                        Battle battle = new Battle(
                                c1.getId(),
                                c2.getId(),
                                n.getId(),
                                c1.getColonyName(),
                                c2.getColonyName(),
                                n.getNutrientName());

                        if (!list.contains(battle))
                            list.add(battle);

                    } catch (BattleException ex) {
                        logger.error(ex.getMessage(), ex);
                    }
                }
            }
        }

        //remove already run
        if (!duplicate) {
            for (Battle finished : battles) {
                if (list.contains(finished)) {
                    logger.warn("Battle already run: (" + battles.toString() + ")");
                    list.remove(finished);
                }
            }
        }

        return list;

    }

    public List<Battle> getMissingRunBattles(boolean deleteUnavailable) throws TournamentException {
        //todo: test
        try {
            String targetRunFile = config.getBattlesTargetRunFile(this.name);

            if (!persistence.existsFile(targetRunFile)) return new ArrayList<>();

            List<Battle> list = persistence.readAll(targetRunFile);

            if (list.isEmpty()) return list;

            //remove already run
            list.removeAll(battles);

            //remove battles containing a distribution of nutrient which is not available at the moment.
            if (deleteUnavailable) {
                List<Integer> allowedNutrients = config.getNutrients();
                List<Battle> toDelete = new ArrayList<>();

                for (Battle battle : battles) {
                    if (!allowedNutrients.contains(battle.getNutrientId())) {
                        toDelete.add(battle); //currently the nutrient is not available
                    }
                }

                list.removeAll(toDelete);
            }

            return list;
        } catch (IOException e) {
            throw new TournamentException("Cant not read target run file.", e);
        }
    }

    public Battle getUnsuccessfulBattle() throws TournamentException {
        //todo: test it
        //if the contest mode is programmer then it does not matters.
        if (config.isProgrammerMode()) return null;

        List<Battle> missingRun = getMissingRunBattles(false);
        if (missingRun.isEmpty()) {
            try {
                persistence.deleteFile(config.getBattlesTargetRunFile(this.name));
            } catch (IOException ignored) {
                logger.error("Failed to remove target run file.", ignored);
            }
            return null;
        }

        //the first battle it the one which didn't finish successful
        return missingRun.get(0);
    }

    @Override
    public void handle(Event event) {
        try {
            if (event instanceof BattleStartsEvent) {
                BattleStartsEvent tmp = (BattleStartsEvent) event;
                logger.info("Starting battle: " + tmp.getBattle().toString());

                if (config.isCompetitionMode())
                    sPersistence.appendInit(tmp.getEnvironment().getNutrient(), tmp.getEnvironment().getMOs(), tmp.getBattle());
            } else if (event instanceof BattleMovementEvent) {
                if (config.isCompetitionMode()) {
                    BattleMovementEvent tmp = (BattleMovementEvent) event;
                    sPersistence.append(tmp.getEnvironment().getNutrient(), tmp.getEnvironment().getMOs());
                }
            } else if (event instanceof BattleFinishEvent) {
                BattleFinishEvent tmp = (BattleFinishEvent) event;
                Battle battle = tmp.getBattle();
                logger.info("End of the battle. Winner " + battle.getWinnerName() + " with energy " + battle.getWinnerEnergy());

                if (config.isCompetitionMode())
                    sPersistence.appendFinish(tmp.getEnvironment().getNutrient(), tmp.getEnvironment().getMOs(), battle);
            }
        } catch (Throwable t) {
            logger.error(t.getMessage(), t);
        }
    }
}
