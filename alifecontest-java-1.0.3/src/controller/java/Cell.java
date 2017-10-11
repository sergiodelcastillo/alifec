/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

package controller.java;

import data.java.Defs;


public final class Cell implements Comparable<Cell> {
    /**
     * Colony id
     */
    public final int id;

    /**
     * The MO energy
     */
    public float ene;
    /**
     * The MO position
     */
    public int x, y;


    public Cell(int id) {
        this.id = id;
    }

    public boolean isDied() {
        return ene <= 0.0f;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Cell))
            return false;
        Cell c = (Cell) obj;

        return x == c.x && y == c.y;
    }


    @Override
    public int compareTo(Cell o) {
        return x * Defs.DIAMETER + y - o.x * Defs.DIAMETER + o.y;
    }

}
