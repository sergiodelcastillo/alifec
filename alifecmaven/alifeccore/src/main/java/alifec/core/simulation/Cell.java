package alifec.core.simulation;


public final class Cell {
    /**
     * ID of colony
     */
    public final int colonyId;

    /**
     * Energy of colony
     */
    public float ene = 0f;
    /**
     * Energy of colony
     */
    public int x;

    public int y;

    public Cell(int colonyId) {
        this.colonyId = colonyId;
    }

    public boolean isDied() {
        return ene <= 0.0f;
    }
}
