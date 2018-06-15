package alifec.simulation.controller;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;


/**
 * Created by Sergio Del Castillo on 14/06/18.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class StatisticsController implements Controller {

    private MainController father;
    private Parent root;

    public Stage init(MainController father, Parent root) {
        this.father = father;
        this.root = root;

        Stage statistics = new Stage();
        statistics.setTitle(father.getBundle().getString("ALifeContestMain.statistics.title"));
        statistics.initModality(Modality.WINDOW_MODAL);
        statistics.setResizable(false);
        statistics.setScene(new Scene(root));

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
