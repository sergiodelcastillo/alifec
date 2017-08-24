/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

package alifec.core.contest.tournament.battles;

import alifec.core.persistence.ContestConfig;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;

import static java.util.Collections.max;

public class BattleManager {
    private Logger logger = Logger.getLogger(getClass());

    private List<Battle> battles = new ArrayList<>();

    private String tournamentName;
    private ContestConfig config;

    /**
     * This Class is the manager of the class battle.
     *
     * @param config:        configuration of the contest
     * @param tournamentName The name of the tournament which contains these battles.
     * @throws IOException if can not create the file to manage the battles.
     */
    public BattleManager(ContestConfig config, String tournamentName) throws IOException {
        this.config = config;
        this.tournamentName = tournamentName;

        if (config.isCompetitionMode()) {
            File f = new File(config.getBattlesFile(tournamentName));
            if (!f.exists()) {
                if(!f.createNewFile())
                    throw new IOException("Can not create the file: " + f.getAbsolutePath());
            }
        }
    }

    /**
     * Add a battle to the battles file!
     *
     * @param n1   name of first colony
     * @param n2   name of second colony
     * @param nut  name of nutrient
     * @param ene1 accumulated energy of first colony in this battle
     * @param ene2 accumulated energy of second colony in this battle
     * @throws IOException if can not access to battles file
     */
    public void add(String n1, String n2, String nut, float ene1, float ene2)
            throws IOException {
        Battle b = new Battle(n1, n2, nut, ene1, ene2);

        if (config.isCompetitionMode()) {
            b.save(getBattlesFileName());
        }

        battles.add(b);
    }

    public void setMode(int mode) {
        this.config.setMode(mode);
    }


    /**
     * delete all instance of the colony by name.
     *
     * @param name of colony to delete
     */
    public void delete(String name) {
        List<Battle> tmp = new ArrayList<>();

        for (Battle b : battles) {
            if (b.contain(name)) {
                try {
                    tmp.add(b); // delete instance
                    b.delete(getBattlesFileName()); // delete battle of file
                } catch (IOException ex) {
                    logger.warn(ex.getMessage(), ex);
                }
            }
        }

        battles.removeAll(tmp);
    }

    public boolean penalize(String name) {
        boolean ret = true;

        try {
            List<Battle> tmp = new ArrayList<>();

            for (Battle b : battles) {
                if (b.contain(name)) {
                    if (!b.delete(getBattlesFileName()))
                        ret = false;
                    tmp.add(b);
                }
            }

            battles.removeAll(tmp);
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
            return false;
        }

        return ret;
    }

    /**
     * @return results hash(name, energy)
     */
    public Hashtable<String, Float> getResults() {
        Hashtable<String, Float> results = new Hashtable<>();

        for (Battle b : battles) {
            if (results.containsKey(b.getWinnerName())) {
                Float f = results.remove(b.getWinnerName());
                results.put(b.getWinnerName(), f + b.getWinnerEnergy());
            } else {
                results.put(b.getWinnerName(), b.getWinnerEnergy());
            }
        }
        return results;
    }

    /**
     * Get name of File of battles
     *
     * @return absolute path of File
     */

    public String getBattlesFileName() {
        return config.getBattlesFile(tournamentName);
    }

    public String getBattlesBackupFile() {
        return config.getBattlesBackupFile(tournamentName);
    }

    /**
     * get Maximun of energy
     *
     * @return max of energy of microorganism
     */
    public Float getMaxEnergy() {
        Collection<Float> values = getResults().values();
        if (values.isEmpty()) {
            return 0f;
        }
        return max(values);
    }

    public void read() throws IOException {
        String path = getBattlesFileName();

        FileReader fr = new FileReader(path);
        BufferedReader br = new BufferedReader(fr);
        String line = "";

        while (line != null) {
            try {
                line = br.readLine();

                battles.add(new Battle(line));
            } catch (IllegalArgumentException ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
    }

    public void save() {
        try {
            for (Battle b : battles) {
                b.save(getBattlesFileName());
            }
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public List<String> getNames() {
        List<String> names = new ArrayList<>();

        for (Battle b : battles) {
            if (!names.contains(b.getName_1()))
                names.add(b.getName_1());
            if (!names.contains(b.getName_2()))
                names.add(b.getName_2());
        }
        return names;
    }

    public int getBattlesWin(String name) {
        int count = 0;

        for (Battle b : battles) {
            if (b.getWinnerName().equals(name))
                ++count;
        }
        return count;
    }

    public boolean contain(BattleRun br) {
        for (Battle b : this.battles) {
            if (b.getNutrient().equalsIgnoreCase(br.nutrient) &&
                    ((b.getName_1().equalsIgnoreCase(br.name1) &&
                            b.getName_2().equalsIgnoreCase(br.name2)) ||
                            (b.getName_1().equalsIgnoreCase(br.name2) &&
                                    b.getName_2().equalsIgnoreCase(br.name1))))
                return true;
        }
        return false;
    }


}
