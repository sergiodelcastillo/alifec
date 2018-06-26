package alifec.simulation.controller;

import alifec.core.contest.Contest;
import alifec.core.contest.oponentInfo.ColonyStatistics;
import alifec.core.contest.oponentInfo.TournamentStatistics;
import alifec.core.exception.CreateContestException;
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

import java.io.IOException;
import java.util.ResourceBundle;

/**
 * Created by Sergio Del Castillo on 10/06/18.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class ALifeContestController {
    @FXML
    public BorderPane mainLayout;

    @FXML
    public Label messagePanel;

    @FXML
    public ComboBox<String> opponentsList1;

    @FXML
    public ComboBox<String> opponentsList2;

    @FXML
    public ComboBox<alifec.core.simulation.NutrientDistribution> nutrientsList;

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
    private Contest contest;

    public void init(ResourceBundle bundle, Stage root, ContestConfig config) throws CreateContestException {
        this.bundle = bundle;
        this.root = root;
        this.config = config;

        root.setResizable(false);
        root.getIcons().add(new Image("/images/logo.png"));

        contest = loadContest(config);

        try {
            //TODO implement this part
            initStatisticsPanel(contest);
            initBattlePanel(contest);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initBattlePanel(Contest contest) {
        opponentsList1.getItems().setAll(contest.getEnvironment().getOpponentNames());
        opponentsList1.getSelectionModel().selectFirst();

        opponentsList2.getItems().setAll(contest.getEnvironment().getOpponentNames());
        opponentsList2.getSelectionModel().selectLast();

        nutrientsList.getItems().setAll(contest.getCurrentNutrients());
        nutrientsList.getSelectionModel().selectFirst();
    }

    private Contest loadContest(ContestConfig config) throws CreateContestException {

        //create an instance of the contest
        Contest contest = new Contest(config);

        /* todo: implement this part!!
        Battle failed = contest.getUnsuccessfulBattle();

        if (failed != null) {
            UnsuccessfulColoniesSolverUI solver = new UnsuccessfulColoniesSolverUI(this, failed.getFirstColony(), failed.getSecondColony());
            solver.setVisible(true);

            //remove the colony which was excluded from the Dialog "UnsuccessfulColoniesSolverUI".
            for (String excludedColony : excluded) {
                contest.getEnvironment().delete(excludedColony);
            }
        }
        * */
        return contest;
    }

    public void newContest(ActionEvent ignored) {
        try {
            if (dialogNewContest == null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/DialogNewContest.fxml"), bundle);
                dialogNewContest = loader.load();
                dialogNewContest.initOwner(mainLayout.getScene().getWindow());

                //set specific data!!
                NewContestController controller = loader.getController();
                controller.setMainController(this);
                controller.setParentView(root);
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
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/DialogStatistics.fxml"), bundle);
                dialogStatistics = loader.load();
                dialogStatistics.initOwner(mainLayout.getScene().getWindow());

                //set specific data!!
                StatisticsController controller = loader.getController();
                controller.setMainController(this);
            }

            dialogStatistics.showAndWait();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void showDialogPreferences(ActionEvent ignored) {
        try {
            if (dialogPreferences == null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/DialogPreferences.fxml"), bundle);
                dialogPreferences = loader.load();
                dialogPreferences.initOwner(mainLayout.getScene().getWindow());

                //set specific data!!
                PreferencesController controller = loader.getController();
                controller.setMainController(this);
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
        alert.initOwner(mainLayout.getScene().getWindow());

        alert.showAndWait();
    }

    public void showDialogAbout(ActionEvent ignored) {
        try {
            if (dialogAbout == null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/DialogAbout.fxml"), bundle);
                dialogAbout = loader.load();
                dialogAbout.initOwner(mainLayout.getScene().getWindow());
            }

            dialogAbout.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void savePreferences() {
        System.out.println("update data");
    }

    public void createReportTxt() {
        System.out.println("update data txt");
    }

    public void createReportCsv() {
        System.out.println("update data csv");
    }


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


    public void initStatisticsPanel(Contest contest) throws IOException {
        //clear all items
        coloniesStatistics.getItems().clear();
        TournamentStatistics statistics = contest.lastTournament().getTournamentStatistics();

        //Create test data!!!
        /*if (statistics == null) {
            statistics = new TournamentStatistics();
            statistics.addWinner("yeyo", "yeyo", "yeyo", 12518f);
            statistics.addWinner("Bicho", "bbbaa", "frsf", 1258f);
            statistics.addWinner("Bacteria", "lele", "test", 5258f);
            //calculate the points
            statistics.calculate();
        }*/

        ObservableList<CompetitorView> list = FXCollections.observableArrayList();

        for (ColonyStatistics col : statistics.getColonyStatistics()) {
            list.addAll(new CompetitorView(col, statistics.getMaxEnergy()));
        }

        SortedList<CompetitorView> sortedList = new SortedList<>(list, new CompetitorViewComparator());

        coloniesStatistics.getItems().addAll(sortedList);
    }

}
