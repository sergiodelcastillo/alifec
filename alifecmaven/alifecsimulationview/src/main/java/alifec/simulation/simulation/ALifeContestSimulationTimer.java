package alifec.simulation.simulation;

import alifec.core.contest.Battle;
import alifec.core.simulation.Defs;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.List;

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
    private boolean gameOver;
    private List<Battle> battles;
    private boolean oneRun;

    public ALifeContestSimulationTimer(ALifeContestSimulationView view) {
        this.view = view;

        petriDish = view.getPetriDishGraphicsContext();
        colonyInfo = view.getColonyInfoGraphicsContext();
        energyTrend = view.getEnergyTrendGraphicsContext();
    }

    @Override
    public void handle(long now) {
        if (gameOver) return;

        System.out.println("bla " + now);
        //paintInit();
        drawPetriLine();

        if (oneRun) {
            stop();
        }

    }

    private void drawPetriLine() {
        petriDish.setStroke(Color.GRAY);
        petriDish.setLineWidth(MO_SIZE);
        petriDish.strokeOval(MO_SIZE >> 1,
                MO_SIZE >> 1,
                (Defs.DIAMETER + 1) * MO_SIZE,
                (Defs.DIAMETER + 1) * MO_SIZE);
    }

    public void setSimulation(List<Battle> simulation) {
        this.battles = simulation;
        this.gameOver = false;
        this.oneRun = false;

    }

    public void setOneRunSimulation() {
        oneRun = true;
    }

    public void disableOneRunSimulation() {
        oneRun = false;
    }
}
