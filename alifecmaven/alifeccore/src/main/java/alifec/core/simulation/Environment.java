package alifec.core.simulation;

import alifec.core.contest.BattleRun;
import alifec.core.exception.MoveMicroorganismException;
import alifec.core.exception.NutrientException;
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
     * Simulation time
     */
    private int liveTime;

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

    /*
     * Initialize the two colonies and the agar to fight.
     *
     * @param b is a battle to run
     * @return if was successful
     */
    public boolean createBattle(BattleRun b) {
        opponents = b;

        try {
            agar.setNutrient(b.nutrientID);
        } catch (NutrientException e) {
            logger.error(e.getMessage(), e);
            return false;
        }


        if ((c1 = getColonyById(b.ID1)) == null) {
            return false;
        }

        if ((c2 = getColonyById(b.ID2)) == null) {
            return false;
        }

        microorganism = new Cell[Defs.DIAMETER][Defs.DIAMETER];
        init(c1);
        init(c2);

        liveTime = 0;
        return true;

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

        allOps.addAll(c1.getMOs());
        allOps.addAll(c2.getMOs());

        // randomize list!
        Collections.shuffle(allOps);

        boolean mitosis;
        Movement mov;

        for (Cell mo : allOps) {
            Colony current = (mo.id == c2.id) ? c2 : c1;

            if (mo.isDied()) {
                continue;
            }

            int indexMO = current.getMOs().indexOf(mo);

            try {
                current.update(indexMO, mo.ene, mo.x, mo.y);
                mov = current.move(indexMO);
                mitosis = current.mitosis(indexMO);
            } catch (Exception ex) {
                //todo: check the logs.
                logger.error(ex.getMessage(), ex);
                String txt = "The colony: " + current.getName() + " has been Penalized.";
                throw new MoveMicroorganismException(txt, current.getName(), ex);
            }

            for (ColonyRule rule : rules) {
                if (ColonyRule.Status.CURRENT_DEAD == rule.apply(this, mo, mov, mitosis)) {
                    break;      // break of rules
                }
            }
        }

        agar.moveRandom();
        ++liveTime;

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

    public int getColonyIdByName(String col) {
        for (Colony c : colonies) {
            if (c.getName().toLowerCase().equals(col.toLowerCase())) {
                return c.id;
            }
        }
        return -1;
    }

    /**
     * get the colony  with the id = id
     *
     * @param id the colony identifier
     * @return a colony f exists. null in another case.
     */
    public Colony getColonyById(int id) {
        for (Colony c : colonies) {
            if (c.getId() == id) {
                return c;
            }
        }
        return null;
    }

    public Cell getCell(int x, int y) {
        return microorganism[x][y];
    }

    /**
     * create a instance of MO in the position (x,y) with the energy = ene and id = id
     *
     * @param x
     * @param y
     * @param ene
     * @param id
     * @return the if can create the mo.
     */
    public boolean createMO(int x, int y, float ene, int id) {
        if (!inDish(x, y) || ene <= 0 || microorganism[x][y] != null) {
            return false;
        }

        Cell mo = new Cell(id);
        mo.x = x;
        mo.y = y;
        mo.ene = ene;

        microorganism[x][y] = mo;

        return id == c1.id ? c1.createInstance(mo) : c2.createInstance(mo);
    }

    public int getLiveTime() {
        return liveTime;
    }
}
