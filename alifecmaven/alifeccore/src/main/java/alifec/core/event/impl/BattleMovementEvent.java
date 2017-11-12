package alifec.core.event.impl;

import alifec.core.event.Event;
import alifec.core.simulation.Environment;

/**
 * Created by Sergio Del Castillo on 01/11/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class BattleMovementEvent implements Event {

    private final Environment environment;

    public BattleMovementEvent(Environment environment) {
        this.environment = environment;
    }

    public Environment getEnvironment() {
        return environment;
    }
}
