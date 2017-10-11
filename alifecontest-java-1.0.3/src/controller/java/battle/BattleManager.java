/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

package controller.java.battle;

import controller.java.contest.ContestMode;
import data.java.Log;
import data.java.tournament.AccumulatedDTO;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

public class BattleManager {
    /**
     * Read a file <b>file</b> and create a list of battles.
     *
     * @param file the absolute path of the  battles file
     * @return a list of battles
     * @throws IOException if can access to file.
     */
    public static ArrayList<BattleRun> loadBattleRun(String file) throws IOException {
        ArrayList<BattleRun> list = new ArrayList<BattleRun>();
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String line;

        while ((line = br.readLine()) != null) {
            try {
                list.add(new BattleRun(line));
            } catch (IllegalArgumentException ignored) {
                Log.println("Ignoring battle: " + line);
            }
        }
        br.close();
        fr.close();

        return list;
    }

    private int mode;
    private String file;

    private ArrayList<Battle> battles;

    /**
     * This Class is the manager of the class battle.
     *
     * @param battlesFile: absolute battlesFile of battle
     * @param mode         is the mode of contest. It can be COMPETITION of PROGRAMMER mode
     * @throws java.io.IOException if can not create the file to manage the battle.
     */
    public BattleManager(String battlesFile, int mode) throws IOException {
        this.file = battlesFile;
        this.mode = mode;
        this.battles = new ArrayList<Battle>();

        // create battle file if not exists
        if (mode == ContestMode.COMPETITION_MODE) {
            File f = new File(battlesFile);
            if (!f.exists()) f.createNewFile();
        }
    }

    /**
     * Add a battle to the file of battles
     *
     * @param b the battle
     * @throws IOException if can not access to the battle file
     */
    public void add(Battle b) throws IOException {
        if (mode == ContestMode.COMPETITION_MODE) {
            b.append(file);
        }

        battles.add(b);
    }

    /**
     * Add a battle to the file of battles
     *
     * @param battle the battle
     * @throws IOException if can not access to the battle file
     */
    public void add(BattleRun battle) throws IOException {
        Battle b = new Battle();

        b.setFirstOpponent(battle.getFirstName(), battle.getFirstEnergy());
        b.setSecondOpponent(battle.getSecondName(), battle.getSecondEnergy());
        b.setNutrientId(battle.getNutrientId());
        add(b);
    }

    /**
     * Set the mode of the contest.
     *
     * @param mode can be COMPETITION_MODE or PROGRAMMER_MODE
     */
    public void setMode(int mode) throws IOException {
        if (this.mode == ContestMode.PROGRAMMER_MODE) {
            save(file, battles);
        }
        this.mode = mode;


    }


    /**
     * delete all instance of the colony by name in the file battles.csv.
     *
     * @param name of colony to delete
     */
    public boolean delete(String name) {
        boolean status = true;
        ArrayList<Battle> toDelete = new ArrayList<Battle>();

        for (Battle b : battles) {
            if (b.contain(name)) {
                try {
                    toDelete.add(b); // delete instance
                    status &= b.delete(file); // delete battle of file
                } catch (IOException ex) {
                    Log.printlnAndSave("Can't delete the battle: " + b, ex);
                }
            }
        }
        battles.removeAll(toDelete);
        return status;
    }


    /**
     * @return results hash(name, energy)
     */
    public ArrayList<AccumulatedDTO> getResults() {
        ArrayList<AccumulatedDTO> results = new ArrayList<AccumulatedDTO>();

        for (Battle b : battles) {
            addToResults(results, b.getWinnerName(), b.getWinnerEnergy());
        }

        Collections.sort(results);
        Collections.reverse(results);

        return results;
    }

    private void addToResults(ArrayList<AccumulatedDTO> results, String name, float energy) {
        for (AccumulatedDTO r : results) {
            if (r.getName().equals(name)) {
                r.add(energy);
                return;
            }
        }

        results.add(new AccumulatedDTO(name, energy));
    }


    /**
     * get Maximum of energy
     *
     * @return max of energy of microorganism
     */
    public float getMaxEnergy() {
        float energy = 0.0f;

        for (AccumulatedDTO dto : getResults()) {
            if (dto.getEnergy() > energy) {
                energy = dto.getEnergy();
            }
        }

        return energy;
    }

    public void load() throws IOException {
        try {
            load(file);
        } catch (FileNotFoundException ex) {
            //create the file..
            Log.printlnAndSave("Creating the file:" + file);
            new File(file).createNewFile();
        }
    }

    /**
     * Read a battle file and create all instances of battles per line.
     *
     * @throws IOException
     */
    public void load(String file) throws IOException {
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String line = "";

        while (line != null) {
            try {
                line = br.readLine();

                battles.add(new Battle(line));
            } catch (IllegalArgumentException ignored) {
                Log.println("Ignoring battle: " + line);
            }
        }
        br.close();
        fr.close();
    }

    /**
     * Save all battles to the battle path
     *
     * @return true if the status is successful.
     */
    void save(String path, ArrayList<? extends AbstractBattle> battles) throws IOException {

        FileWriter fw = new FileWriter(path, false);
        BufferedWriter pw = new BufferedWriter(fw);

        // print line.
        for (AbstractBattle b : battles) {
            pw.append(b.toCSV());
            pw.append('\n');
        }

        pw.close();
        fw.close();

    }

    public void createBackUp(ArrayList<BattleRun> list) throws IOException {
        if (mode == ContestMode.COMPETITION_MODE) {
            save(file.replace(".csv", "_backup.csv"), list);
        }
    }

    /**
     * @param br a battle
     * @return true if the battle br is into the list of battles.
     */
    public boolean contain(BattleRun br) {
        for (Battle b : this.battles) {
            if (b.contain(br)) return true;
        }

        return false;
    }

    /**
     * @return the count of battles.
     */
    public int size() {
        return battles.size();
    }

    /**
     * return a battle
     *
     * @param index index of battle
     * @return a battle that correspond with the index <b> index </b>
     */
    public Battle get(int index) {
        return this.battles.get(index);
    }

    /**
     * Delete the file of battles.
     *
     * @return true if it is successful
     */
    public boolean deleteFile() {
        File f = new File(file);

        return !f.exists() || f.delete();

    }

    /**
     * @return true if the back-up was deleted successfully. false in other case.
     */
    public boolean deleteBackUp() {
        if (mode == ContestMode.PROGRAMMER_MODE) {
            return false;
        }

        File f = new File(file.replace(".csv", "_backup.csv"));

        boolean status = !f.exists() || f.delete();

        if (status) {
            Log.println("Deleting backup file " + f.getAbsolutePath() + " [OK]");
        } else {
            Log.println("Deleting backup file " + f.getAbsolutePath() + " [FAIL]");
        }

        return status;
    }

    /**
     * @return true if has a back-up file.
     */
    public boolean hasBackUp() {
        return new File(file.replace(".csv", "_backup.csv")).exists();
    }

    public int getMode() {
        return mode;
    }

    public void setFile(String path) {
        this.file = path;
    }
}
