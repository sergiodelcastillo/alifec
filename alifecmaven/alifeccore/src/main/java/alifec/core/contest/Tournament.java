package alifec.core.contest;

import alifec.core.contest.oponentInfo.TournamentStatistics;
import alifec.core.event.Event;
import alifec.core.event.Listener;
import alifec.core.event.impl.BattleEvent;
import alifec.core.exception.BattleException;
import alifec.core.exception.TournamentException;
import alifec.core.persistence.SimulationFileManager;
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

    private final String name;
    private Logger logger = LogManager.getLogger(getClass());
    private List<Battle> battles;
    private List<String> colonies;
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
        return getStatistics().getMaxEnergy();
    }

    public List<Battle> getBattles() {
        return battles;
    }

    public boolean addBattle(Battle battle) {
        //todo: throw the exception!! it should be handled outside

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

    public TournamentStatistics getStatistics() {
        //todo: testssss!!!
        TournamentStatistics statistics = new TournamentStatistics();

        for (Battle b : battles) {
            statistics.addWinner(b.getWinnerName(), null, null, b.getWinnerEnergy());
        }

        for (String c : colonies) {
            statistics.addWinner(c, null, null, 0.0f);
        }

        statistics.calculate();

        return statistics;
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

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean b) {
        isEnabled = b;
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

    public List<Battle> generateMissingBattles(List<Competitor> competitors, List<NutrientDistribution> nutrients, boolean duplicate) {
        //todo: create tests.
        List<Battle> list = new ArrayList<>();

        for (int i = 0; i < competitors.size(); i++) {
            Competitor c1 = competitors.get(i);
            for (int j = i + 1; j < competitors.size(); j++) {
                Competitor c2 = competitors.get(j);
                for (NutrientDistribution n : nutrients) {
                    try {
                        Battle battle = new Battle(
                                c1.getId(),
                                c2.getId(),
                                n.getId(),
                                c1.getColonyName(),
                                c2.getColonyName(),
                                n.getNutrientName());

                        if (duplicate || !battles.contains(battle)) {
                            list.add(battle);
                        }

                    } catch (BattleException ex) {
                        logger.error(ex.getMessage(), ex);
                    }
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

        //the first battle is the one which didn't finish successful
        return missingRun.get(0);
    }

    @Override
    public void handle(Event event) {
        try {
            if (event instanceof BattleEvent) {
                BattleEvent battleEvent = (BattleEvent) event;

                switch (battleEvent.getStatus()) {
                    case START:
                        handleBattleStart(battleEvent);
                        break;
                    case MOVEMENT:
                        handleBattleMovement(battleEvent);
                        break;
                    case FINISH:
                        handleBattleFinish(battleEvent);
                        break;
                }
            }
        } catch (Throwable t) {
            logger.error(t.getMessage(), t);
        }
    }

    private void handleBattleFinish(BattleEvent event) throws IOException {
        Battle battle = event.getBattle();
        logger.info("End of the battle. Winner " + battle.getWinnerName() + " with energy " + battle.getWinnerEnergy());

        if (config.isCompetitionMode())
            sPersistence.appendFinish(event.getEnvironment().getNutrient(), event.getEnvironment().getMOs(), battle);
    }

    private void handleBattleMovement(BattleEvent event) throws IOException {
        if (config.isCompetitionMode()) {
            sPersistence.append(event.getEnvironment().getNutrient(), event.getEnvironment().getMOs());
        }
    }

    private void handleBattleStart(BattleEvent event) throws IOException {
        logger.info("Starting battle: " + event.getBattle().toString());

        if (config.isCompetitionMode())
            sPersistence.appendInit(event.getEnvironment().getNutrient(), event.getEnvironment().getMOs(), event.getBattle());
    }
}
