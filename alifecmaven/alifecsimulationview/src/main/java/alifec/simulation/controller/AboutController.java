package alifec.simulation.controller;

import javafx.scene.Parent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

/**
 * Created by Sergio Del Castillo on 14/06/18.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class AboutController extends Controller {

    private MainController father;
    private Parent root;

    @Override
    public Stage init(MainController controller, Parent root) {
        this.father = controller;
        this.root = root;

        Stage about = buildDialog(root, father.getBundle().getString("about.title"));

        about.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent e) -> {
            if (KeyCode.ESCAPE == e.getCode()) {
                about.close();
            }
        });

        return about;
    }
}
