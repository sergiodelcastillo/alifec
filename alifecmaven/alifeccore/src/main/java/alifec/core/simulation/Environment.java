package alifec.core.simulation;

import alifec.core.contest.tournament.battles.BattleRun;
import alifec.core.exception.MoveMicroorganismException;
import alifec.core.persistence.ContestConfig;
import alifec.core.persistence.filter.SourceCodeFilter;
import alifec.core.simulation.rules.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

/**
 * @author Sergio Del Castillo
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class Environment {
    Logger logger = LogManager.getLogger(getClass());

    /**
     * Agar of environment.
     */
    private Agar agar;

    /**
     * All colonies!!
     */
    private List<Colony> colonies;

    /**
     * Reference to position of MOs.
     */
    private Cell microorganism[][] = new Cell[Defs.DIAMETER][Defs.DIAMETER];

    /**
     * First Opponent .. temporal reference !!
     */
    private Colony c1;

    /**
     * Second Opponent .. temporal reference !!
     */
    private Colony c2;

    /**
     * Information of current battle .. temporal reference !!
     */
    private BattleRun opponents;

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
        for (String name : SourceCodeFilter.listJavaMOs(config.getMOsPath())) {
            try {
                JavaColony.addClassPath(config.getCompilationTarget());
                colonies.add(new JavaColony(colonies.size(), "MOs." + name));
                logger.info(name + " [OK]");
            } catch (Exception ex) {
                logger.warn(name + " [FAIL]");
                logger.warn(ex.getMessage(), ex);
            }
        }

        // loading library
        logger.info("Loading C++ Colonies ");

        if (CppColony.loadLibrary(config.getCompilationTarget())) {
            logger.info("Loading C++ Library [OK]");

            for (String name : SourceCodeFilter.listCppMOs(config.getMOsPath())) {
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
        opponents = b;

        agar.setNutrient(b.nutrientID);

        for (Colony colony : colonies) {
            if (colony.id == b.ID1)
                c1 = colony;
        }

        for (Colony colony : colonies) {
            if (colony.id == b.ID2)
                c2 = colony;
        }


        Petri.getInstance().agar = agar;
        Petri.getInstance().firstOpponent = c1;
        Petri.getInstance().secondOpponent = c2;

        microorganism = new Cell[Defs.DIAMETER][Defs.DIAMETER];
        init(c1);
        init(c2);

    }

    public final void init(Colony c) {
        Random r = new Random();

        c.clear();

        for (int index = 0; index < Defs.MO_INITIAL; ) {
            if (createInstance(r.nextInt(Defs.DIAMETER), r.nextInt(Defs.DIAMETER), Defs.E_INITIAL, c.id))
                ++index;
        }
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
                current.update(indexMO, mo.ene, mo.x, mo.y);
                mov = current.move(indexMO);
                mitosis = current.mitosis(indexMO);
            } catch (Exception ex) {
                logger.warn(ex.getMessage(), ex);
                String txt = "The colony: " + current.getName() + " has been Penalized.";
                throw new MoveMicroorganismException(txt, current.getName(), ex);
            }

            for (ColonyRule rule : rules) {
                if (rule.apply(this, current, enemy, mo, mov, mitosis)) {
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
            opponents.winnerID = opponents.ID2;
            opponents.winner_energy = c2.getEnergy();
            return true;
        }

        if (c2.isDied()) {
            opponents.winnerID = opponents.ID1;
            opponents.winner_energy = c1.getEnergy();
            return true;
        }

        return false;
    }


    public boolean inDish(int x, int y) {
        return ((x - Defs.RADIUS) * (x - Defs.RADIUS) + (y - Defs.RADIUS) * (y - Defs.RADIUS)) <
                (Defs.RADIUS * Defs.RADIUS);
    }

    public boolean moveMO(int ax, int ay, int bx, int by) {
        if (microorganism[ax][ay] == null)
            return false;

        if (microorganism[bx][by] != null)
            return false;

        microorganism[bx][by] = microorganism[ax][ay];
        microorganism[ax][ay] = null;
        microorganism[bx][by].x = bx;
        microorganism[bx][by].y = by;
        return true;
    }

    public boolean killMO(int x, int y) {
        if (!inDish(x, y)) {
            return false;
        }

        Cell mo = microorganism[x][y];

        microorganism[x][y] = null;
        return mo != null && ((mo.id == c1.id) ? c1.kill(mo) : c2.kill(mo));

    }

    public boolean createInstance(int px, int py, float ene, int id) {
        if (!inDish(px, py) ||
                ene <= 0 || ene > Defs.E_INITIAL ||
                microorganism[px][py] != null)
            return false;

        Cell mo = new Cell(id);
        mo.x = px;
        mo.y = py;
        mo.ene = ene;

        microorganism[px][py] = mo;

        return id == c1.id ? c1.createInstance(mo) : c2.createInstance(mo);
    }

    public Agar getAgar() {
        return agar;
    }

    public Colony getFirstOpponent() {
        return c1;
    }

    public Colony getSecondOpponent() {
        return c2;
    }

    public BattleRun getResults() {
        return opponents;
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

    public Cell getMO(int x, int y) {
        return microorganism[x][y];
    }

    public float eat(int x, int y) {
        return agar.eat(x, y);
    }
}
