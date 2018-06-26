package alifec.simulation.controller;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.util.ResourceBundle;


/**
 * Created by Sergio Del Castillo on 14/06/18.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class StatisticsController {

    private ALifeContestController father;
    //private Parent root;


    public void setMainController(ALifeContestController controller) {
        this.father = controller;
    }

    public void keyHandler(KeyEvent event) {
        if (KeyCode.ESCAPE == event.getCode()) {
            ((Scene) event.getSource()).getWindow().hide();
        }
    }

    public void generateReportTxt(ActionEvent event) {
        //do the work
        father.createReportTxt();

        ((Node) event.getSource()).getScene().getWindow().hide();
    }

    public void generateReportCsv(ActionEvent event) {
        //do the work
        father.createReportCsv();

        ((Node) event.getSource()).getScene().getWindow().hide();
    }

    public void cancelReports(ActionEvent event) {
        ((Node) event.getSource()).getScene().getWindow().hide();
    }
}
