package alifec.core.simulation;


public class Petri {
    /**
     * reference to single instance.
     */
    private static Petri INSTANCE = null;

    /*
      * Environment reference.
      */
    private Environment env;

    /**
     * First opponent into the current battle..
     */
   // Colony firstOpponent;
    /**
     * Second opponent into the current battle
     */
    //Colony secondOpponent;
    /**
     * this class contains the nutrients in the battle.
     */
    Agar agar;

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
        if (env.getCell(x, y) == null)
            return -1;  // cant

        return env.getCell(x, y).id;
    }

    /**
     * @param x
     * @param y
     * @return the microorganism's energy in the position <b> x,y</b>. 0.0f if the position is empty.
     */
    public float getEnergy(int x, int y) {
        if (env.getCell(x, y) == null)
            return 0.0f;
        return env.getCell(x, y).ene;
    }

    /**
     * @param x
     * @param y
     * @return the nutrient in the position <b>x, y</b>.
     * @see Agar#getNutrient(int, int)
     */
    public float getNutrient(int x, int y) {
        return agar.getNutrient(x, y);
    }


    /**
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return true if both the microorganism in the position x1,y1 and x2,y2, have different identifiers:
     */
    public boolean canCompete(int x1, int y1, int x2, int y2) {

        return env.getCell(x1, y1) != null &&
                env.getCell(x2, y2) != null &&
                env.getCell(x1, y1) !=
                        env.getCell(x2, y2);

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
     * @return true if the MO in the position (x,y) is not out of the dish.
     * It means that the MO is within the circle with diameter {@link Defs#DIAMETER} and center ({@link Defs#RADIUS}, {@link Defs#RADIUS}).
     */
    public boolean inDish(int x, int y) {
        return env.inDish(x, y);
    }

    /**
     * @return true if the MO in the position (p.x,p.y) is not out of the dish.
     * It means that the MO is within the circle with diameter {@link Defs#DIAMETER} and center ({@link Defs#RADIUS}, {@link Defs#RADIUS}).
     */
    public boolean inDish(Position p) {
        return env.inDish(p.x, p.y);
    }

    /**
     * Return the current nutrient distribution.
     * 1  : Inclined  Plane
     * 2  : Vertical Bar
     * 3  : Rings
     * 4  : Lattice
     * 5  : Two Gaussians
     * 100: Famine
     */
    public int getDistNutri() {
        return env.getAgar().getDistNutri();
    }

    void setEnvironment(Environment e) {
        this.env = e;
    }

    public int getLiveTime() {
        return this.env.getLiveTime();
    }

}
