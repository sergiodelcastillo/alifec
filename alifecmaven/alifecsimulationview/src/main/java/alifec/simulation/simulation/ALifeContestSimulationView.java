package alifec.simulation.simulation;

import alifec.core.contest.Battle;
import alifec.core.exception.ValidationException;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

/**
 * Created by Sergio Del Castillo on 02/07/18.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class ALifeContestSimulationView extends Stage {
    private boolean active;
    private Canvas petriDish;
    private Canvas energyTrend;
    private Canvas colonyInfo;
    private ALifeContestSimulationTimer timer;
    private boolean paused;

    public ALifeContestSimulationView(Parent father) {
        super();

        if (father != null)
            initOwner(father.getScene().getWindow());

        VBox rootPane = new VBox();
        petriDish = new Canvas();
        energyTrend = new Canvas();
        colonyInfo = new Canvas();
        Label info = new Label("P: pause/one movement; ENTER: continue simulation; Q: quit");
        info.setPadding(new Insets(10, 10, 10, 10));
        rootPane.getChildren().addAll(petriDish, energyTrend, colonyInfo, info);

        Scene scene = new Scene(rootPane);
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case Q:
                case ESCAPE:
                    endSimulation();
                    break;
                case P:
                    if (isPaused()) {
                        oneRunSimulation();
                    } else {
                        pauseSimulation();
                    }
                    break;
                case ENTER:
                    continueSimulation();
                    break;
            }
        });
        setScene(scene);

        setOnCloseRequest(event -> endSimulation());


        petriDish.setHeight(432);
        petriDish.setWidth(432);

        energyTrend.setHeight(180);
        energyTrend.setWidth(432);

        colonyInfo.setHeight(28);
        colonyInfo.setWidth(432);

        setResizable(false);
        active = false;

        timer = new ALifeContestSimulationTimer(this);
    }

    private void continueSimulation() {
        timer.disableOneRunSimulation();
        timer.start();
        paused = false;
    }

    private void oneRunSimulation() {
        timer.setOneRunSimulation();
        timer.start();
        paused = true;
    }

    public void endSimulation() {
        if (active) {
            //stop everything!!!

            active = false;
            paused = false;
        }

        timer.stop();
        this.hide();
    }


    public void pauseSimulation() {
        timer.stop();
        paused = true;
    }



    public void simulate(List<Battle> battles) throws ValidationException {
        //update all needed!
        active = true;
        paused = false;

        timer.setSimulation(battles);
        timer.start();
        //how
        showAndWait();
    }


    public GraphicsContext getPetriDishGraphicsContext() {
        return petriDish.getGraphicsContext2D();
    }

    public GraphicsContext getEnergyTrendGraphicsContext() {
        return energyTrend.getGraphicsContext2D();
    }

    public GraphicsContext getColonyInfoGraphicsContext() {
        return colonyInfo.getGraphicsContext2D();
    }
    public boolean isPaused() {
        return paused;
    }
}
