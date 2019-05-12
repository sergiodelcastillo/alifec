package alifec.simulation.simulation;

import alifec.core.contest.Battle;
import alifec.core.exception.ValidationException;
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
        switch (decideAction()) {
            case START_SIMULATION:
                showStartSimulation();
                lastState = State.THE_BEGINNING;
                break;
            case START_BATTLE:
                createBattle();
                lastState = State.LIVING;
                break;
            case MOVE_COLONIES:
                live();
                lastState = State.LIVING;
                break;
            case MOVE_COLONIES_ONE_STEP:
                live();
                stop();
                lastState = State.LIVING_ONE_STEP;
                break;
            case END_BATTLE:
                waitBetweenBattles();
                lastState = State.END_BATTLE;
                break;
            case END_SIMULATION:
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

    private void showStartSimulation() {
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

    public void setSimulation(List<Battle> simulation) throws ValidationException {
        if (simulation == null  || simulation.isEmpty())
            throw new ValidationException("The battle list is empty so it is not possible to run a simulation.");

        //clear battles list
        battles.clear();

        //set new state!
        lastState = State.NONE;

        //set the list of battles
        battles.addAll(simulation);
    }

    public void setOneRunSimulation() {
        lastState = State.LIVING_ONE_STEP;
    }

    public void disableOneRunSimulation() {
        lastState = State.LIVING;
    }

    private Action decideAction() {

        switch (lastState) {
            case NONE:
                return Action.START_SIMULATION;
            case THE_BEGINNING:
                return Action.START_BATTLE;
            case LIVING:
                return Action.MOVE_COLONIES;
            case LIVING_ONE_STEP:
                return Action.MOVE_COLONIES_ONE_STEP;
            case END_BATTLE:
                //ver!!
                return Action.START_BATTLE;
            case END_SIMULATION:
                return Action.END_SIMULATION;
        }

        //something wrong .. finish simulation!!
        return Action.END_SIMULATION;
    }
}
