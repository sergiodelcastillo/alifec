package alifec.simulation.controller;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import java.util.ResourceBundle;

/**
 * Created by Sergio Del Castillo on 14/06/18.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class PreferencesController extends Controller {

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

        return buildDefaultStage(root, bundle.getString("preferences.title"));
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
