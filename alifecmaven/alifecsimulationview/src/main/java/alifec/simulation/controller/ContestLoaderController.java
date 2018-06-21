package alifec.simulation.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.ResourceBundle;

/**
 * Created by Sergio Del Castillo on 19/06/18.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class ContestLoaderController extends Controller {
    @FXML
    public GridPane fixFilePane;
    @FXML
    public GridPane discardFilePane;
    private Parent root;
    private MainController father;

    @Override
    public Stage init(MainController controller, Parent root, ResourceBundle bundle) {
        this.father = controller;
        this.root = root;

        Stage stage = buildDialog(root, bundle.getString("contest.loader.title"));

        setDefaults();

        return stage;
    }

    public void discardFile(ActionEvent event) {
        setDiscardFile();
    }


    public void fixFile(ActionEvent event) {
        setFixFile();
    }

    @Override
    void setDefaults() {
        setDiscardFile();
    }

    private void setDiscardFile() {
        fixFilePane.setVisible(false);
        discardFilePane.setVisible(true);
    }

    private void setFixFile() {
        fixFilePane.setVisible(true);
        discardFilePane.setVisible(false);
    }
}
