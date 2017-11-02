package alifec.core.event.impl;

import alifec.core.event.Event;
import alifec.core.simulation.Cell;

/**
 * Created by Sergio Del Castillo on 01/11/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class BattleMovementEvent implements Event {
    private final Cell[][] cell;

    public BattleMovementEvent(Cell[][] microorganism) {
        this.cell = microorganism;
    }

    public Cell[][] getCells() {
        return cell;
    }
}
