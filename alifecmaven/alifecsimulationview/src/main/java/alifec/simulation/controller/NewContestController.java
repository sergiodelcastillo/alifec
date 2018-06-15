package alifec.simulation.controller;

import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.stage.Stage;

/**
 * Created by Sergio Del Castillo on 14/06/18.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class NewContestController extends Controller {
    private MainController father;
    private Parent root;

    @Override
    public Stage init(MainController controller, Parent root) {
        this.father = controller;
        this.root = root;

        return buildDefaultStage(root, father.getBundle().getString("newcontest.title"));
    }

    public void accept(ActionEvent event) {

    }

    public void cancel(ActionEvent event) {

    }
}
