package alifec.simulation.controller;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ResourceBundle;

/**
 * Created by Sergio Del Castillo on 14/06/18.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class PreferencesController implements Controller {

    public ComboBox<String> competitorOptions;
    private MainController controller;
    private Parent root;

    public PreferencesController() {
    }


    public Stage init(MainController controller, Parent root) {
        this.controller = controller;
        this.root = root;

        ResourceBundle bundle = controller.getBundle();
        competitorOptions.getItems().addAll(
                bundle.getString("preferences.contest.mode.competition"),
                bundle.getString("preferences.contest.mode.programmer")
        );

        Stage preferences = new Stage();
        preferences.setTitle(bundle.getString("ALifeContestMain.preferences.title"));
        preferences.initModality(Modality.WINDOW_MODAL);
        preferences.setResizable(false);
        preferences.setScene(new Scene(root));

        return preferences;
    }

    public void cancel(ActionEvent event) {
        ((Node) event.getSource()).getScene().getWindow().hide();
    }

    public void accept(ActionEvent event) {
        //DO THE WORK!!
        controller.savePreferences();

        ((Node) event.getSource()).getScene().getWindow().hide();
    }
}
