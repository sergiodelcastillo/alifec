package alifec.simulation.simulation;

import alifec.core.contest.Battle;
import alifec.core.contest.Contest;
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
import java.util.ResourceBundle;

/**
 * Created by Sergio Del Castillo on 02/07/18.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class ALifeContestSimulationView extends Stage {
    private static final String COLOR_BACKGROUND = "#F3F3F3";
    private final ResourceBundle bundle;

    private boolean active;
    private Canvas petriDish;
    private Canvas energyTrend;
    private Canvas colonyInfo;
    private ALifeContestSimulationTimer timer;
    private boolean paused;

    public ALifeContestSimulationView(Parent father, Contest contest, ResourceBundle bundle) {
        super();

        if (father != null) {
            initOwner(father.getScene().getWindow());
        }

        this.bundle = bundle;

        VBox rootPane = new VBox();
        rootPane.setStyle("-fx-background-color: " + COLOR_BACKGROUND);
//        rootPane.setSpacing(10);
        rootPane.setPadding(new Insets(10, 10, 10, 10));
        petriDish = new Canvas();
        energyTrend = new Canvas();
        colonyInfo = new Canvas();

        Label info = new Label("P: pause/one movement; ENTER: continue simulation; Q: quit");
        //info.setPadding(new Insets(10, 10, 10, 10));
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

        timer = new ALifeContestSimulationTimer(this, contest);
    }

    public static String getColorBackground() {
        return COLOR_BACKGROUND;
    }

    private void continueSimulation() {
        paused = timer.disableOneRunSimulation();
    }

    private void oneRunSimulation() {
        paused = timer.setOneRunSimulation();
    }

    public void endSimulation() {
        if (active) {
            //stop everything!!!

            active = false;
            paused = false;
        }

        timer.endSimulation();
        this.hide();
    }

    public void pauseSimulation() {
        paused = timer.pauseSimulation();
    }

    public void simulate(List<Battle> battles) throws ValidationException {
        //update all needed!
        active = true;
        paused = false;

        timer.startSimulation(battles);

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

    public double getPetriDishWidth() {
        return petriDish.getWidth();
    }

    public double getPetriDishHeight() {
        return petriDish.getHeight();
    }

    public boolean isPaused() {
        return paused;
    }

    public ResourceBundle getBundle() {
        return bundle;
    }
}
