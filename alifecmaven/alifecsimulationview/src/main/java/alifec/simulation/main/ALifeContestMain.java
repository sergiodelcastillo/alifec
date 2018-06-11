package alifec.simulation.main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by Sergio Del Castillo on 10/06/18.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class ALifeContestMain extends Application {
    @FXML
    public BorderPane mainLayout;

    @FXML
    public Label messagePanel;

    /*@FXML
    private MenuItem quit;*/

    private ResourceBundle bundle;

    public static void main(String[] args) {
        System.out.println("todo: Load contest!!!");
        Application.launch(ALifeContestMain.class, args);

    }

    public ALifeContestMain() {
        Locale currentLocale = Locale.ENGLISH;
        //TODO: set the default locale from comfiguration or load english instead.

        bundle = ResourceBundle.getBundle("i18n/messages", currentLocale);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle(bundle.getString("ALifeContestMain.title"));
        final Parent fxmlRoot = FXMLLoader.load(getClass().getResource("/Main.fxml"), bundle);
        stage.setScene(new Scene(fxmlRoot));
        stage.show();
    }

    public void handleButtonAction(ActionEvent event) {
        System.out.println("click");
    }


    public void newContest(ActionEvent event) {
        System.out.println("new contest");
    }

    public void setDefaultContest(ActionEvent event) {
        System.out.println("set default contest");
    }

    public void quit(ActionEvent event) {
        System.out.println("quit");

        /*TODO:
           if (!contest.createBackUp())
           */        // logger.error("Cannot create the backup file");

        Platform.exit();

    }

    public void setMode(ActionEvent event) {
        System.out.println("set mode");
    }

    public void reports(ActionEvent event) {
        System.out.println("reports");
    }

    public void preferences(ActionEvent event) {
        System.out.println("preferences");
    }

    public void help(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(bundle.getString("ALifeContestMain.help.title"));
        alert.setHeaderText(bundle.getString("ALifeContestMain.help.header"));
        alert.setContentText(bundle.getString("ALifeContestMain.help.contentText"));

        alert.showAndWait();
    }

    public void about(ActionEvent event) {
        try {
            // Load the fxml file and create a new stage for the popup dialog.
            GridPane page = new FXMLLoader(getClass().getResource("/DialogAbout.fxml"), bundle).load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle(bundle.getString("ALifeContestMain.about.title"));
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(mainLayout.getScene().getWindow());
            dialogStage.setResizable(false);
            dialogStage.setScene(new Scene(page));

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

}
