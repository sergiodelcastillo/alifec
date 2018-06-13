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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
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

    private Stage dialogAbout;

    private Stage dialogPreferences;

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

    public void preferences(ActionEvent ignored) {
        try {
            if (dialogPreferences == null) {
                // Load the fxml file and create a new stage for the popup dialog.
                VBox preferences = getFXMLLoader("/DialogPreferences.fxml").load();

                dialogPreferences = new Stage();
                dialogPreferences.setTitle(bundle.getString("ALifeContestMain.preferences.title"));
                dialogPreferences.initModality(Modality.WINDOW_MODAL);
                dialogPreferences.initOwner(mainLayout.getScene().getWindow());
                dialogPreferences.setResizable(false);
                dialogPreferences.setScene(new Scene(preferences));

                /*dialogPreferences.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent e) -> {
                    if (KeyCode.ESCAPE == e.getCode()) {
                        dialogPreferences.close();
                    }
                });*/
            }
            dialogPreferences.showAndWait();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void help(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(bundle.getString("ALifeContestMain.help.title"));
        alert.setHeaderText(bundle.getString("ALifeContestMain.help.header"));
        alert.setContentText(bundle.getString("ALifeContestMain.help.contentText"));

        alert.showAndWait();
    }

    public void about(ActionEvent ignored) {
        try {
            // Create the dialog Stage.
            if (dialogAbout == null) {
                // Load the fxml file and create a new stage for the popup dialog.
                GridPane dialogAboutPane = getFXMLLoader("/DialogAbout.fxml").load();

                dialogAbout = new Stage();
                dialogAbout.setTitle(bundle.getString("ALifeContestMain.about.title"));
                dialogAbout.initModality(Modality.WINDOW_MODAL);
                dialogAbout.initOwner(mainLayout.getScene().getWindow());
                dialogAbout.setResizable(false);
                dialogAbout.setScene(new Scene(dialogAboutPane));

                dialogAbout.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent e) -> {
                    if (KeyCode.ESCAPE == e.getCode()) {
                        dialogAbout.close();
                    }
                });
            }
            // Show the dialog and wait until the user closes it
            dialogAbout.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private FXMLLoader getFXMLLoader(String fxml) {
        return new FXMLLoader(getClass().getResource(fxml), bundle);
    }
}
