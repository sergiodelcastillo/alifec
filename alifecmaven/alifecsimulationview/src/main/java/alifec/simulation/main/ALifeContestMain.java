package alifec.simulation.main;

import alifec.simulation.controller.ALifeContestController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by Sergio Del Castillo on 14/06/18.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class ALifeContestMain extends Application {

    private final ResourceBundle bundle;

    public static void main(String[] args) {
        System.out.println("todo: Load contest!!!");
        Application.launch(ALifeContestMain.class, args);
    }

    public ALifeContestMain(){
        Locale currentLocale = Locale.ENGLISH;
        //TODO: set the default locale from comfiguration or load english instead.

        bundle = ResourceBundle.getBundle("i18n/messages", currentLocale);
        System.out.println("Init constructor application");
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle(bundle.getString("ALifeContestMain.title"));
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ALifeContestView.fxml"), bundle);
        final Parent root = loader.load();

        ((ALifeContestController)loader.getController()).init(bundle, stage);

        stage.setScene(new Scene(root));
        stage.show();
    }
}
