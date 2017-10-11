/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */
package controller.java;

import data.java.Defs;

public class Petri {
    /**
     * reference to single instance.
     */
    private static Petri INSTANCE = null;

    /*
      * Environment reference.
      */
    private Environment env;

    private int battleId;

    private int nutrientId;
    private int liveTime;

    /**
     * The Petri Class use the Singleton pattern.
     */
    private Petri() {
    }

    /**
     * Create a static instance. This instance is unique.
     */
    private static synchronized void createInstance() {
        if (INSTANCE == null)
            INSTANCE = new Petri();
    }

    public static Petri getInstance() {
        if (INSTANCE == null)
            createInstance();

        return INSTANCE;
    }

    /**
     * @param x
     * @param y
     * @return return the  Colony identifier in the position x,y. return -1 if the position is empty.
     */
    public int getOpponent(int x, int y) {
        if (!ensureIndex(x, y)) {
            return -1;
        }
        if (env.microorganism[x][y] == null) {
            return -1;  // cant
        }

        return env.microorganism[x][y].id;
    }

    public int getOpponent(Position p) {
        return getOpponent(p.x, p.y);
    }

    /**
     * @param x
     * @param y
     * @return the microorganism's energy in the position <b> x,y</b>. 0.0f if the position is empty.
     */
    public float getEnergy(int x, int y) {
        if (!ensureIndex(x, y)) {
            return 0.0f;
        }
        if (env.microorganism[x][y] == null) {
            return 0.0f;
        }

        return env.microorganism[x][y].ene;
    }

    public float getEnergy(Position p) {
        return getEnergy(p.x, p.y);
    }

    /**
     * @param x
     * @param y
     * @return the nutrient in the position <b>x, y</b>.
     * @see Agar#getNutrient(int, int)
     */
    public float getNutrient(int x, int y) {
        if (!ensureIndex(x, y)) {
            return 0.0f;
        }
        return env.getAgar().getNutrient(x, y);
    }

    public float getNutrient(Position p) {
        return getNutrient(p.x, p.y);
    }

    /**
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return true if both the microorganism in the position x1,y1 and x2,y2, have different identifiers:
     */
    public boolean canCompete(int x1, int y1, int x2, int y2) {
        if (!ensureIndex(x1, y1) || !ensureIndex(x2, y2)) {
            return false;
        }

        return env.microorganism[x1][y1] != null &&
                env.microorganism[x2][y2] != null &&
                env.microorganism[x1][y1].id != env.microorganism[x2][y2].id;

    }

    /**
     * @param a
     * @param b
     * @return true if both the microorganism in the position <b>a</b> and <b>b</b>, have different identifiers:
     */
    public boolean canCompete(Position a, Position b) {
        return canCompete(a.x, a.y, b.x, b.y);
    }

    /**
     * @return true if the position (x,y) is within the dish:
     */
    public boolean inDish(int x, int y) {
        return env.inDish(x, y);
    }

    /**
     * @see Petri#inDish(int, int)
     */
    public boolean inDish(Position p) {
        return env.inDish(p.x, p.y);
    }

    /**
     * @return information of the current nutrient distribution.
     *         <p/>
     *         1  : Inclined  Plane
     *         2  : Vertical Bar
     *         3  : Rings
     *         4  : Lattice
     *         5  : Two Gaussians
     *         100: Famine
     */
    public int getDistNutri() {
        return this.env.getAgar().getNutrientId();
    }

    public int getBattleId() {
        return  this.env.getCurrentBattle().getId();
    }

    public int getLiveTime() {
        return this.env.getLiveTime();
    }

    void setEnvironment(Environment e) {
        this.env = e;
    }

    private boolean ensureIndex(int x, int y) {
        return x >= 0 && x < Defs.DIAMETER && y >= 0 && y < Defs.DIAMETER;
    }

    /*void update() {
        this.nutrientId = env.getAgar().getNutrientId();
        this.battleId = env.getCurrentBattle().getId();
        this.liveTime = env.getLiveTime();
    }*/
}
