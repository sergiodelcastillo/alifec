package alifec.core.simulation.rules; /**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

import alifec.core.simulation.Cell;
import alifec.core.simulation.Colony;
import alifec.core.simulation.Defs;
import alifec.core.simulation.Environment;
import alifec.core.simulation.Movement;
import alifec.core.simulation.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class LoveRule implements ColonyRule {

    public boolean apply(Environment env, Colony c, Colony enemy, Cell mo, Movement mov, boolean mitosis) {
        //TODO: imporove it
        if (mo == null || c == null || mo.pos == null || !valid(mo.pos.x, mo.pos.y))
            throw new IllegalArgumentException("Illegal Argument");

        if (!mitosis)
            return false;

        List<Position> posRel = new ArrayList<>();

        for (int i = mo.pos.x - 1; i <= mo.pos.x + 1; i++) {
            for (int j = mo.pos.y - 1; j <= mo.pos.y + 1; j++) {
                if (env.inDish(i, j) &&
                        env.getMO(i, j) == null) {
                    posRel.add(new Position(i, j));
                }
            }
        }

        if (posRel.size() == 0) return false;

        mo.ene = (0.99f * mo.ene) / 2;

        // crear un Nuevo MO!!
        Position pAleatoria = posRel.get(new Random().nextInt(posRel.size()));
        env.createInstance(pAleatoria, mo.ene, mo.id);
        return false;
    }

    boolean valid(int x, int y) {
        return x >= 0 && y >= 0 && x < Defs.DIAMETER && y < Defs.DIAMETER;
    }
}
