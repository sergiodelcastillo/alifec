/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

package lib;

import java.awt.*;

public class MoveRule extends ColonyRule {

    public boolean apply(Colony c, Colony enemy, Cell mo, Movement mov, boolean mitosis) {

        if (mo == null || mov == null ||
                c == null || mo.pos == null ||
                !valid(mo.pos.x, mo.pos.y))
            throw new IllegalArgumentException("Illegal Argument");

        if (!mov.isMoved()) return false;

        Point newPos = new Point(mo.pos.x + mov.dx, mo.pos.y + mov.dy);
        mo.ene -= Defs.LESS_MOVE;


        if (mo.isDied()) {
            env.killMO(mo.pos.x, mo.pos.y);
            return true;
        }

        if (!mov.isValid() || !env.inDish(newPos.x, newPos.y))
            return false;

        if (env.microorganism[newPos.x][newPos.y] == null) {
            if (!env.moveMO(mo.pos, newPos))
                System.out.println("error en move rule");
        }

        return false;
    }

    boolean valid(int x, int y) {
        return x >= 0 && y >= 0 && x < Defs.DIAMETER && y < Defs.DIAMETER;
    }
}
