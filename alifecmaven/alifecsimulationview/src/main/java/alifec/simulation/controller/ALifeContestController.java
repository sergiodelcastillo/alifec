package alifec.simulation.controller;

import alifec.core.contest.Battle;
import alifec.core.contest.Contest;
import alifec.core.contest.oponentInfo.ColonyStatistics;
import alifec.core.contest.oponentInfo.TournamentStatistics;
import alifec.core.exception.BattleException;
import alifec.core.exception.CreateContestException;
import alifec.core.exception.ValidationException;
import alifec.core.persistence.config.ContestConfig;
import alifec.core.simulation.Competitor;
import alifec.core.simulation.NutrientDistribution;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
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
    public ComboBox<Competitor> opponentsList1;
    @FXML
    public ComboBox<Competitor> opponentsList2;
    @FXML
    public ComboBox<NutrientDistribution> nutrientsList;
    @FXML
    public ListView<Parent> coloniesStatistics;
    @FXML
    public TitledPane tournamentPanel;
    @FXML
    public ListView<Battle> battleList;
    private Logger logger = LogManager.getLogger(ALifeContestController.class.getName());
    private Stage root;

    private Stage dialogAbout;

    private Stage dialogPreferences;

    private Stage dialogStatistics;

    private Stage dialogNewContest;

    private ResourceBundle bundle;

    //contest properties
    private ContestConfig config;
    private Contest contest;

    private Alert existingBattleAlert;
    private Alert createBattleAlert;
    private Alert duplicatedBattlesDecision;

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
            logger.error(e.getMessage(), e);
        }
    }

    private void initBattlePanel(Contest contest) {
        opponentsList1.getItems().setAll(contest.getEnvironment().getCompetitors());
        opponentsList1.getSelectionModel().selectFirst();

        opponentsList2.getItems().setAll(contest.getEnvironment().getCompetitors());
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
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            ;
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
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
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
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void showDialogHelp(ActionEvent ignored) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(bundle.getString("ALifeContestController.help.title"));
        alert.setHeaderText(bundle.getString("ALifeContestController.help.header"));
        alert.setContentText(bundle.getString("ALifeContestController.help.contentText"));
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
            logger.error(e.getMessage(), e);
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


    void createNewContest() {
        //TODO: implement the creation of new contest
        System.out.println("Todo: create new contest");
    }

    public ResourceBundle getBundle() {
        return bundle;
    }

    public void deleteSelectedBattles(ActionEvent event) {
        int index = battleList.getSelectionModel().getSelectedIndex();

        if (index > -1)
            battleList.getItems().remove(index);

    }

    public void deleteAllBattles() {
        battleList.getItems().clear();
    }

    public void runSelectedBattle(ActionEvent event) {
        System.out.println("run selected battle");
    }

    public void runAllBattles(ActionEvent event) {
        System.out.println("Run all battles");
    }

    public void addBattle() {
        //todo: alertar cuando es una batalla duplicada!!!
        try {
            Battle battle = new Battle(opponentsList1.getSelectionModel().getSelectedItem(),
                    opponentsList2.getSelectionModel().getSelectedItem(),
                    nutrientsList.getSelectionModel().getSelectedItem());

            if (config.isCompetitionMode()) {
                if (battleList.getItems().contains(battle)) {
                    showExistingBattleAlert();
                } else {
                    battleList.getItems().add(battle);
                }
            } else {
                battleList.getItems().add(battle);
            }
        } catch (BattleException e) {
            logger.error(e.getMessage(), e);

            showCreateBattleAlert(e);
        }
    }

    /*
    boolean existingBattle = false;
            boolean option = father.getContest().getMode() == ContestConfig.PROGRAMMER_MODE;
            List<Battle> list = contest.lastTournament().generateMissingBattles(opponents, nutrients, option);

            for (Battle battle : list) {
                if (!addBattle(battle, option)) {
                    existingBattle = true;
                }

                if (list.size() == 0 && existingBattle)
                    Message.printErr(this, "Battle/s already run.");
            }
    * */
    public void addAllBattles() {
        boolean duplicate = config.isProgrammerMode();

        if (config.isProgrammerMode()) {
            if (!battleList.getItems().isEmpty() || !contest.lastTournament().getBattles().isEmpty()) {
                Optional<ButtonType> optional = showDuplicatedBattlesDecision();

                duplicate = !optional.get().getButtonData().isCancelButton();
            }
        }
        List<Battle> list = contest.lastTournament().generateMissingBattles(
                opponentsList1.getItems(),
                nutrientsList.getItems(),
                duplicate);

        if (!duplicate) {
            //remove battles which are pending to run.
            list.removeAll(battleList.getItems());
        }

        battleList.getItems().addAll(list);
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
        tournamentPanel.setText(contest.lastTournament().getName());
        coloniesStatistics.getItems().clear();
        TournamentStatistics statistics = contest.lastTournament().getTournamentStatistics();

        ObservableList<CompetitorView> list = FXCollections.observableArrayList();

        for (ColonyStatistics col : statistics.getColonyStatistics()) {
            list.addAll(new CompetitorView(col, statistics.getMaxEnergy()));
        }

        SortedList<CompetitorView> sortedList = new SortedList<>(list, new CompetitorViewComparator());

        coloniesStatistics.getItems().addAll(sortedList);
    }

    private Optional<ButtonType> showDuplicatedBattlesDecision() {
        if (duplicatedBattlesDecision == null) {
            duplicatedBattlesDecision = new Alert(Alert.AlertType.CONFIRMATION);
            duplicatedBattlesDecision.setTitle(bundle.getString("ALifeContestController.duplicated.battles.alert.title"));
            duplicatedBattlesDecision.setHeaderText(bundle.getString("ALifeContestController.duplicated.battles.alert.header"));
            duplicatedBattlesDecision.setContentText(bundle.getString("ALifeContestController.duplicated.battles.alert.content"));
            duplicatedBattlesDecision.initOwner(mainLayout.getScene().getWindow());
        }
        return duplicatedBattlesDecision.showAndWait();
    }

    private void showCreateBattleAlert(BattleException ex) {
        if (createBattleAlert == null) {
            createBattleAlert = new Alert(Alert.AlertType.ERROR);
            createBattleAlert.setTitle(bundle.getString("ALifeContestController.create.battle.alert.title"));
            createBattleAlert.setHeaderText(bundle.getString("ALifeContestController.create.battle.alert.header"));
            createBattleAlert.initOwner(mainLayout.getScene().getWindow());
        }

        if (ex.getCause() instanceof ValidationException) {
            //todo: improve translation
            createBattleAlert.setContentText(ex.getCause().getMessage());
        } else {
            createBattleAlert.setContentText(bundle.getString("ALifeContestController.create.battle.alert.content"));
        }
        createBattleAlert.showAndWait();
    }

    private void showExistingBattleAlert() {
        if (existingBattleAlert == null) {
            existingBattleAlert = new Alert(Alert.AlertType.ERROR);
            existingBattleAlert.setTitle(bundle.getString("ALifeContestController.existing.battle.alert.title"));
            existingBattleAlert.setHeaderText(bundle.getString("ALifeContestController.existing.battle.alert.header"));
            existingBattleAlert.setContentText(bundle.getString("ALifeContestController.existing.battle.alert.content"));
            existingBattleAlert.initOwner(mainLayout.getScene().getWindow());
        }
        existingBattleAlert.showAndWait();
    }
}
