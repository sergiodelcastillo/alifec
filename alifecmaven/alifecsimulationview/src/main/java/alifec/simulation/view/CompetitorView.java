package alifec.simulation.view;

import alifec.core.contest.oponentInfo.ColonyStatistics;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * Created by Sergio Del Castillo on 18/06/18.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class CompetitorView extends GridPane {

    private final ColonyStatistics model;
    private final ResourceBundle bundle;

    @FXML
    public Label opponentName;
    @FXML
    public Label opponentsDetails;
    @FXML
    public ProgressBar opponentsProgress;

    public CompetitorView(ColonyStatistics col, float maxEnergy, ResourceBundle bundle) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/CompetitorView.fxml"), bundle);

        loader.setRoot( this );
        loader.setController( this );

        try {
            loader.load();
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }

        this.bundle = bundle;
        this.model = col;
        opponentName.setText(col.getName());
        recalculate(maxEnergy);
    }

    public ColonyStatistics getModel(){
        return model;
    }

    public void recalculate(float maxEnergy){
        opponentsProgress.setProgress(model.getAccumulated()/maxEnergy);
        String pattern = bundle.getString("competitor.detail.format");
        opponentsDetails.setText(MessageFormat.format(pattern, model.getAccumulated(), model.getPoints()));
    }
}
