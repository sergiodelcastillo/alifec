package alifec.core.event.impl;

import alifec.core.contest.Battle;
import alifec.core.event.Event;
import alifec.core.simulation.Cell;

/**
 * Created by Sergio Del Castillo on 01/11/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class BattleStartsEvent implements Event {

    private final Cell[][] cells;
    private final Battle battle;

    public BattleStartsEvent(Cell[][] cells, Battle b){
        this.cells = cells;
        this.battle = b;
    }

    public Cell[][] getCells(){
        return cells;
    }

    public Battle getBattle() {
        return battle;
    }
}
