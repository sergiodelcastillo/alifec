package alifec.core.simulation; /**
 * @author Sergio Del Castillo
 * @email: sergio.jose.delcastillo@gmail.com
 */

import alifec.core.contest.ContestConfig;
import alifec.core.exception.MoveMicroorganismException;
import alifec.core.contest.tournament.battles.BattleRun;
import org.apache.log4j.Logger;

import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

public class Environment {

    static Logger logger = org.apache.log4j.Logger.getLogger(Environment.class);
    /**
     * Agar of environment.
     */
    Agar agar;

    /**
     * All colonies!!
     */
    private List<Colony> colonies;

    /**
     * Reference to position of MOs.
     */
    Cell microorganism[][] = new Cell[Defs.DIAMETER][Defs.DIAMETER];

    /**
     * First Oponent .. temporal reference !!
     */
    Colony c1;

    /**
     * Second Oponent .. temporal reference !!
     */
    Colony c2;

    /**
     * Information of current battle .. temporal reference !!
     */
    private BattleRun oponents;

    private ColonyRule[] rules = {
            new LifeRule(),
            new EatRule(),
            new MoveRule(),
            new AttackRule(),
            new LoveRule()
    };

    /**
     * @param config is the Contest configuration
     */
    public Environment(ContestConfig config) {
        agar = new Agar();
        colonies = new ArrayList<>();

        logger.info("Loading Colonies");
        logger.info("Loading Java Colonies");

        //todo: decide if list java files should use the .java or .class
        for (String name : SourceCodeFilter.listNamesJava(config.getMOsPath())) {
            try {
                JavaColony.addClassPath(config.getCompilationTarget());
                colonies.add(new JavaColony(colonies.size(), "MOs." + name));
                logger.info(name + " [OK]");
            } catch (Exception ex) {
                logger.warn(name + " [FAIL]");
            }
        }

        // loading library
        logger.info("Loading C++ Colonies ");

        if (CppColony.loadLibrary(config.getMOsPath() + File.separator + "lib" + File.separator + "MOs" + File.separator)) {
            logger.info("Loading C++ Library [OK]");

            for (String name : SourceCodeFilter.listNamesCpp(config.getMOsPath())) {
                try {
                    // to initialise the name of colony
                    colonies.add(new CppColony(colonies.size(), name));
                    logger.info(name + " [OK]");
                } catch (ClassNotFoundException ex) {
                    logger.warn(name + " [FAIL]");
                }
            }
        } else {
            logger.warn("Loading C++ Library [FAIL]");
        }

        // set the environment !!
        ColonyRule.env = this;
        Petri.getInstance().setEnvironment(this);
    }

    public boolean delete(String name) {
        for (Colony c : colonies) {
            if (name.equalsIgnoreCase(c.getName())) {
                return colonies.remove(c);
            }
        }
        return false;
    }

    public void generateOpponents(BattleRun b) {
        oponents = b;

        agar.setNutrient(b.nutrientID);

        for (int i = 0; i < colonies.size(); i++) {
            if (colonies.get(i).id == b.ID1)
                c1 = colonies.get(i);
        }

        for (int i = 0; i < colonies.size(); i++) {
            if (colonies.get(i).id == b.ID2)
                c2 = colonies.get(i);
        }


        Petri.getInstance().agar = agar;
        Petri.getInstance().firstOpponent = c1;
        Petri.getInstance().secondOpponent = c2;

        microorganism = new Cell[Defs.DIAMETER][Defs.DIAMETER];
        init(c1);
        init(c2);

    }

    public final boolean init(Colony c) {
        Random r = new Random();

        c.clear();

        for (int index = 0; index < Defs.MO_INITIAL; ) {
            Point p = new Point(r.nextInt(Defs.DIAMETER), r.nextInt(Defs.DIAMETER));

            if (createInstance(p, Defs.E_INITIAL, c.id))
                ++index;
        }

        return true;
    }

