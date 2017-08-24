package alifec.core.simulation;

/**
 * Created by Sergio Del Castillo on 23/08/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class Position {
    public int x;
    public int y;

    public Position() {
        this(0,0);
    }

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
