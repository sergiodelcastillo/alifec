/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

package lib;

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
