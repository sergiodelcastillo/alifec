/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

package controller.java.tournament;

import controller.java.battle.BattleManager;
import controller.java.battle.BattleRun;
import controller.java.contest.ContestMode;
import data.java.Config;
import data.java.Log;
import data.java.contest.RankingDTO;
import data.java.tournament.AccumulatedDTO;
import exceptions.CreateRankingException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;


public class Tournament implements Comparable<Tournament> {
    /**
     * Manager of all battles in this tournament.
     */
    private BattleManager battleManager;

    /**
     * Colonies that have not competed
     */
    private ArrayList<String> colonies;

    /**
     * the name of the tournament
     */
    private final String name;

    private boolean isEnabled = false;

    /**
     * @param name the name of tournament
     * @param mode mode of contest
     * @throws java.io.IOException is thrown if can not find or create the battle file
     */
    public Tournament(String name, int mode) throws IOException {
        //create tournament folder if it is not exists.
        if (mode == ContestMode.COMPETITION_MODE) {
            File f = new File(Config.getInstance().getAbsoluteTournamentFolder(name));

            if (!f.exists()) {
                f.mkdirs();
            }
        }

        this.name = name;
        this.battleManager = new BattleManager(Config.getInstance().getAbsoluteBattleFile(name), mode);
        this.colonies = new ArrayList<String>();
    }

    public Tournament(String name, int mode, ArrayList<String>colonies) throws IOException {
        this(name, mode);

        if(colonies!= null){
            this.colonies.addAll(colonies);
        }
    }

    /*
     if (colonies == null) {
            throw new CreateTournamentException("Invalid argument");
        }

        try {
            Tournament t = new Tournament(getNextName(), mode);
            t.setEnabled(true);

            for (String c : colonies) {
                t.addColony(c);
            }

            if (selected >= 0)
                tournaments.get(selected).setEnabled(false);

            tournaments.add(t);
            selected = tournaments.indexOf(t);
        } catch (IOException ex) {
            throw new CreateTournamentException("Cannot create the tournament");
        }
    * */

    /**
     * @return maximum energy
     * @see controller.java.battle.BattleManager#getMaxEnergy()
     */
    public float getMaxEnergy() {
        return battleManager.getMaxEnergy();
    }

    /**
     * The total of instances of colonies.
     *
     * @return
     */
    public int size() {
        return colonies.size();//getNames().getSize();
    }

    /**
     * Add a colony name to this tournament
     *
     * @param colonyName the name of the colony
     */
    public void addColony(String colonyName) {
        if (!colonies.contains(colonyName)) {
            colonies.add(colonyName);
        }
    }

    /**
     * Add a battle to this tournament
     *
     * @param battle a instance of battle
     * @throws IOException
     * @see controller.java.battle.BattleManager#add(controller.java.battle.BattleRun)
     */
    public void addBattle(BattleRun battle) throws IOException {
        battleManager.add(battle);

        this.addColony(battle.getFirstName());
        this.addColony(battle.getSecondName());
    }

    /**
     * penalize the colony <b>colonyName</b>. This method call to deleteColony
     *
     * @param colonyName the colony name
     * @return true if it is successful
     * @see controller.java.tournament.Tournament#deleteColony(String)
     */
    public boolean penalize(String colonyName) {
        return this.deleteColony(colonyName);
    }

    /**
     * Delete the colony <b>colonyName </b> of colonies list and battles list.
     *
     * @param colonyName the colony name
     * @return true is successful
     */
    public boolean deleteColony(String colonyName) {
        return battleManager.delete(colonyName) &&
                colonies.remove(colonyName);
    }

    @Override
    public int compareTo(Tournament o) {
        return name.compareTo(o.name);
    }

    public ArrayList<RankingDTO> getRanking() throws CreateRankingException {
        ArrayList<RankingDTO> ranking = new ArrayList<RankingDTO>();
        ArrayList<AccumulatedDTO> accumulated = getAccumulatedEnergy();

        // validate the accumulated energy
        for (int i = 0; i < Math.min(accumulated.size() - 1, 3); i++) {
            if (Float.compare(accumulated.get(i).getEnergy(), accumulated.get(i + 1).getEnergy()) == 0) {
                String s = "Ranking can't be created because there are two " +
                        "opponents with the same accumulated energy in " + name + ".\nThe Opponents are " +
                        accumulated.get(i).getName() + " and " + accumulated.get(i + 1).getName();
                Log.printlnAndSave(s);
                throw new CreateRankingException(s);
            }
        }
        int index = 3;

        for (AccumulatedDTO ac : accumulated) {
            ranking.add(new RankingDTO(ac.getName(), index));

            if (index > 0)
                index--;
        }

        return ranking;
    }

    /**
     * Get the accumulated energy of this tournament
     *
     * @return HashTable with accumulated energy.
     */
    public ArrayList<AccumulatedDTO> getAccumulatedEnergy() {
        ArrayList<AccumulatedDTO> accumulated = battleManager.getResults();
        for (String colony : colonies) {
            addToAccumulatedEnergy(accumulated, colony);
        }
        Collections.sort(accumulated);
        Collections.reverse(accumulated);
        return accumulated;

    }

    private void addToAccumulatedEnergy(ArrayList<AccumulatedDTO> accumulated, String colony) {
        for (AccumulatedDTO dto : accumulated) {
            if (dto.getName().equals(colony)) {
                return;
            }
        }

        accumulated.add(new AccumulatedDTO(colony, 0f));
    }


    /**
     * Return the manager of battles
     *
     * @return a instance of manager of battles
     */
    public BattleManager getBattleManager() {
        return battleManager;
    }

    /**
     * Get the name of the colonies in the tournament
     *
     * @return list of colony name.
     */
    public ArrayList<String> getNames() {
        return this.colonies;
    }

    /**
     * Load all battles that was saved in the battles file.
     *
     * @return true is successful
     */
    public void load() throws IOException {
        battleManager.load();
    }

    /**
     * delete the tournament folder
     *
     * @return if it is successful
     */
    public boolean deleteFolder() {
        return battleManager.deleteFile() &&
                new File(Config.getInstance().getAbsoluteTournamentFolder(name)).delete();

    }

    /**
     * @return true is the tournament has a backup file.
     * @see controller.java.battle.BattleManager#hasBackUp()
     */
    public boolean hasBackUpFile() {
        return battleManager.hasBackUp();
    }

    /**
     * The attribute  isEnabled is used by UI to manage the tournaments.
     *
     * @param b true is enabled
     */
    public void setEnabled(boolean b) {
        isEnabled = b;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public String getName() {
        return name;
    }

    public boolean deleteBackUp() {
        return this.battleManager.deleteBackUp();
    }

    public void saveBackUp(ArrayList<BattleRun> list) throws IOException {
        this.battleManager.createBackUp(list);
    }

    private void ensureDirectory() {
        File f = new File(Config.getInstance().getAbsoluteTournamentFolder(name));

        if (!f.exists()) {
            Log.println("Creating tournament: " + f.getAbsolutePath());
            f.mkdirs();
        }
    }
    public void setMode(int mode) throws IOException {
        if(mode == ContestMode.COMPETITION_MODE){
            ensureDirectory();
        }
        battleManager.setMode(mode);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tournament that = (Tournament) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    public void update() throws IOException {
        String path = Config.getInstance().getAbsoluteBattleFile(this.getName());
        battleManager.setFile(path);
        battleManager.setMode(Config.getInstance().getMode());
    }
}
