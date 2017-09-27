package alifec.core.simulation;


/**
 * @author Sergio Del Castillo
 * @email sergio.jose.delcastillo@gmail.com
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
    public int x;

    public int y;

    Cell(int id) {
        this.id = id;
    }

    public boolean isDied() {
        return ene <= 0.0f;
    }
}
