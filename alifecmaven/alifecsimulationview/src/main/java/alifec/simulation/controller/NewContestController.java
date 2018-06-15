package alifec.simulation.controller;

import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Created by Sergio Del Castillo on 14/06/18.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class NewContestController implements Controller {
    private MainController father;
    private Parent root;

    @Override
    public Stage init(MainController controller, Parent root) {
        this.father = controller;
        this.root = root;

        Stage preferences = new Stage();
        preferences.setTitle(father.getBundle().getString("ALifeContestMain.newcontest.title"));
        preferences.initModality(Modality.WINDOW_MODAL);
        preferences.setResizable(false);
        preferences.setScene(new Scene(root));

        return preferences;
    }

    public void accept(ActionEvent event) {

    }

    public void cancel(ActionEvent event) {

    }
}
