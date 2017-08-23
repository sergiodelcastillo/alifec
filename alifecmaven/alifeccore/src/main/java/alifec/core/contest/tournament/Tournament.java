/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

package alifec.core.contest.tournament;

import alifec.core.contest.ContestConfig;
import alifec.core.contest.tournament.battles.BattleManager;
import alifec.core.exception.CreateRankingException;
import org.apache.log4j.Logger;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;


public class Tournament implements Comparable<Tournament> {

    Logger logger = Logger.getLogger(getClass());
    
    /**
     * manager of battles. This battles are permanent
     */
    private BattleManager battleManager;

    /**
     * Colonies that have not competed!
     */
    private List<String> colonies;

    /**
     * Nombre del tournament
     */
    private final String tournamentName;

    public boolean isEnabled = false;

    private ContestConfig config;

    public Tournament(ContestConfig config, String name) throws IOException {
        this.config = config;
        this.tournamentName = name;

        battleManager = new BattleManager(config, tournamentName);
        colonies = new ArrayList<>();
    }
    /**
     * Busca la maxima energia acumulada hasta el momento y la retorna
     *
     * @return energia maxima.
     */
    public float getMaxEnergy() {
        return battleManager.getMaxEnergy();
    }

    public int size() {
        return getNames().size();
    }

    public void addColony(String c) {
        if (!colonies.contains(c))
            colonies.add(c);
    }

    public boolean add(String n1, String n2, String nut, float ene1, float ene2) {
        try {
            battleManager.add(n1, n2, nut, ene1, ene2);
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
            return false;
        }
        return true;
    }

    public boolean penalize(String name) {
        return battleManager.penalize(name) && colonies.remove(name);
    }

    public boolean delete(String name) {
        battleManager.delete(name);
        return colonies.remove(name);
    }

    @Override
    public int compareTo(Tournament o) {
        return tournamentName.compareTo(o.tournamentName);
    }

    public Hashtable<String, Integer> getRanking() throws CreateRankingException {
        Hashtable<String, Integer> h = new Hashtable<>();
        Hashtable<String, Float> acumulated = getAccumulatedEnergy();
        int MAX = 3;
        int size = MAX > acumulated.size() ? acumulated.size() : MAX;

        // agregar los que tienen puntos
        for (int index = 0; index < size; index++) {
            Float max = Collections.max(acumulated.values());
            List<String> winTemp = new ArrayList<>();

            for (String s : acumulated.keySet()) {
                if (acumulated.get(s).equals(max))
                    winTemp.add(s);
            }
            String keyWin = "";
            int maxBattlesWin = 0; // no puede ser !!

            for (String tmp : winTemp) {
                int battleswin = battleManager.getBattlesWin(tmp);
                if (maxBattlesWin != 0 && maxBattlesWin == battleswin) {
                    String s = "Ranking can't be created because there are two " +
                            "opponents with the same energy and the same number" +
                            " of battles won in the " + tournamentName;
                    throw new CreateRankingException(s);
                }
                if (battleswin > maxBattlesWin) {
                    maxBattlesWin = battleswin;
                    keyWin = tmp;
                }
            }
            // chequear<si no hay varios con el mismo numero de batallas ganadas>!!
            acumulated.remove(keyWin);
            h.put(keyWin, MAX - index);
        }
        // add the colonies that do not have points !
        for (String name : acumulated.keySet()) {
            if (!h.containsKey(name)) {
                h.put(name, 0);
            }
        }

        return h;
    }

    
    public Hashtable<String, Float> getAccumulatedEnergy() {
        Hashtable<String, Float> table = battleManager.getResults();

        for (String c : colonies) {
            if (!table.containsKey(c))
                table.put(c,  0.0f);
        }

        return table;
    }

    public BattleManager getBattleManager() {
        return battleManager;
    }

    public List<String> getNames() {
        List<String> names = battleManager.getNames();

        for (String c : colonies)
            if (!names.contains(c))
                names.add(c);

        return names;
    }

    public boolean read() {
        try {
            battleManager.read();
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
            return false;
        }
        return true;
    }

    public void save() throws IOException {
        File f = new File(config.getTournamentPath(tournamentName));

        if (!f.exists()) {
            f.mkdir();
        }
        battleManager.save();
    }

    public boolean hasBackUpFile() {
        return new File(battleManager.getBattlesBackupFile()).exists();
    }

    public void setEnabled(boolean b) {
        isEnabled = b;
    }

    public boolean isEnabled() {
        return isEnabled;
    }


    public String getName(){
        return tournamentName;
    }
}
