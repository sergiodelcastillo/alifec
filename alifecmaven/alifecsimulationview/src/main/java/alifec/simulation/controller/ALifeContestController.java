package alifec.simulation.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.ResourceBundle;

/**
 * Created by Sergio Del Castillo on 10/06/18.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class ALifeContestController implements MainController {
    @FXML
    public BorderPane mainLayout;

    @FXML
    public Label messagePanel;

    @FXML
    public TableView rankingTable;

    private Stage root;

    private Stage dialogAbout;

    private Stage dialogPreferences;

    private Stage dialogStatistics;

    private Stage dialogNewContest;

    private ResourceBundle bundle;

    public void init(ResourceBundle bundle, Stage root) {
        this.bundle = bundle;
        this.root = root;
    }

    public void newContest(ActionEvent ignored) {
        try {
            if (dialogNewContest == null) {
                dialogNewContest = createDialog("/DialogNewContest.fxml");
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
                dialogStatistics = createDialog("/DialogStatistics.fxml");
            }

            dialogStatistics.showAndWait();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void showDialogPreferences(ActionEvent ignored) {
        try {
            if (dialogPreferences == null) {
                dialogPreferences = createDialog("/DialogPreferences.fxml");
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
                dialogAbout = createDialog("/DialogAbout.fxml");
            }

            dialogAbout.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Stage createDialog(String stageFile) throws java.io.IOException {
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

    @Override
    public Window getView() {
        return root;
    }

    @Override
    public void createNewContest() {
        //TODO: implement the creation of new contest
        System.out.println("Todo: create new contest");
    }

    public ResourceBundle getBundle() {
        return bundle;
    }

}
