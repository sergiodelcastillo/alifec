package alifec.core.event.impl;

import alifec.core.contest.Battle;
import alifec.core.event.Event;
import alifec.core.simulation.Environment;

/**
 * Created by Sergio Del Castillo on 01/11/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class BattleFinishEvent implements Event {
    private final Battle battle;
    private final Environment environment;

    public BattleFinishEvent(Environment environment, Battle battle) {
        this.environment = environment;
        this.battle = battle;
    }

    public Battle getBattle() {
        return battle;
    }

    public Environment getEnvironment() {
        return environment;
    }
}
