/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

package alifec.core.contest.tournament;

import alifec.core.contest.tournament.battles.BattleManager;
import alifec.core.exception.CreateRankingException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Tournament implements Comparable<Tournament> {
    /**
     * manager of battles. This battles are permanent
     */
    private BattleManager battleManager;

    /**
     * Colonies that have not competed!
     */
    private Vector<String> colonies;

    /**
     * Nombre del tournament
     */
    public final String NAME;
    public boolean isEnabled = false;

    /**
     * @param n name of tournament
     * @param path absolute URL o tournament
     * @param mode of contest
     * @throws IOException is thrown if can not find or create the battles file
     */
    public Tournament(String n, String path, int mode) throws IOException {
        NAME = n;
        battleManager = new BattleManager(path + File.separator + NAME, mode);
        colonies = new Vector<String>();
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
            colonies.addElement(c);
    }

    public boolean add(String n1, String n2, String nut, float ene1, float ene2) {
        try {
            battleManager.add(n1, n2, nut, ene1, ene2);
        } catch (FileNotFoundException ex) {
            System.out.println("FileNotFoundException ---> Tournament.add()");
            //Logger.getLogger(Tournament.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (IOException ex) {
            System.out.println("IOException ---> Tournament.add()");
//            Logger.getLogger(Tournament.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    public boolean penalice(String name) {
        return battleManager.penalice(name) && colonies.remove(name);
    }

    public boolean delete(String name) {
        battleManager.delete(name);
        return colonies.remove(name);
    }

    @Override
    public int compareTo(Tournament o) {
        return NAME.compareTo(o.NAME);
    }

    public Hashtable<String, Integer> getRanking() throws CreateRankingException {
        Hashtable<String, Integer> h = new Hashtable<String, Integer>();
        Hashtable<String, Float> acumulated = getAccumulatedEnergy();
        int MAX = 3;
        int size = MAX > acumulated.size() ? acumulated.size() : MAX;

        // agregar los que tienen puntos
        for (int index = 0; index < size; index++) {
            Float max = Collections.max(acumulated.values());
            Vector<String> winTemp = new Vector<String>();

            for (String s : acumulated.keySet()) {
                if (acumulated.get(s).equals(max))
                    winTemp.addElement(s);
            }
            String keyWin = "";
            int maxBattlesWin = 0; // no puede ser !!

            for (String tmp : winTemp) {
                int battleswin = battleManager.getBattlesWin(tmp);
                if (maxBattlesWin != 0 && maxBattlesWin == battleswin) {
                    String s = "Ranking can't be created because there are two " +
                            "opponents with the same energy and the same number" +
                            " of battles won in the " + NAME;
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

    public Vector<String> getNames() {
        Vector<String> names = battleManager.getNames();

        for (String c : colonies)
            if (!names.contains(c))
                names.addElement(c);

        return names;
    }

    public boolean read() {
        try {
            battleManager.read();
        } catch (IOException ex) {
            System.out.println("Tournamenent.read()");
            Logger.getLogger(Tournament.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    public void save(String path) {
        String battlePath = path + File.separator + this.NAME;
        File f = new File(battlePath);

        if (!f.exists()) {
            f.mkdir();
        }
        battleManager.save();
    }

    public boolean hasBackUpFile() {
        return new File(battleManager.getPath() + File.separator + "battles_backup.csv").exists();
    }

    public void setEnabled(boolean b) {
        isEnabled = b;
    }

    public boolean isEnabled() {
        return isEnabled;
    }
}
