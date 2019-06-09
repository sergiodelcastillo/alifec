package alifec.simulation.simulation;

import alifec.core.contest.Battle;
import alifec.core.contest.Contest;
import alifec.core.exception.MoveMicroorganismException;
import alifec.core.exception.ValidationException;
import alifec.core.simulation.*;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

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
    private final Contest contest;
    private final Environment environment;

    private Queue<Battle> battles;
    private State lastState;
    private Battle current;
    private Colony colonyA;
    private Colony colonyB;

    //Control information useful for count down.
    private long startNanoTime;
    private long lastCount;
    private long MAX_COUNT = 10;
    private long MILLISECONDS = 1000000000L;
    private long MAX_START_TIME = MAX_COUNT * MILLISECONDS;

    public ALifeContestSimulationTimer(ALifeContestSimulationView view, Contest contest) {
        this.view = view;

        petriDish = view.getPetriDishGraphicsContext();
        colonyInfo = view.getColonyInfoGraphicsContext();
        energyTrend = view.getEnergyTrendGraphicsContext();
        battles = new LinkedList<>();
        this.contest = contest;
        this.environment = contest.getEnvironment();

        resetValues();
    }


    @Override
    public void handle(long now) {
        if (startNanoTime == 0) {
            startNanoTime = now;
        }

        try {
            //todo: improve the lastState update, it coul be passed to decideAction and updated in this method
            // so the switch
            switch (decideAction()) {
                case START_SIMULATION:
                    if (now - startNanoTime >= MAX_START_TIME)
                        lastState = State.THE_BEGINNING;
                    else
                        showStartSimulation((now - startNanoTime) / MILLISECONDS);
                    break;
                case START_BATTLE:
                    createBattle();
                    if (live()) {
                        lastState = State.LIVING;
                    } else {
                        lastState = State.END_BATTLE;
                    }
                    break;
                case MOVE_COLONIES:
                    if (live()) {
                        lastState = State.LIVING;
                    } else {
                        lastState = State.END_BATTLE;
                    }
                    break;
                case MOVE_COLONIES_ONE_STEP:
                    if (live()) {
                        lastState = State.LIVING_ONE_STEP;
                    } else {
                        lastState = State.END_BATTLE;
                    }
                    stop();
                    break;
                case END_BATTLE:
                    waitBetweenBattles();
                    lastState = State.END_BATTLE;
                    break;
                case END_SIMULATION:
                    showEndSimulation();
                    lastState = State.END_SIMULATION;
                    break;
                case EXIT:
                    view.endSimulation();
            }
        } catch (MoveMicroorganismException e) {
            e.printStackTrace();
        }
    }

    private void showEndSimulation() {
        System.out.println("End simulation");
    }

    private void createBattle() {
        System.out.println("create battle");
        if (battles.isEmpty()) return;

        current = battles.poll();


        //todo: improve create battle definition, it should throw exceptions intead of boolean result.
        if (!environment.createBattle(current)) {
            System.out.println("someting wrong!!!!");
        }

        this.colonyA = environment.getFirstOpponent();
        this.colonyB = environment.getSecondOpponent();

        //todo: agregar logger. battle created: <bla>

    }

    private void waitBetweenBattles() {
        System.out.println("wait between battles");
    }

    public void endSimulation() {
        //clean everything!
        resetValues();
        System.out.println("endSimulation");

        stop();
    }

    private void showStartSimulation(long seconds) {
        if (lastCount < seconds) {
            lastCount = seconds;

            String text1 = view.getBundle().getString("AlifecContestSimulationTimer.welcome") + " " + view.getBundle().getString("alifec.version");
            String text2 = String.valueOf(MAX_COUNT - seconds);
            centeredText(text1, text2);

            //todo: improve logging
            System.out.println("start=" + (MAX_COUNT - seconds));
        }
    }

    private void centeredText(String text1, String text2) {
        clearDish();

        petriDish.setTextAlign(TextAlignment.CENTER);
        petriDish.setFont(new Font(Font.getDefault().getName(), 16));
        petriDish.setFill(Color.GRAY);

        if (text1 != null && !text1.isEmpty())
            petriDish.fillText(text1, view.getPetriDishWidth() / 2, 150);

        if (text2 != null && !text2.isEmpty())
            petriDish.fillText(text2, view.getPetriDishWidth() / 2, 200);
    }


    private void clearDish() {
        petriDish.setFill(Color.valueOf(ALifeContestSimulationView.getColorBackground()));
        petriDish.fillOval(0, 0, MO_SIZE * (3 + Defs.DIAMETER), MO_SIZE * (3 + Defs.DIAMETER));
    }

    private boolean live() throws MoveMicroorganismException {
        //move colonies
        boolean battleContinue = !environment.moveColonies();

        //Clear the whole circle
        clearDish();

        //draw nutrients
        for (int x = 0; x < Defs.DIAMETER; x++) {
            for (int y = 0; y < Defs.DIAMETER; y++) {
                if (Petri.getInstance().inDish(x, y)) {
                    petriDish.setFill(nutrientColor(environment.getAgar().getNutrient(x, y)));
                    petriDish.fillRect((x + 1) * MO_SIZE, (y + 1) * MO_SIZE, MO_SIZE, MO_SIZE);
                }
            }
        }

        //draw MOs
        for (int a = 0; a < colonyA.size(); a++) {
            Cell cell = colonyA.getMO(a);
            petriDish.setFill(colonyAColor(cell.ene));
            petriDish.fillOval((cell.x + 1) * MO_SIZE, (cell.y + 1) * MO_SIZE, MO_SIZE, MO_SIZE);
        }

        for (int b = 0; b < colonyB.size(); b++) {
            Cell cell = colonyB.getMO(b);
            petriDish.setFill(colonyBColor(cell.ene));
            petriDish.fillOval((cell.x + 1) * MO_SIZE, (cell.y + 1) * MO_SIZE, MO_SIZE, MO_SIZE);
        }

        //draw the petri dish
        petriDish.setStroke(Color.GRAY);
        petriDish.setLineWidth(MO_SIZE);
        petriDish.strokeOval(MO_SIZE, MO_SIZE, (Defs.DIAMETER + 1) * MO_SIZE, (Defs.DIAMETER + 1) * MO_SIZE);

        //todo: implement battle information

        //todo: implement the history
        return battleContinue;

    }

    private Color nutrientColor(float nutrient) {
        return Color.color(NUTRIENT_COLOR.getRed(), NUTRIENT_COLOR.getGreen(), NUTRIENT_COLOR.getBlue(), getAlphaNutrients(nutrient));
    }

    private double getAlphaNutrients(double nutrients) {
        return Math.pow(nutrients / 5000d, 1d / 3d);
    }

    private Color colonyAColor(float energy) {
        return calculateColonyColor(COLONY_A_COLOR, energy);
    }

    private Color colonyBColor(float energy) {
        return calculateColonyColor(COLONY_B_COLOR, energy);
    }

    private Color calculateColonyColor(Color c, float e) {
        return Color.color(c.getRed(), c.getGreen(), c.getBlue(), Math.min(e / (Defs.E_INITIAL * 2), 1));
    }

    public void startSimulation(List<Battle> simulation) throws ValidationException {
        if (simulation == null || simulation.isEmpty())
            throw new ValidationException("The battle list is empty so it is not possible to run a simulation.");

        //clear battles list
        battles.clear();

        //set initial state!
        resetValues();

        //set the list of battles
        battles.addAll(simulation);

        //start
        start();
    }

    public boolean setOneRunSimulation() {
        if (lastState == State.END_BATTLE || lastState == State.END_SIMULATION) {
            start();
            return false;
        }
        if (lastState == State.LIVING || lastState == State.LIVING_ONE_STEP) {
            lastState = State.LIVING_ONE_STEP;

            start();
            return true;
        }
        return false;

    }

    public boolean disableOneRunSimulation() {
        if (lastState == State.LIVING_ONE_STEP) {
            lastState = State.LIVING;

            start();
            return false;
        }

        return true;
    }

    private Action decideAction() {

        switch (lastState) {
            case NONE:
                return Action.START_SIMULATION;
            case THE_BEGINNING:
                return Action.START_BATTLE;
            case LIVING:
                /*if(colonyA.isDied() || colonyB.isDied()){
                    return
                }*/
                return Action.MOVE_COLONIES;
            case LIVING_ONE_STEP:
                return Action.MOVE_COLONIES_ONE_STEP;
            case END_BATTLE:
                //ver!!
                colonyA = null;
                colonyB = null;
                return battles.isEmpty() ? Action.END_SIMULATION : Action.START_BATTLE;
            case END_SIMULATION:
                return Action.EXIT;
        }

        //something wrong .. finish simulation!!
        return Action.EXIT;
    }

    private void resetValues() {
        lastState = State.NONE;
        startNanoTime = 0;
        lastCount = -1;
    }

    public boolean pauseSimulation() {
        // it is allowed to stop only when the simulation is running.
        if (lastState == State.LIVING) {
            stop();
            return true;
        }
        return false;
    }

}
