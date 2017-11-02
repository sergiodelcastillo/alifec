package alifec.core.event.impl;

import alifec.core.contest.Battle;
import alifec.core.event.Event;
import alifec.core.simulation.Cell;

/**
 * Created by Sergio Del Castillo on 01/11/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class BattleFinishEvent implements Event {
    private final Battle battle;
    private Cell[][] cells;

    public BattleFinishEvent(Cell[][] cells, Battle battle) {
        this.cells = cells;
        this.battle = battle;
    }

    public Battle getBattle() {
        return battle;
    }

    public Cell[][] getCells() {
        return cells;
    }
}
