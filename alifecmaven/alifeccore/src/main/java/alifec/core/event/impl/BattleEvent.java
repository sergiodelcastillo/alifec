package alifec.core.event.impl;

import alifec.core.contest.Battle;
import alifec.core.event.Event;
import alifec.core.simulation.Environment;

/**
 * Created by Sergio Del Castillo on 19/06/19.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public final class BattleEvent implements Event {
    private final Battle battle;
    private final Environment environment;
    private final Status status;

    public BattleEvent(Environment environment, Battle battle, Status status) {
        this.environment = environment;
        this.battle = battle;
        this.status = status;
    }

    public Battle getBattle() {
        return battle;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public Status getStatus() {
        return status;
    }

    public enum Status {
        START,
        MOVEMENT,
        FINISH
    }
}
