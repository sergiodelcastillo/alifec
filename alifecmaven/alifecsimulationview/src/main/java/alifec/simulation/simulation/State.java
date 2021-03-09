package alifec.simulation.simulation;


/**
 * Created by Sergio Del Castillo on 09/07/18.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public enum State {
    NONE,
    THE_BEGINNING,
    LIVING,
    LIVING_ONE_STEP,

    END_BATTLE,
    END_SIMULATION
 /*   NONE(o -> Action.START_BATTLE),

    THE_BEGINNING((o, n) -> {
        //necesita now (se puede utilizar el mismo como inicio si notiene otro)
        if (n == 0L){

        }
    }),

    LIVING(o -> {
        //van a utilizar un boolean para decidir siguiene acción
    }),
    LIVING_ONE_STEP(o -> {
        //van a utilizar un boolean para decidir siguiene acción
    }),

    END_BATTLE(o -> {
        // va a utilizar el now, puede almacenar el tiemnpo
    }),
    END_SIMULATION(o -> {
        // va a utilizar el now, puede almacenar el tiemnpo
    });


    private final BiFunction<Object, Long, Action> function;
     long nanoseconds;


    State(BiFunction<Object, Long, Action> function) {
        this.function = function;
        nanoseconds = 0;
    }

    public Action nextAction(Object o) {

        return function.apply(o, nanoseconds);
    }*/
}
