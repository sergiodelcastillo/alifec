package alifec.core.simulation.rules;

import alifec.core.simulation.*;

import java.util.Objects;


public class MoveRule implements ColonyRule {


    /**
     * The current mo can move at a empty relative position if the relative position is in dish.
     *
     * @param env     the environment of the competition
     * @param mo      current MO
     * @param mov     the results of call the method MO.move
     * @param mitosis the results of call the method MO.mitosis
     * @return
     */
    public Status apply(Environment env, Cell mo, Movement mov, boolean mitosis) {
        if (!mov.isMoved()) {
            return Status.NONE;
        }

        Position newPos = new Position(mo.x + mov.dx, mo.y + mov.dy);

        if (!env.inDish(newPos.x, newPos.y)) {
            mo.ene -= Defs.LESS_MOVE;

            if (mo.isDied()) {
                env.killMO(mo.x, mo.y);
                return Status.CURRENT_DEAD;
            }

            return Status.NONE;
        }

        Cell cell = env.getCell(newPos.x, newPos.y);

        if (Objects.nonNull(cell) && cell.colonyId != mo.colonyId) {
            // delegate to  attack rule
            return Status.NONE;
        }

        mo.ene -= Defs.LESS_MOVE;

        if (mo.isDied()) {
            env.killMO(mo.x, mo.y);
            return Status.CURRENT_DEAD;
        }

        if (!mov.isValid()) {
            return Status.NONE;
        }

        env.moveMO(mo.x, mo.y, newPos.x, newPos.y);

        return Status.NONE;
    }
}
