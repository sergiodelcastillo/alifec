package alifec.simulation.simulation;

import alifec.core.contest.Battle;
import alifec.core.contest.Contest;
import alifec.core.event.EventBus;
import alifec.core.event.impl.BattleEvent;
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

import static alifec.simulation.simulation.ALifeContestSimulationView.*;

/**
 * Created by Sergio Del Castillo on 09/07/18.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class ALifeContestSimulationTimer extends AnimationTimer {

    //COLORS
    private static Color COLOR_LINE = Color.GRAY;
    private static Color COLOR_COLONY_A = Color.RED;
    private static Color COLOR_COLONY_B = Color.BLUE;
    private static Color COLOR_NUTRIENT = Color.YELLOW;
    private static Color COLOR_BACKGROUND = Color.valueOf(COLOR_BACKGROUND_STRING);

    //Fonts
    private static Font FONT_COLONIES = new Font(Font.getDefault().getName(), 14);
    private static String FORMAT_ENERGY = "Energy: %.2f";
    private static String FORMAT_MOS = "MOs: %d";


    private final ALifeContestSimulationView view;
    private final GraphicsContext dish;
    private final GraphicsContext info;
    private final GraphicsContext trend;

    private final Contest contest;
    private final Environment environment;
    private final Queue<Battle> battles;
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

        dish = view.getPetriDishGraphicsContext();
        info = view.getColonyInfoGraphicsContext();
        trend = view.getEnergyTrendGraphicsContext();
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
            //todo: improve the lastState update, it could be passed to decideAction and updated in this method
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
                    notifyWinner();
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

    private void notifyWinner() {
        //notify winner
        EventBus.post(new BattleEvent(environment, environment.getResults(), BattleEvent.Status.FINISH));

        colonyA = null;
        colonyB = null;
    }

    private void showEndSimulation() {
        System.out.println("Simulation will close");
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

        stop();


        System.out.println("timer stopped");
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

        dish.setTextAlign(TextAlignment.CENTER);
        //todo: improve this!!!
        dish.setFont(new Font(Font.getDefault().getName(), 16));
        dish.setFill(COLOR_LINE);

        if (text1 != null && !text1.isEmpty())
            dish.fillText(text1, view.getPetriDishWidth() / 2, 150);

        if (text2 != null && !text2.isEmpty())
            dish.fillText(text2, view.getPetriDishWidth() / 2, 200);
    }


    private void clearDish() {
        dish.setFill(COLOR_BACKGROUND);
        dish.fillOval(0, 0, WIDTH, DISH_HEIGH);
    }

    private boolean live() throws MoveMicroorganismException {
        //do the movement of colonies
        boolean battleContinue = !environment.moveColonies();

        //draw the petri dish including colonies and nutrients
        drawPetriDish();

        //Colonies information
        drawColoniesInformation();

        //todo: implement the history
        drawColoniesTrend();
        return battleContinue;

    }

    private void drawPetriDish() {
        //Clear the whole circle
        clearDish();

        //draw nutrients
        for (int x = 0; x < Defs.DIAMETER; x++) {
            for (int y = 0; y < Defs.DIAMETER; y++) {
                if (Petri.getInstance().inDish(x, y)) {
                    dish.setFill(nutrientColor(environment.getAgar().getNutrient(x, y)));
                    dish.fillRect((x + 1) * MO_SIZE, (y + 1) * MO_SIZE, MO_SIZE, MO_SIZE);
                }
            }
        }

        //draw MOs
        for (int a = 0; a < colonyA.size(); a++) {
            Cell cell = colonyA.getMO(a);
            dish.setFill(colonyAColor(cell.ene));
            dish.fillOval((cell.x + 1) * MO_SIZE, (cell.y + 1) * MO_SIZE, MO_SIZE, MO_SIZE);
        }

        for (int b = 0; b < colonyB.size(); b++) {
            Cell cell = colonyB.getMO(b);
            dish.setFill(colonyBColor(cell.ene));
            dish.fillOval((cell.x + 1) * MO_SIZE, (cell.y + 1) * MO_SIZE, MO_SIZE, MO_SIZE);
        }

        //draw the petri dish line
        dish.setStroke(COLOR_LINE);
        dish.setLineWidth(MO_SIZE);
        dish.strokeOval(MO_SIZE, MO_SIZE, (Defs.DIAMETER + 1) * MO_SIZE, (Defs.DIAMETER + 1) * MO_SIZE);
    }

    private void drawColoniesInformation() {
        //todo: the performance can be improved:
        // currently the whole rectangle is repainted per iteration.
        // There is an option of painting the line and the colony names and update only
        // the fields which are changing every iteration (energy / mos ).
        // To cleanup the already painted energy / mos it is possible to use
        // "old" amount and paint them with background color and then
        // paint the new amount with the corresponding color.

        //clear the old information
        info.setFill(COLOR_BACKGROUND);
        info.fillRect(0, 0, WIDTH, INFO_HEIGH);

        info.setFont(FONT_COLONIES);
        info.setStroke(COLOR_LINE);
        info.setLineWidth(2);
        info.strokeLine(MO_SIZE, MO_SIZE, WIDTH - MO_SIZE, MO_SIZE);

        drawColonyLine(COLOR_COLONY_A, environment.getFirstOpponent(), 28);

        drawColonyLine(COLOR_COLONY_B, environment.getSecondOpponent(), 50);
    }

    private void drawColoniesTrend() {
        //todo
    }

    private void drawColonyLine(Color color, Colony colony, int y) {
        info.setFill(color);
        info.fillText(colony.getName(), 10, y);
        info.fillText(String.format(FORMAT_ENERGY, colony.getEnergy()), 160, y);
        info.fillText(String.format(FORMAT_MOS, colony.getMOs().size()), 340, y);
    }


    private Color nutrientColor(float nutrient) {
        return Color.color(COLOR_NUTRIENT.getRed(), COLOR_NUTRIENT.getGreen(), COLOR_NUTRIENT.getBlue(), getAlphaNutrients(nutrient));
    }

    /**
     * This f(x) will always return a value between 0-1
     * The reason behind of using a x^3 function is
     * because low values of nutrients are almost not visible
     * if it is normalized by using nutrients/MAX_NUTRIENTS.
     *
     * @param nutrients the amount of nutrient in an specific position (x,y). The nutrient value should be between 0 and 5000.
     * @return a value between 0 and 1
     */
    private double getAlphaNutrients(double nutrients) {
        return Math.pow(nutrients / 5000d, 1d / 3d);
    }

    private Color colonyAColor(float energy) {
        return calculateColonyColor(COLOR_COLONY_A, energy);
    }

    private Color colonyBColor(float energy) {
        return calculateColonyColor(COLOR_COLONY_B, energy);
    }

    private Color calculateColonyColor(Color c, float e) {
        return Color.color(c.getRed(), c.getGreen(), c.getBlue(), Math.min(e / (Defs.E_INITIAL * 2), 1));
    }

    public void startSimulation(List<Battle> simulation) throws ValidationException {
        if (simulation == null || simulation.isEmpty())
            throw new ValidationException("The battle list is empty so it is not possible to run a simulation.");

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
                return battles.isEmpty() ? Action.END_SIMULATION : Action.START_BATTLE;
            case END_SIMULATION:
                return Action.EXIT;
        }

        //something wrong .. finish simulation!!
        return Action.EXIT;
    }

    private void resetValues() {
        battles.clear();
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
