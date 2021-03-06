package alifec.core.simulation;

import alifec.core.contest.Battle;
import alifec.core.event.EventBus;
import alifec.core.event.impl.BattleEvent;
import alifec.core.exception.MoveMicroorganismException;
import alifec.core.exception.OpponentException;
import alifec.core.persistence.SourceCodeFileManager;
import alifec.core.persistence.config.ContestConfig;
import alifec.core.simulation.nutrient.Nutrient;
import alifec.core.simulation.rules.AttackRule;
import alifec.core.simulation.rules.ColonyRule;
import alifec.core.simulation.rules.EatRule;
import alifec.core.simulation.rules.LifeRule;
import alifec.core.simulation.rules.LoveRule;
import alifec.core.simulation.rules.MoveRule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
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
    private Cell[][] microorganism;

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
    private Battle battle;

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
        microorganism = new Cell[Defs.DIAMETER][Defs.DIAMETER];
        SourceCodeFileManager helper = new SourceCodeFileManager(config.getMOsPath());

        logger.info("Loading Colonies");
        logger.info("Loading Java Colonies");

        try {
            for (String name : helper.listJavaMOs()) {
                try {
                    //todo: it seems to be unnecessary: JavaColony.addClassPath(config.getCompilationTarget());
                    addJavaColony(config, name);
                    logger.info(name + " [OK]");
                } catch (ClassNotFoundException ex) {
                    logger.warn(ex.getMessage(), ex);
                }
            }
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
        }

        // loading library
        logger.info("Loading C++ Colonies ");
        try {
            if (CppColony.loadLibrary(config.getCompilationTarget())) {
                logger.info("Loading C++ Library [OK]");

                for (String name : helper.listCppMOs()) {
                    try {
                        // to initialise the name of colony
                        addCppColony(name);
                        logger.info(name + " [OK]");
                    } catch (ClassNotFoundException ex) {
                        logger.warn(name + " [FAIL]");
                    }
                }
            } else {
                logger.warn("Loading C++ Library [FAIL]");
            }
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
        }
        // set the environment !!
        Petri.getInstance().setEnvironment(this);
    }

    private void addCppColony(String name) throws ClassNotFoundException {
        addColony(new CppColony(colonies.size(), name));
    }

    private void addJavaColony(ContestConfig config, String name) throws ClassNotFoundException, MalformedURLException {
        addColony(new JavaColony(colonies.size(), config.getCompilationTarget(), "MOs." + name));
    }

    private void addColony(Colony colony){
        colonies.add(colony);
    }

   /* TODO: it seems to be unnecessary
       public boolean delete(String name) {
        for (Colony c : colonies) {
            if (name.equals(c.getName())) {
                return colonies.remove(c);
            }
        }
        return false;
    }*/

    /*
     * Initialize the two colonies and the agar to fight.
     *
     * @param b is a battle to run
     * @return if was successful
     */
    public boolean createBattle(Battle b) {
        battle = b;

        int nutrientId = b.getNutrientId();
        Nutrient temp = ContestConfig.nutrientOptions().get(nutrientId);

        if (Objects.isNull(temp)) {
            logger.error("There is not nutrient distribution with id = " + nutrientId + ".");
            return false;
        }

        agar.setNutrient(temp);

        if (Objects.isNull(c1 = getColonyById(b.getFirstColonyId()))) {
            return false;
        }

        if (Objects.isNull(c2 = getColonyById(b.getSecondColonyId()))) {
            return false;
        }


        for (int i = 0; i < microorganism.length; i++) {
            for (int j = 0; j < microorganism[i].length; j++) {
                microorganism[i][j] = null;
            }
        }
        init(c1);
        init(c2);

        liveTime = 0;

        EventBus.post(new BattleEvent(this, b, BattleEvent.Status.START));
        return true;

    }

    private void init(Colony c) {
        Random r = new Random();

        c.clear();

        for (int index = 0; index < Defs.MO_INITIAL; ) {
            if (createMOInstance(r.nextInt(Defs.DIAMETER), r.nextInt(Defs.DIAMETER), Defs.E_INITIAL, c.id))
                ++index;
        }
    }

    public boolean moveColonies() throws MoveMicroorganismException {
        List<Cell> allOps = getMOs();

        // randomize list!
        Collections.shuffle(allOps);

        boolean mitosis;
        Movement mov;

        for (Cell mo : allOps) {
            Colony current = (mo.colonyId == c2.id) ? c2 : c1;

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
                String txt = "The colony: " + current.getName() + " has been penalized.";
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

    public List<Cell> getMOs() {
        List<Cell> allOps = new ArrayList<>();

        allOps.addAll(c1.getMOs());
        allOps.addAll(c2.getMOs());
        return allOps;
    }

    /**
     * Update the status of the current battle.
     *
     * @return true if the battles was finished, false otherwise.
     */
    private boolean updateStatus() {

        if (c1.isDied()) {
            battle.setWinner(battle.getSecondColonyId(), c2.getEnergy());
            EventBus.post(new BattleEvent(this, battle, BattleEvent.Status.FINISH));
            return true;
        }

        if (c2.isDied()) {
            battle.setWinner(battle.getFirstColonyId(), c1.getEnergy());
            EventBus.post(new BattleEvent(this, battle, BattleEvent.Status.FINISH));
            return true;
        }

        EventBus.post(new BattleEvent(this, battle, BattleEvent.Status.MOVEMENT));
        return false;
    }


    public boolean inDish(int x, int y) {
        return ((x - Defs.RADIUS) * (x - Defs.RADIUS) + (y - Defs.RADIUS) * (y - Defs.RADIUS)) <
                (Defs.RADIUS * Defs.RADIUS);
    }

    public boolean moveMO(int ax, int ay, int bx, int by) {
        if (Objects.isNull(microorganism[ax][ay]))
            return false;

        if (Objects.nonNull(microorganism[bx][by]))
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
        return Objects.nonNull(mo) && ((mo.colonyId == c1.id) ? c1.kill(mo) : c2.kill(mo));
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

    public Battle getResults() {
        return battle;
    }

    public List<String> getOpponentNames() {
        List<String> tmp = new ArrayList<>();

        for (Colony c : colonies)
            tmp.add(c.getName());

        return tmp;
    }


    public List<Competitor> getCompetitors() {
        List<Competitor> list = new ArrayList<>();

        for (Colony c : colonies) {
            try {
                list.add(new Competitor(c.getId(), c.getName(), c.getAuthor(), c.getAffiliation()));
            } catch (OpponentException e) {
                logger.error(e.getMessage(), e);
            }
        }

        return list;
    }

    public String getName(int id) {
        for (Colony c : colonies)
            if (c.id == id) return c.getName();

        throw new ArrayIndexOutOfBoundsException("cant find id: " + id);
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
     * @param px
     * @param py
     * @param ene
     * @param id
     * @return the if can create the mo.
     */
    public boolean createMOInstance(int px, int py, float ene, int id) {
        if (!inDish(px, py) ||
                ene <= 0 || ene > Defs.E_INITIAL ||
                Objects.nonNull(microorganism[px][py]))
            return false;

        Cell mo = new Cell(id);
        mo.x = px;
        mo.y = py;
        mo.ene = ene;

        microorganism[px][py] = mo;

        return id == c1.id ? c1.createInstance(mo) : c2.createInstance(mo);
    }

    public int getLiveTime() {
        return liveTime;
    }

    public Nutrient getNutrient() {
        return agar.getCurrent();
    }
}
