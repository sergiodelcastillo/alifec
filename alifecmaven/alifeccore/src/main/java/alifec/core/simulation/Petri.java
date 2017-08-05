package alifec.core.simulation; /**
 * @author: Sergio Del Castillo
 * @email: sergio.jose.delcastillo@gmail.com
 */

import java.awt.*;

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
     * First oponent into the current battle..
     */
    Colony firstOponent;
    /**
     * Second oponent into the current battle
     */
    Colony secondOponent;
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
     * @return  return the  Colony identifier in the position x,y. return -1 if the position is empty.
     */
    public int getOpponent(int x, int y) {
        if (env.microorganism[x][y] == null)
            return -1;  // cant

        return env.microorganism[x][y].id;
    }

    /**
     * @param x
     * @param y
     * @return the microorganism's energy in the position <b> x,y</b>. 0.0f if the position is empty.
     */
    public float getEnergy(int x, int y) {
        if (env.microorganism[x][y] == null)
            return 0.0f;
        return env.microorganism[x][y].ene;
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
    public boolean canCompite(int x1, int y1, int x2, int y2) {

        return env.microorganism[x1][y1] != null &&
                env.microorganism[x2][y2] != null &&
                env.microorganism[x1][y1] !=
                        env.microorganism[x2][y2];

    }

    /**
     * @param a
     * @param b
     * @return true if both the microorganism in the position <b>a</b> and <b>b</b>, have different identifiers:
     */
    public boolean canCompite(Point a, Point b) {
        return canCompite(a.x, a.y, b.x, b.y);
    }

    /**
     * @return si el MO en la posicion (x,y) esta dentro del entorno,
     *         es decir,  pertenece al circulo de entro (Defs.Radious, Defs.Radios ) y
     *         radio Defs.radious.
     */
    public boolean inDish(int x, int y) {
        return env.inDish(x, y);
    }

    /**
     * @return si el MO en la posicion p esta dentro del entorno,
     *         es decir,  pertenece al circulo de entro (Defs.Radious, Defs.Radios ) y
     *         radio Defs.radious.
     */
    public boolean inDish(Point p) {
        return env.inDish(p.x, p.y);
    }

    /**
     * Retorna informacion acerca de la distribucion de nutrientes actual.
     * 1  : plano inclinado (Inclined  Plane)
     * 2  : barra vertical (Vertical Bar)
     * 3  : anillo (Rings)
     * 4  : grilla (Lattice)
     * 5  : dos gausianas (Two Gaussians)
     * 100: distrubucion uniforme (Famine)
     */
    public int getDistNutri() {
        return env.getAgar().getDistNutri();
    }

    void setEnvironment(Environment e) {
        this.env = e;
    }
}
