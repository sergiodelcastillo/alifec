package alifec.simulation.controller;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by Sergio Del Castillo on 10/06/18.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class ALifeContestController extends Application implements MainController {
    @FXML
    public BorderPane mainLayout;

    @FXML
    public Label messagePanel;

    @FXML
    public TableView rankingTable;

    private Stage dialogAbout;

    private Stage dialogPreferences;

    private Stage dialogStatistics;

    private Stage dialogNewContest;

    private ResourceBundle bundle;


    public ALifeContestController() {
        Locale currentLocale = Locale.ENGLISH;
        //TODO: set the default locale from comfiguration or load english instead.

        bundle = ResourceBundle.getBundle("i18n/messages", currentLocale);
        System.out.println("init constructor");
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle(bundle.getString("ALifeContestMain.title"));
        final Parent root = FXMLLoader.load(getClass().getResource("/ALifeContest.fxml"), bundle);
        stage.setScene(new Scene(root));
        stage.show();
    }


    public void newContest(ActionEvent ignored) {
        try {
            if (dialogNewContest == null) {
                dialogNewContest = createDialogStage("/DialogNewContest.fxml");
            }

            dialogNewContest.showAndWait();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void changeContest(ActionEvent event) {
        System.out.println("set default contest");
    }

    public void quit(ActionEvent event) {
        System.out.println("quit");

        /*TODO:
           if (!contest.createBackUp())
           */        // logger.error("Cannot create the backup file");

        Platform.exit();

    }

    public void showDialogStatistics(ActionEvent ignored) {
        try {
            if (dialogStatistics == null) {
                dialogStatistics = createDialogStage("/DialogStatistics.fxml");
            }

            dialogStatistics.showAndWait();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void showDialogPreferences(ActionEvent ignored) {
        try {
            if (dialogPreferences == null) {
                dialogPreferences = createDialogStage("/DialogPreferences.fxml");
            }

            dialogPreferences.showAndWait();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void showDialogHelp(ActionEvent ignored) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(bundle.getString("ALifeContestMain.help.title"));
        alert.setHeaderText(bundle.getString("ALifeContestMain.help.header"));
        alert.setContentText(bundle.getString("ALifeContestMain.help.contentText"));

        alert.showAndWait();
    }

    public void showDialogAbout(ActionEvent ignored) {
        try {
            if (dialogAbout == null) {
                dialogAbout = createDialogStage("/DialogAbout.fxml");
            }

            dialogAbout.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Stage createDialogStage(String stageFile) throws java.io.IOException {
        FXMLLoader loader = getFXMLLoader(stageFile);
        Parent root = loader.load();

        Stage stage = ((Controller) loader.getController()).init(this, root);
        stage.initOwner(mainLayout.getScene().getWindow());

        return stage;
    }


    private FXMLLoader getFXMLLoader(String fxml) {
        return new FXMLLoader(getClass().getResource(fxml), bundle);
    }

    @Override
    public void savePreferences() {
        System.out.println("update data");

    }

    public void createReportTxt() {
        System.out.println("update data txt");
    }

    public void createReportCsv() {
        System.out.println("update data csv");
    }

    public ResourceBundle getBundle() {
        return bundle;
    }

}