    public boolean moveColonies() throws MoveMicroorganismException {
        List<Cell> allOps = new ArrayList<>();

        for (int i = 0; i < c1.size(); i++)
            allOps.add(c1.getMO(i));

        for (int i = 0; i < c2.size(); i++)
            allOps.add(c2.getMO(i));

        // randomize list!
        Collections.shuffle(allOps);

        while (!allOps.isEmpty()) {
            Cell mo = allOps.remove(allOps.size() - 1);
            Colony current, enemy;

            if (mo.id == c2.id) {
                current = c2;
                enemy = c1;
            } else {
                current = c1;
                enemy = c2;
            }

            int indexMO = current.getMOs().indexOf(mo);
            if (indexMO < 0)
                continue;

            boolean mitosis;
            Movement mov;

            try {
                current.update(indexMO, mo.ene, mo.pos.x, mo.pos.y);
                mov = current.move(indexMO);
                mitosis = current.mitosis(indexMO);
            } catch (Exception ex) {
                String txt = "The colony: " + current.getName() + " has been Penalized.";
                throw new MoveMicroorganismException(txt, current.getName(), ex);
            }

            for (ColonyRule rule : rules) {
                if (rule.apply(current, enemy, mo, mov, mitosis)) {
                    break;      // break of rules
                }
            }
        }

        if (new Random().nextInt(4) > 1)
            agar.moveRandom();

        return updateStatus();
    }

    /**
     * Update the status of the current battle.
     *
     * @return true if the battles was finished, false otherwise.
     */
    private boolean updateStatus() {
        if (c1 == null || c2 == null) {
            return true;
        }

        if (c1.isDied()) {
            oponents.winnerID = oponents.ID2;
            oponents.winner_energy = c2.getEnergy();
            return true;
        }

        if (c2.isDied()) {
            oponents.winnerID = oponents.ID1;
            oponents.winner_energy = c1.getEnergy();
            return true;
        }

        return false;
    }


    public boolean inDish(int x, int y) {
        return ((x - Defs.RADIUS) * (x - Defs.RADIUS) + (y - Defs.RADIUS) * (y - Defs.RADIUS)) <
                (Defs.RADIUS * Defs.RADIUS);
    }

    boolean moveMO(Point a, Point b) {
        if (a == null || b == null)
            return false;

        if (microorganism[a.x][a.y] == null)
            return false;

        if (microorganism[b.x][b.y] != null)
            return false;

        microorganism[b.x][b.y] = microorganism[a.x][a.y];
        microorganism[a.x][a.y] = null;
        microorganism[b.x][b.y].pos.x = b.x;
        microorganism[b.x][b.y].pos.y = b.y;
        return true;
    }

    boolean killMO(int x, int y) {
        if (!inDish(x, y)) {
            return false;
        }

        Cell mo = microorganism[x][y];

        microorganism[x][y] = null;
        return mo != null && ((mo.id == c1.id) ? c1.kill(mo) : c2.kill(mo));

    }

    boolean createInstance(Point pos, float ene, int id) {
        if (pos == null || !inDish(pos.x, pos.y) ||
                ene <= 0 || ene > Defs.E_INITIAL ||
                microorganism[pos.x][pos.y] != null)
            return false;

        Cell mo = new Cell(id);
        mo.pos.x = pos.x;
        mo.pos.y = pos.y;
        mo.ene = ene;

        microorganism[pos.x][pos.y] = mo;

        return id == c1.id ? c1.createInstance(mo) : c2.createInstance(mo);
    }

    /*
      ***************************************************************************
      * Metodos Gets																				*
      * *************************************************************************
      */

    public Agar getAgar() {
        return agar;
    }

    public Colony getFirstOponent() {
        return c1;
    }

    public Colony getSecondOponent() {
        return c2;
    }

    public BattleRun getResults() {
        return oponents;
    }

    public List<String> getNames() {
        List<String> tmp = new ArrayList<>();

        for (Colony c : colonies)
            tmp.add(c.getName());

        return tmp;
    }

    public Hashtable<String, Integer> getOps() {
        Hashtable<String, Integer> r = new Hashtable<>();
        for (Colony c : colonies) {
            r.put(c.getName(), c.id);
        }

        return r;
    }

    public String getName(int id) {
        for (Colony c : colonies)
            if (c.id == id) return c.getName();

        throw new ArrayIndexOutOfBoundsException("cant find id: " + id);
    }

    public String getAuthor(int id) {
        for (Colony c : colonies)
            if (c.id == id) return c.getAuthor();

        throw new ArrayIndexOutOfBoundsException("cant find id: " + id);
    }

    public String getAffiliation(int id) {
        for (Colony c : colonies)
            if (c.id == id) return c.getAffiliation();

        throw new ArrayIndexOutOfBoundsException("cant find id: " + id);
    }

    public int getColonyID(String col) {
        for (Colony c : colonies) {
            if (c.getName().toLowerCase().equals(col.toLowerCase())) {
                return c.id;
            }
        }
        return -1;
    }

}
