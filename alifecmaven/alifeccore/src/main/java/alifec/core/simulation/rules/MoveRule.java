package alifec.core.simulation.rules;

import alifec.core.simulation.*;
import alifec.core.simulation.rules.ColonyRule;


public class MoveRule implements ColonyRule {

    public boolean apply(Environment env, Colony c, Colony enemy, Cell mo, Movement mov, boolean mitosis) {
        //TODO: imporove it
        if (mo == null || mov == null ||c == null ||
                !valid(mo.x, mo.y))
            throw new IllegalArgumentException("Illegal Argument");

        if (!mov.isMoved()) return false;

        int newx = mo.x + mov.dx;
        int newy = mo.y + mov.dy;
        mo.ene -= Defs.LESS_MOVE;


        if (mo.isDied()) {
            env.killMO(mo.x, mo.y);
            return true;
        }

        if (!mov.isValid() || !env.inDish(newx, newy))
            return false;

        if (env.getMO(newx, newy) == null) {
            if (!env.moveMO(mo.x, mo.y, newx, newy))
                System.out.println("error en move rule");
        }

        return false;
    }

    boolean valid(int x, int y) {
        return x >= 0 && y >= 0 && x < Defs.DIAMETER && y < Defs.DIAMETER;
    }
}
