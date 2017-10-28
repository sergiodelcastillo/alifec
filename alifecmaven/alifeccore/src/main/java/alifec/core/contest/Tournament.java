
package alifec.core.contest;

import alifec.core.exception.BattleException;
import alifec.core.exception.CreateRankingException;
import alifec.core.exception.TournamentException;
import alifec.core.persistence.TournamentFileManager;
import alifec.core.persistence.config.ContestConfig;
import alifec.core.simulation.Competitor;
import alifec.core.simulation.NutrientDistribution;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.*;

import static java.util.Collections.max;


public class Tournament implements Comparable<Tournament> {

    private Logger logger = LogManager.getLogger(getClass());

    private List<Battle> battles;
    /**
     * Colonies that have not competed!
     */
    private List<String> colonies;

    private final String name;

    private TournamentFileManager persistence;

    private boolean isEnabled = false;

    private ContestConfig config;

    public Tournament(ContestConfig config, String name) throws TournamentException {
        this.config = config;
        this.name = name;

        this.battles = new ArrayList<>();
        this.colonies = new ArrayList<>();
        this.persistence = new TournamentFileManager();

        if (config.isCompetitionMode()) {
            try {
                persistence.createFileAndPathIfNotExists(config.getBattlesFile(this.name));
            } catch (IOException e) {
                throw new TournamentException("Can not create the file: " + name, e);
            }
        }
    }

    /**
     * @return the maximum accumulated energy
     */
    public float getMaxEnergy() {
        Collection<Float> values = getAccumulatedEnergy().values();

        return values.isEmpty() ? 0f : max(values);
    }

    public int size() {
        return battles.size();
    }

    public void addColony(String c) {
        if (!colonies.contains(c))
            colonies.add(c);
    }

    public boolean addResult(Battle battle) {
        try {
            if (config.isCompetitionMode()) {
                persistence.append(config.getBattlesFile(name), battle);
            }

            battles.add(battle);
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

            persistence.delete(config.getBattlesFile(this.name), toDelete);
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

    public Hashtable<String, Integer> getRanking() throws CreateRankingException {
        Hashtable<String, Integer> h = new Hashtable<>();
        Hashtable<String, Float> accumulated = getAccumulatedEnergy();
        int MAX = 3;
        int size = MAX > accumulated.size() ? accumulated.size() : MAX;

        // add colonies which already earn points.
        for (int index = 0; index < size; index++) {
            Float max = Collections.max(accumulated.values());
            List<String> winTemp = new ArrayList<>();

            for (String s : accumulated.keySet()) {
                if (accumulated.get(s).equals(max))
                    winTemp.add(s);
            }
            String keyWin = "";
            int maxBattlesWin = 0; // no puede ser !!

            for (String tmp : winTemp) {
                int battlesWon = getBattlesWon(tmp);
                if (maxBattlesWin != 0 && maxBattlesWin == battlesWon) {
                    String s = "Ranking can't be created because there are two " +
                            "opponents with the same energy and the same number" +
                            " of battles won in the " + name;
                    throw new CreateRankingException(s);
                }
                if (battlesWon > maxBattlesWin) {
                    maxBattlesWin = battlesWon;
                    keyWin = tmp;
                }
            }

            accumulated.remove(keyWin);
            h.put(keyWin, MAX - index);
        }
        // add the colonies that do not have points !
        for (String name : accumulated.keySet()) {
            if (!h.containsKey(name)) {
                h.put(name, 0);
            }
        }

        return h;
    }

    private int getBattlesWon(String name) {
        int count = 0;

        for (Battle b : battles) {
            if (b.getWinnerName().equals(name))
                ++count;
        }
        return count;
    }

    public Hashtable<String, Float> getAccumulatedEnergy() {
        Hashtable<String, Float> results = new Hashtable<>();

        for (Battle b : battles) {
            if (results.containsKey(b.getWinnerName())) {
                Float f = results.remove(b.getWinnerName());
                results.put(b.getWinnerName(), f + b.getWinnerEnergy());
            } else {
                results.put(b.getWinnerName(), b.getWinnerEnergy());
            }
        }

        for (String c : colonies) {
            if (!results.containsKey(c))
                results.put(c, 0.0f);
        }

        return results;
    }


    public List<String> getColonyNames() {
        List<String> names = new ArrayList<>();

        //add from battles
        for (Battle b : battles) {
            if (!names.contains(b.getFirstColony()))
                names.add(b.getFirstColony());
            if (!names.contains(b.getSecondColony()))
                names.add(b.getSecondColony());
        }

        //add from colonies
        for (String c : colonies)
            if (!names.contains(c))
                names.add(c);

        return names;
    }

    public boolean load() {
        try {
            this.battles.addAll(persistence.readAll(config.getBattlesFile(name)));
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
            return false;
        }
        return true;
    }

    public void save() throws IOException {
        persistence.saveAll(config.getBattlesFile(name), battles);
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

    public List<Battle> generateAllBattles(List<Competitor> competitos, List<NutrientDistribution> nutrients, boolean duplicate) {
        //todo: create tests.
        List<Battle> list = new ArrayList<>();

        for (Competitor c1 : competitos) {
            for (Competitor c2 : competitos) {
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

    public List<Battle> getMissingRunBattles() throws IOException {
        //todo: test
        List<Battle> list = persistence.readAll(config.getBattlesTargetRunFile(this.name));

        if (list.isEmpty()) return list;

        List<Battle> alreadyRun = persistence.readAll(config.getBattlesFile(this.name));

        //remove already run
        list.removeAll(alreadyRun);

        List<Integer> allowedNutrients = config.getNutrients();

        List<Battle> toDelete = new ArrayList<>();

        for (Battle battle : battles) {
            if (!allowedNutrients.contains(battle.getNutrientId())) {
                toDelete.add(battle); //currently the nutrient is not available
            }
        }
        //remove battles containing a distribution of nutrient which is not available at the moment.
        list.removeAll(toDelete);

        return list;
    }
}
