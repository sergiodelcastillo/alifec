package alifec.simulation.simulation;

import alifec.core.contest.Battle;
import alifec.core.contest.Contest;
import alifec.core.exception.ValidationException;
import alifec.core.simulation.Defs;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Created by Sergio Del Castillo on 02/07/18.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class ALifeContestSimulationView extends Stage {
    static final int MO_SIZE = 8;
    static final int WIDTH = (3 + Defs.DIAMETER) * MO_SIZE;
    static final int DISH_HEIGHT = WIDTH;
    static final int INFO_HEIGHT = 60;
    static final int TREND_HEIGHT = 120;
    static final String COLOR_BACKGROUND_STRING = "#F3F3F3";
    private static Logger logger = LogManager.getLogger(ALifeContestSimulationView.class);
    private final ResourceBundle bundle;

    private boolean active;
    private Canvas petriDish;
    private Canvas colonyInfo;
    private Canvas energyTrend;
    private ALifeContestSimulationTimer timer;
    private boolean paused;

    public ALifeContestSimulationView(Parent father, Contest contest, ResourceBundle bundle) {
        super();

        if (Objects.nonNull(father)) {
            initOwner(father.getScene().getWindow());
        }

        this.bundle = bundle;

        VBox rootPane = new VBox();
        rootPane.setStyle("-fx-background-color: " + COLOR_BACKGROUND_STRING);
//        rootPane.setSpacing(10);
        rootPane.setPadding(new Insets(10, 10, 10, 10));
        petriDish = new Canvas();
        colonyInfo = new Canvas();
        energyTrend = new Canvas();

        Label info = new Label("P: pause/one movement; ENTER: continue simulation; Q: quit");
        //info.setPadding(new Insets(10, 10, 10, 10));
        rootPane.getChildren().addAll(petriDish, colonyInfo, energyTrend, info);

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

        petriDish.setHeight(DISH_HEIGHT);
        petriDish.setWidth(WIDTH);

        colonyInfo.setHeight(INFO_HEIGHT);
        colonyInfo.setWidth(WIDTH);

        energyTrend.setHeight(TREND_HEIGHT);
        energyTrend.setWidth(WIDTH);


        setResizable(false);
        active = false;

        timer = new ALifeContestSimulationTimer(this, contest);
    }

    private void continueSimulation() {
        paused = timer.disableOneRunSimulation();
    }

    private void oneRunSimulation() {
        paused = timer.setOneRunSimulation();
    }

    public void endSimulation() {
        if (active) {
            active = false;
            paused = false;
        }

        timer.endSimulation();
        this.hide();

        logger.debug("Simulation ended. bye :)");
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
