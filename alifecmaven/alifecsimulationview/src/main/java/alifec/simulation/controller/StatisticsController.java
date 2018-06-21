package alifec.simulation.controller;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.util.ResourceBundle;


/**
 * Created by Sergio Del Castillo on 14/06/18.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class StatisticsController extends Controller {

    private MainController father;
    private Parent root;

    public Stage init(MainController father, Parent root, ResourceBundle bundle) {
        this.father = father;
        this.root = root;

        Stage statistics = buildDialog(root, bundle.getString("statistics.title"));

        statistics.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent e) -> {
            if (KeyCode.ESCAPE == e.getCode()) {
                statistics.close();
            }
        });
        return statistics;
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
