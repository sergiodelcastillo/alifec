package alifec.simulation.simulation;

import alifec.core.contest.Battle;
import alifec.core.simulation.Defs;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by Sergio Del Castillo on 09/07/18.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class ALifeContestSimulationTimer extends AnimationTimer {
    private final ALifeContestSimulationView view;
    private final GraphicsContext petriDish;
    private final GraphicsContext colonyInfo;
    private final GraphicsContext energyTrend;
    private final int MO_SIZE = 8;

    private Queue<Battle> battles;
    private State lastState;
    private Battle current;

    public ALifeContestSimulationTimer(ALifeContestSimulationView view) {
        this.view = view;

        petriDish = view.getPetriDishGraphicsContext();
        colonyInfo = view.getColonyInfoGraphicsContext();
        energyTrend = view.getEnergyTrendGraphicsContext();
        battles = new LinkedList<>();
    }

    @Override
    public void handle(long now) {
        switch (getNextAction()) {
            case START:
                showStart();
                lastState = State.BEGINNING;
                break;
            case NEW_BATTLE:
                createBattle();
                //lastState = State.LIVING;
                //break;
            case CONTINUE_SIMULATION:
                live();
                lastState = State.LIVING;
                break;
            case ONE_MOVEMENT_SIMULATION:
                live();
                stop();
                lastState = State.LIVING_ONE_MOVEMENT;
                break;
            case FINISH_BATTLE:
                waitBetweenBattles();
                lastState = State.DEAD_COLONY;
                break;
            case FINISH_SIMULATION:
                showEnd();
        }
    }

    private void createBattle() {
        System.out.println("create battle");

        current = battles.poll();
        System.out.println("new battle: " + current.toString());

    }

    private void waitBetweenBattles() {
        System.out.println("wait between battles");
    }

    private void showEnd() {
        System.out.println("end");
        view.endSimulation();
    }

    private void showStart() {
        System.out.println("start");
    }

    private void live() {
        System.out.println("Perform one movement");
        petriDish.setStroke(Color.GRAY);
        petriDish.setLineWidth(MO_SIZE);
        petriDish.strokeOval(MO_SIZE >> 1,
                MO_SIZE >> 1,
                (Defs.DIAMETER + 1) * MO_SIZE,
                (Defs.DIAMETER + 1) * MO_SIZE);
    }

    public void setSimulation(List<Battle> simulation) {

        //clear battles list
        battles.clear();
        if (simulation.isEmpty()) {
            lastState = State.ENDING;
        } else {
            battles.addAll(simulation);

            //set new state!
            lastState = State.NONE;
        }
    }

    public void setOneRunSimulation() {
        lastState = State.LIVING_ONE_MOVEMENT;
    }

    public void disableOneRunSimulation() {
        lastState = State.LIVING;
    }

    private Action getNextAction() {

        switch (lastState) {
            case NONE:
                return Action.START;
            case BEGINNING:
                return Action.NEW_BATTLE;
            case LIVING:
                return Action.CONTINUE_SIMULATION;
            case LIVING_ONE_MOVEMENT:
                return Action.ONE_MOVEMENT_SIMULATION;
            case DEAD_COLONY:
                //ver!!
                return Action.NEW_BATTLE;
            case ENDING:
                return Action.FINISH_SIMULATION;
        }

        //something wrong .. finish simulation!!
        return Action.FINISH_SIMULATION;
    }
}
