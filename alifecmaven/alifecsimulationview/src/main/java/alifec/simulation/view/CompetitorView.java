package alifec.simulation.view;

import alifec.core.contest.oponentInfo.ColonyStatistics;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.util.Comparator;

/**
 * Created by Sergio Del Castillo on 18/06/18.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class CompetitorView extends GridPane {
    private final ColonyStatistics model;

    //todo: add translation
    private String format = "Energy: %.2f, Points: %d";

    public CompetitorView(ColonyStatistics col, float maxEnergy) {
        this.model = col;

        ColumnConstraints constraint1 = new ColumnConstraints();
        constraint1.setHgrow(Priority.SOMETIMES);
        constraint1.setPercentWidth(40.0);
        constraint1.setHalignment(HPos.LEFT);

        ColumnConstraints constraint2 = new ColumnConstraints();
        constraint2.setHgrow(Priority.SOMETIMES);
        constraint2.setPercentWidth(60.0);

        getColumnConstraints().addAll(constraint1, constraint2);

        Label opponentName = new Label();
        opponentName.setStyle("-fx-font-weight: bold; -fx-font-size: 14");
        opponentName.setText(col.getName());

        ProgressBar opponentsProgress = new ProgressBar();
        GridPane.setHgrow(opponentsProgress, Priority.ALWAYS);
        opponentsProgress.setMaxWidth(Double.MAX_VALUE);
        opponentsProgress.setProgress(col.getAccumulated()/maxEnergy);
        opponentsProgress.setMouseTransparent(true);

        Label opponentsDetails = new Label();
        opponentsDetails.setStyle("-fx-font-size: 12");
        opponentsDetails.setText(String.format(format, col.getAccumulated(), col.getPoints()));

        add(opponentName, 0, 0);
        add(opponentsProgress, 1, 0);
        add(opponentsDetails, 1, 1);
    }



    public ColonyStatistics getModel(){
        return model;
    }
}
