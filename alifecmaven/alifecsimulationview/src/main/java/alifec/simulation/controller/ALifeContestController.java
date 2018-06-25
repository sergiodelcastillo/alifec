package alifec.simulation.controller;

import alifec.core.contest.oponentInfo.ColonyStatistics;
import alifec.core.contest.oponentInfo.TournamentStatistics;
import alifec.core.persistence.config.ContestConfig;
import alifec.simulation.util.CompetitorViewComparator;
import alifec.simulation.view.CompetitorView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
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
    public ComboBox opponentsList1;

    @FXML
    public ComboBox opponentsList2;

    @FXML
    public ComboBox nutrientsList;

    @FXML
    public ListView<Parent> coloniesStatistics;

    private Stage root;

    private Stage dialogAbout;

    private Stage dialogPreferences;

    private Stage dialogStatistics;

    private Stage dialogNewContest;

    private ResourceBundle bundle;

    //contest properties
    private ContestConfig config;


    public void init(ResourceBundle bundle, Stage root, ContestConfig config) {
        this.bundle = bundle;
        this.root = root;
        this.config = config;

        loadContest(config);
        root.setResizable(false);
        root.getIcons().add(new Image("/images/logo.png"));

        try {
            //TODO
            updateStatistics(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadContest(ContestConfig config) {

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

        Stage stage = ((Controller) loader.getController()).init(this, root, getBundle());
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

    public void deleteSelectedBattles(ActionEvent event) {
        System.out.println("delete selected battles");
    }

    public void deleteAllBattles(ActionEvent event) {
        System.out.println("delete all battles");
    }

    public void runSelectedBattle(ActionEvent event) {
        System.out.println("run selected battle");
    }

    public void runAllBattles(ActionEvent event) {
        System.out.println("Run all battles");
    }

    public void addBattle(ActionEvent event) {
        System.out.println("Add one battle");
    }

    public void addAllBattles(ActionEvent event) {
        System.out.println("add all battles");
    }

    public void previousTournament(ActionEvent event) {
        System.out.println("prev tournament");
    }

    public void nextTournament(ActionEvent event) {
        System.out.println("next tournament");
    }

    public void deleteTournament(ActionEvent event) {
        System.out.println("delete tournament");
    }

    public void addTournament(ActionEvent event) {
        System.out.println("create new tournament");
    }


    public void updateStatistics(TournamentStatistics statistics) throws IOException {
        //clear all items
        coloniesStatistics.getItems().clear();

        //Create test data!!!
        if (statistics == null) {
            statistics = new TournamentStatistics();
            statistics.addWinner("yeyo", "yeyo", "yeyo", 12518f);
            statistics.addWinner("Bicho", "bbbaa", "frsf", 1258f);
            statistics.addWinner("Bacteria", "lele", "test", 5258f);
            //calculate the points
            statistics.calculate();
        }

        ObservableList<CompetitorView> list = FXCollections.observableArrayList();

        for (ColonyStatistics col : statistics.getColonyStatistics()) {
            list.addAll(new CompetitorView(col, statistics.getMaxEnergy()));
        }

        SortedList<CompetitorView> sortedList = new SortedList<>(list, new CompetitorViewComparator());

        coloniesStatistics.getItems().addAll(sortedList);

    }

}
