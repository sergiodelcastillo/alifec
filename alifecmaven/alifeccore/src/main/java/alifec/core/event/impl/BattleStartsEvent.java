package alifec.core.event.impl;

import alifec.core.contest.Battle;
import alifec.core.event.Event;
import alifec.core.simulation.Environment;

/**
 * Created by Sergio Del Castillo on 01/11/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class BattleStartsEvent implements Event {


    private final Battle battle;
    private final Environment environment;

    public BattleStartsEvent(Environment environment, Battle b) {
        this.environment = environment;
        this.battle = b;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public Battle getBattle() {
        return battle;
    }
}
