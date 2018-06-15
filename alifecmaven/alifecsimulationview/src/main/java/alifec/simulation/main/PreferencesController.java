package alifec.simulation.main;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;

import java.util.ResourceBundle;

/**
 * Created by Sergio Del Castillo on 14/06/18.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class PreferencesController {

    public ComboBox<String> competitorOptions;
    private ALifeContestController mainController;

    public PreferencesController() {
    }

    public void init(ALifeContestController mainController) {
        this.mainController = mainController;

        ResourceBundle bundle = mainController.getBundle();
        competitorOptions.getItems().addAll(
                bundle.getString("preferences.contest.mode.competition"),
                bundle.getString("preferences.contest.mode.programmer")
        );
    }

    public void cancel(ActionEvent event) {
        ((Node) event.getSource()).getScene().getWindow().hide();
    }

    public void accept(ActionEvent event) {
        //DO THE WORK!!
        mainController.acceptPreferences(event);

        ((Node) event.getSource()).getScene().getWindow().hide();
    }
}
