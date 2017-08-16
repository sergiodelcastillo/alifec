package alifec.core.simulation; /**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class LoveRule extends ColonyRule {

    public boolean apply(Colony c, Colony enemy, Cell mo, Movement mov, boolean mitosis) {
        if (mo == null || c == null || mo.pos == null || !valid(mo.pos.x, mo.pos.y))
            throw new IllegalArgumentException("Illegal Argument");

        if (!mitosis)
            return false;

        List<Point> posRel = new ArrayList<>();

        for (int i = mo.pos.x - 1; i <= mo.pos.x + 1; i++) {
            for (int j = mo.pos.y - 1; j <= mo.pos.y + 1; j++) {
                if (env.inDish(i, j) &&
                        env.microorganism[i][j] == null) {
                    posRel.add(new Point(i, j));
                }
            }
        }

        if (posRel.size() == 0) return false;

        mo.ene = (0.99f * mo.ene) / 2;

        // crear un Nuevo MO!!
        Point pAleatoria = posRel.get(new Random().nextInt(posRel.size()));
        env.createInstance(pAleatoria, mo.ene, mo.id);
        return false;
    }

    boolean valid(int x, int y) {
        return x >= 0 && y >= 0 && x < Defs.DIAMETER && y < Defs.DIAMETER;
    }
}
