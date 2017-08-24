package alifec.core.simulation; /**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */


public final class Cell {
    /**
     * ID of colony
     */
    public final int id;

    /**
     * Energy of colony
     */
    public float ene = 0f;
    /**
     * Energy of colony
     */
    public Position pos = new Position();

    Cell(int id) {
        this.id = id;
    }

    public boolean isDied() {
        return ene <= 0.0f;
    }
}
