/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

package controller.java.rules;

import controller.java.Cell;
import controller.java.Environment;
import controller.java.Movement;
import controller.java.Position;

import java.util.ArrayList;
import java.util.Random;


public class LoveRule implements ColonyRule {
    private static final Random random = new Random();        
    /**
     * The current MO can duplicate if it want.
     *
     * @param env     the environment of the competition
     * @param mo      current MO
     * @param mov     the results of call the method MO.move
     * @param mitosis the results of call the method MO.mitosis
     * @return false
     */
    public StatusRule apply(Environment env, Cell mo, Movement mov, boolean mitosis) {
        if (!mitosis) {
            return StatusRule.NOTHING;
        }

        ArrayList<Position> posRel = new ArrayList<Position>();

        for (int i = mo.x - 1; i <= mo.x + 1; i++) {
            for (int j = mo.y - 1; j <= mo.y + 1; j++) {
                if (env.inDish(i, j) && env.getCell(i, j) == null) {
                    posRel.add(new Position(i, j));
                }
            }
        }

        if (posRel.isEmpty()) {
            return StatusRule.NOTHING;
        }

        mo.ene = (0.99f * mo.ene) / 2.0f;

        // create new MO
        int index = random.nextInt(posRel.size());
        env.createMO(posRel.get(index).x, posRel.get(index).y, mo.ene, mo.id);

        return StatusRule.NOTHING;
    }
}
