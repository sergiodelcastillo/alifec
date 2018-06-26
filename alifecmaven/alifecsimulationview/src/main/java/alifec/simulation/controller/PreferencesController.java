package alifec.simulation.controller;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.util.ResourceBundle;

/**
 * Created by Sergio Del Castillo on 14/06/18.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class PreferencesController {

    public ComboBox<String> competitorOptions;
    private ALifeContestController controller;

    public void setMainController(ALifeContestController controller) {
        this.controller = controller;
    }

    public void keyHandler(KeyEvent event) {
        if (KeyCode.ESCAPE == event.getCode()) {
            ((Scene) event.getSource()).getWindow().hide();
        }
    }

    public void cancel(ActionEvent event) {
        ((Node) event.getSource()).getScene().getWindow().hide();
    }

    public void accept(ActionEvent event) {
        //DO THE WORK!!
        controller.savePreferences();

        ((Node) event.getSource()).getScene().getWindow().hide();
    }

    public void changeContest(ActionEvent event) {
        System.out.println("change contest");
    }
}
