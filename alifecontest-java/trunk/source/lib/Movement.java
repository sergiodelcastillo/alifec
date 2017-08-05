package lib;

/**
 * @author: Sergio Del Castillo
 * @email: sergio.jose.delcastillo@gmail.com
 * <p>
 * The Movement class represents the relative movement which can be
 * performed by a Microorganism.
 */

public class Movement {
    public int dx;
    public int dy;

    public Movement() {
    }

    public Movement(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public final boolean isValid() {
        return (dx == -1 || dx == 0 || dx == 1) &&
                (dy == -1 || dy == 0 || dy == 1);
    }

    public final boolean isMoved() {
        return dx != 0 || dy != 0;
    }
}
