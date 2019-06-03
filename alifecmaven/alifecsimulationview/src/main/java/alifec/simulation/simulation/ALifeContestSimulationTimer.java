package alifec.simulation.simulation;

import alifec.core.contest.Battle;
import alifec.core.exception.ValidationException;
import alifec.core.simulation.Defs;
import alifec.core.simulation.Petri;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by Sergio Del Castillo on 09/07/18.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class ALifeContestSimulationTimer extends AnimationTimer {
    private static Color COLONY_A_COLOR = Color.RED;
    private static Color COLONY_B_COLOR = Color.BLUE;
    private static Color NUTRIENT_COLOR = Color.YELLOW;
    private final ALifeContestSimulationView view;
    private final GraphicsContext petriDish;
    private final GraphicsContext colonyInfo;
    private final GraphicsContext energyTrend;
    private final int MO_SIZE = 8;
    private final Paint defaultColor;

    private Queue<Battle> battles;
    private State lastState;
    private Battle current;

    public ALifeContestSimulationTimer(ALifeContestSimulationView view) {
        this.view = view;

        petriDish = view.getPetriDishGraphicsContext();
        colonyInfo = view.getColonyInfoGraphicsContext();
        energyTrend = view.getEnergyTrendGraphicsContext();
        battles = new LinkedList<>();

        defaultColor = petriDish.getFill();
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
                live();
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

        //todo: ver porque da error
        if (current != null)
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
        //todo: hacer movimiento.

        //Clear the whole circle
        petriDish.setFill(Color.valueOf(ALifeContestSimulationView.getColorBackground()));
        petriDish.fillOval(0, 0, MO_SIZE * (2 + Defs.DIAMETER), MO_SIZE * (2 + Defs.DIAMETER));


        //draw the petri dish
        petriDish.setStroke(Color.GRAY);
        petriDish.setLineWidth(MO_SIZE);

        petriDish.strokeOval(MO_SIZE, MO_SIZE, (Defs.DIAMETER + 1) * MO_SIZE, (Defs.DIAMETER + 1) * MO_SIZE);

        //draw nutrients
        for (int i = 0; i < Defs.DIAMETER; i++) {
            for (int j = 0; j < Defs.DIAMETER; j++) {
                if (Petri.getInstance().inDish(i, j)) {
                    petriDish.setFill(Color.color(NUTRIENT_COLOR.getRed(), NUTRIENT_COLOR.getGreen(), NUTRIENT_COLOR.getBlue(), getAlphaNutrients((i * Defs.DIAMETER) + j)));
                    petriDish.fillRect((i + 1) * MO_SIZE, (j + 1) * MO_SIZE, MO_SIZE, MO_SIZE);
                }
            }
        }

        //draw MOs
        for (int i = 0; i < Defs.DIAMETER; i++) {
            for (int j = 0; j < Defs.DIAMETER; j++) {
                if (Petri.getInstance().inDish(i, j)) {
                    if ((i * Defs.DIAMETER + j) % 2 == 0)
                        petriDish.setFill(Color.RED);
                    else
                        petriDish.setFill(Color.BLUE);

                    petriDish.fillOval((i + 1) * MO_SIZE, (j + 1) * MO_SIZE, MO_SIZE, MO_SIZE);
                }

            }
        }
    }

    private double getAlphaNutrients(double nutrients) {
        return Math.pow(nutrients / 5000d, 1d / 3d);


    }

    public void setSimulation(List<Battle> simulation) throws ValidationException {
        if (simulation == null || simulation.isEmpty())
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
