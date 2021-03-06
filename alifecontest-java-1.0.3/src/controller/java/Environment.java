/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

package controller.java;

import controller.java.battle.BattleRun;
import controller.java.rules.*;
import data.java.AllFilter;
import data.java.Config;
import data.java.Defs;
import data.java.Log;
import exceptions.CreateEnvironmentException;
import exceptions.LoadNutrientsException;
import exceptions.MoveMicroorganismException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Environment {
    enum ColonyType {
        JAVA_COLONY,
        CPP_COLONY;

    }


    /**
     * Agar of environment.
     */
    private Agar agar;
    /**
     * AllData colonies!!
     */
    private ArrayList<Colony> colonies;

    /**
     * Reference to position of MOs.
     */
    protected Cell[][] microorganism = new Cell[Defs.DIAMETER][Defs.DIAMETER];
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
    private BattleRun currentBattle;

    private ColonyRule[] rules = {
            new EatRule(),
            new LifeRule(),
            new MoveRule(),
            new AttackRule(),
            new LoveRule()
    };

    public Environment() throws LoadNutrientsException, CreateEnvironmentException {
        try {
            agar = new Agar();
            colonies = new ArrayList<Colony>();

            String[] javaColonies = AllFilter.listJavaColonies();
            String[] cppColonies = AllFilter.listCppColonies();

            if (javaColonies.length > 0) {
                Log.println("Loading Colonies\nLoading Java Colonies");

                for (String name : javaColonies) {
                    createColony(colonies.size(), Config.getInstance().getAbsoluteMOsPackage(name), ColonyType.JAVA_COLONY);
                }
            }

            if (cppColonies.length > 0) {
                // loading cpp library
                Log.println("\nLoading C++ Colonies ");

                if (CppColony.loadLibrary(Config.getInstance().getAbsoluteBinLibCppFolder())) {
                    Log.println("Loading C++ Library [OK]");

                    for (String name : cppColonies) {
                        createColony(colonies.size(), name, ColonyType.CPP_COLONY);
                    }
                } else {
                    Log.println("Loading C++ Library [FAIL]");
                }
            }

            // eat the environment !!
            Petri.getInstance().setEnvironment(this);
        } catch (Exception ex) {
            throw new CreateEnvironmentException("Cant create the environment", ex);
        }
    }

    public void end() {
        for (Colony c : colonies) {
            c.end();
        }
    }

    private void createColony(int id, String name, ColonyType type) {
        try {
            Colony colony;

            switch (type) {
                case JAVA_COLONY:
                    colony = new JavaColony(id, name);
                    break;
                case CPP_COLONY:
                    colony = new CppColony(id, name);
                    break;
                default:
                    return;
            }

            if (colonies.contains(colony)) {
                colony.end(); //delete references.
                Log.println(colony + " [FAIL] Duplicated Colony");
                return;
            }

            colonies.add(colony);
            Log.println(name + " [OK]");

        } catch (ClassNotFoundException ex) {
            Log.println(name + " [FAIL]");
            Log.save("Fail load colony: " + name, ex);
        }
    }

    /**
     * Remove a colony  of the list of colonies.
     *
     * @param colonyName is the name of the colony
     * @return true if the colony was removed.
     */
    public boolean delete(String colonyName) {
        for (Colony c : colonies) {
            if (colonyName.equals(c.getName())) {
                return colonies.remove(c);
            }
        }
        return false;
    }

    /**
     * .
     * Initialize the two colonies and the agar to fight.
     *
     * @param b is a battle to run
     * @return if was successful
     */
    public boolean createBattle(BattleRun b) {
        currentBattle = b;

        if (!agar.setNutrient(b.getNutrientId())) {
            return false;
        }

        if ((c1 = getColony(b.getFistId())) == null) {
            return false;
        }

        if ((c2 = getColony(b.getSecondId())) == null) {
            return false;
        }

        microorganism = new Cell[Defs.DIAMETER][Defs.DIAMETER];
        positionMOs(c1);
        positionMOs(c2);
        liveTime = 0;
        return true;
    }

    /**
     * get the colony  with the id = id
     *
     * @param id the colony identifier
     * @return a colony f exists. null in another case.
     */
    Colony getColony(int id) {
        for (Colony c : colonies) {
            if (c.getId() == id) {
                return c;
            }
        }
        return null;
    }


    /**
     * .
     * Position all Colony MOs
     *
     * @param c a colony
     */
    private void positionMOs(Colony c) {
        Random r = new Random();

        c.clear();

        for (int index = 0; index < Defs.MO_INITIAL;) {
            if (createMO(r.nextInt(Defs.DIAMETER), r.nextInt(Defs.DIAMETER), Defs.E_INITIAL, c.id)) {
                ++index;
            }
        }
    }

    public boolean moveColonies() throws MoveMicroorganismException {
        ArrayList<Cell> mos = new ArrayList<Cell>();

        initShuffle(mos);

        for (Cell mo : mos) {
            Colony current = (mo.id == c2.id) ? c2 : c1;

            if (mo.isDied()) {
                continue;
            }

            try {
                int indexMO = current.getMOs().indexOf(mo);

                current.update(indexMO, mo.ene, mo.x, mo.y);

                Movement mov = current.move(indexMO);
                boolean mitosis = current.mitosis(indexMO);

                for (ColonyRule rule : rules) {
                    if (StatusRule.MO_DEAD == rule.apply(this, mo, mov, mitosis)) {
                        break;
                    }
                }
            } catch (Exception ex) {
                String txt = "The colony: " + current.getName() + " has been Penalized.";
                throw new MoveMicroorganismException(txt, current.getName(), current.getId(), ex);
            }
        }

        agar.moveRandom();
        ++liveTime;

        return updateStatus();
    }

    private void initShuffle(ArrayList<Cell> mos) {
        mos.addAll(c1.getMOs());
        mos.addAll(c2.getMOs());

        Collections.shuffle(mos);
    }

    /**
     * Update the status of the current battle.
     *
     * @return true if the battle was finished, false otherwise.
     */

    private boolean updateStatus() {
        if (c1.isDied()) {
            currentBattle.setWinner(currentBattle.getSecondId(), c2.getEnergy());
            return true;
        }

        if (c2.isDied()) {
            currentBattle.setWinner(currentBattle.getFistId(), c1.getEnergy());
            return true;
        }

        return false;
    }

    public boolean inDish(int x, int y) {
        return ((x - Defs.RADIUS) * (x - Defs.RADIUS) + (y - Defs.RADIUS) * (y - Defs.RADIUS)) < (Defs.RADIUS * Defs.RADIUS);
    }

    /**
     * Move a MO of the position (ax,ay) to (bx,by)
     *
     * @param ax position-x of the first MO
     * @param ay position-y of the first MO
     * @param bx the new position-x of the MO
     * @param by the new position-y of the MO
     * @return the if can move
     */
    public boolean moveMO(int ax, int ay, int bx, int by) {
        if (microorganism[ax][ay] == null) {
            return false;
        }

        if (microorganism[bx][by] != null) {
            return false;
        }

        microorganism[bx][by] = microorganism[ax][ay];
        microorganism[ax][ay] = null;
        microorganism[bx][by].x = bx;
        microorganism[bx][by].y = by;

        return true;
    }

    /**
     * Kill a MO in the position (x,y)
     *
     * @param x the position x of the MO
     * @param y the position y of the MO
     * @return true if can kill
     */
    public boolean killMO(int x, int y) {
        if (microorganism[x][y] == null) return false;

        Cell mo = microorganism[x][y];

        microorganism[x][y] = null;

        return ((mo.id == c1.id) ? c1.kill(mo) : c2.kill(mo));
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

    public boolean addIds(BattleRun battleRun) {
        Colony f = find(battleRun.getFirstName());
        Colony s = find(battleRun.getSecondName());

        if (f == null || s == null) {
            return false;
        }
        battleRun.setFirstId(f.getId());
        battleRun.setSecondId(s.getId());
        return true;
    }

    private Colony find(String name) {
        for (Colony colony : colonies) {
            if (colony.getName().equals(name)) {
                return colony;
            }
        }
        return null;
    }

    /*
      ***************************************************************************
      * Get methods
      * *************************************************************************
      */

    public Agar getAgar() {
        return agar;
    }

    public Colony getFirstColony() {
        return c1;
    }

    public Colony getSecondColony() {
        return c2;
    }

    public BattleRun getCurrentBattle() {
        return currentBattle;
    }

    public ArrayList<String> getNames() {
        ArrayList<String> names = new ArrayList<String>();

        for (Colony c : colonies) {
            names.add(c.getName());
        }

        return names;
    }

    public Cell getCell(int x, int y) {
        return microorganism[x][y];
    }

    public ArrayList<Colony> getColonies() {
        return this.colonies;
    }

    public int getLiveTime() {
        return liveTime;
    }
}
