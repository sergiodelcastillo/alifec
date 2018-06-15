package alifec.simulation.main;

import javafx.event.ActionEvent;
import javafx.scene.Node;

/**
 * Created by Sergio Del Castillo on 14/06/18.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class StatisticsController {

    private ALifeContestController father;

    public void init(ALifeContestController father) {
        this.father = father;
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
