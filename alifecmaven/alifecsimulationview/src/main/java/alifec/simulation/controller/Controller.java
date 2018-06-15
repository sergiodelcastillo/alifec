package alifec.simulation.controller;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Created by Sergio Del Castillo on 14/06/18.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public abstract class Controller {

    public abstract Stage init(MainController controller, Parent root);

    protected Stage buildDefaultStage(Parent root, String title) {
        Stage preferences = new Stage();

        preferences.setTitle(title);
        preferences.initModality(Modality.WINDOW_MODAL);
        preferences.setResizable(false);
        preferences.setScene(new Scene(root));

        return preferences;
    }
}
