/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

package alifec.core.contest.tournament.battles;

import alifec.core.contest.ContestConfig;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.Hashtable;
import java.util.Vector;

import static java.util.Collections.max;

public class BattleManager {

    static Logger logger = Logger.getLogger(BattleManager.class);

    private Vector<Battle> battles = new Vector<>();

    private String tournamentName;
    private ContestConfig config;

    /**
     * This Class is the manager of the class battle.
     *
     * @param config: configuration of the contest
     * @param tournamentName The name of the tournament which contains these battles.
     * @throws IOException if can not create the file to manage the battles.
     */
    public BattleManager(ContestConfig config, String tournamentName) throws IOException {
        this.config = config;
        this.tournamentName = tournamentName;

        if (config.getMode() == ContestConfig.COMPETITION_MODE) {
            File f = new File(config.getBattlesFile(tournamentName));
            if (!f.exists()) f.createNewFile();
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

        if (config.getMode() == ContestConfig.COMPETITION_MODE) {
            b.save(getBattlesFileName());
        }

        battles.addElement(b);
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
        Vector<Battle> tmp = new Vector<>();

        for (Battle b : battles) {
            if (b.contain(name)) {
                try {
                    tmp.addElement(b); // delete instance
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
            Vector<Battle> tmp = new Vector<>();

            for (Battle b : battles) {
                if (b.contain(name)) {
                    if (!b.delete(getBattlesFileName()))
                        ret = false;
                    tmp.addElement(b);
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
        try {
            return max(getResults().values());
        } catch (java.util.NoSuchElementException ex) {
            return 0f;
        }
    }

    public void read() throws IOException {
        String path = getBattlesFileName();

        FileReader fr = new FileReader(path);
        BufferedReader br = new BufferedReader(fr);
        String line = "";

        while (line != null) {
            try {
                line = br.readLine();

                battles.addElement(new Battle(line));
            } catch (IllegalArgumentException ignored) {
            }
        }
    }

    public boolean save() {
        try {
            for (Battle b : battles) {
                b.save(getBattlesFileName());
            }
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
            return false;
        }
        return true;
    }

    public Vector<String> getNames() {
        Vector<String> names = new Vector<>();

        for (Battle b : battles) {
            if (!names.contains(b.getName_1()))
                names.addElement(b.getName_1());
            if (!names.contains(b.getName_2()))
                names.addElement(b.getName_2());
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
