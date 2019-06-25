package alifec.simulation.controller;

import alifec.core.contest.Battle;
import alifec.core.contest.Contest;
import alifec.core.contest.Tournament;
import alifec.core.contest.oponentInfo.ColonyStatistics;
import alifec.core.contest.oponentInfo.TournamentStatistics;
import alifec.core.event.Event;
import alifec.core.event.EventBus;
import alifec.core.event.Listener;
import alifec.core.event.impl.BattleEvent;
import alifec.core.exception.*;
import alifec.core.persistence.config.ContestConfig;
import alifec.core.simulation.Competitor;
import alifec.core.simulation.NutrientDistribution;
import alifec.simulation.simulation.ALifeContestSimulationView;
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

import java.text.MessageFormat;
import java.util.*;

/**
 * Created by Sergio Del Castillo on 10/06/18.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class ALifeContestController implements Listener {
    @FXML
    public BorderPane mainLayout;
    @FXML
    public Label messagePanel;
    @FXML
    public ComboBox<Competitor> opponentsList1;
    @FXML
    public ComboBox<Competitor> opponentsList2;
    @FXML
    public ComboBox<KeyBasedModel> nutrientsList;
    @FXML
    public ListView<Parent> coloniesStatistics;
    @FXML
    public TitledPane tournamentPanel;
    @FXML
    public ListView<Battle> battleList;
    @FXML
    public Button deletedSelectedBattlesButton;
    @FXML
    public Button deleteAllBattlesButton;
    @FXML
    public Button runSelectedBattleButton;
    @FXML
    public Button runAllBattlesButton;
    @FXML
    public Button addBattleButton;
    @FXML
    public Button addAllBattlesButton;

    private Logger logger = LogManager.getLogger(ALifeContestController.class.getName());
    private Stage root;

    private Stage dialogAbout;

    private Stage dialogPreferences;

    private Stage dialogStatistics;

    private Stage dialogNewContest;

    private ALifeContestSimulationView dialogSimulation;

    private ResourceBundle bundle;

    //contest properties
    private ContestConfig config;
    private Contest contest;

    private List<KeyBasedModel> nutrients;

    private Alert existingBattleAlert;
    private Alert createBattleAlert;
    private Alert duplicatedBattlesDecision;
    private Alert help;
    private Alert simulationException;
    private Alert notSupportedYet;


    public void init(ResourceBundle bundle, Stage root, ContestConfig config) throws CreateContestException {
        this.bundle = bundle;
        this.root = root;
        this.config = config;
        nutrients = new ArrayList<>();

        root.setResizable(false);
        root.getIcons().add(new Image(getClass().getResource("/images/logo.png").toExternalForm()));

        contest = loadContest(config);


        //TODO implement this part
        updateRanking(contest.lastTournament());
        initBattlePanel(contest);

        battleList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            boolean selection = (newValue == null);

            deletedSelectedBattlesButton.setDisable(selection);
            runSelectedBattleButton.setDisable(selection);
        });

        EventBus.register(this);
    }

    private void initBattlePanel(Contest contest) {
        opponentsList1.getItems().setAll(contest.getEnvironment().getCompetitors());
        opponentsList1.getSelectionModel().selectFirst();

        opponentsList2.getItems().setAll(contest.getEnvironment().getCompetitors());
        opponentsList2.getSelectionModel().selectLast();

        updateNutrientsList();
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

        }
    }

    public void quit(ActionEvent event) {
        logger.info("Good bye!.");

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
        if (help == null) {
            help = new Alert(Alert.AlertType.INFORMATION);
            help.setTitle(bundle.getString("ALifeContestController.help.title"));
            help.setHeaderText(bundle.getString("ALifeContestController.help.header"));
            help.setContentText(bundle.getString("ALifeContestController.help.contentText"));
            help.initOwner(mainLayout.getScene().getWindow());
        }

        help.showAndWait();
    }

    private void showDialogSimulationException(ValidationException ex) {
        //todo: improve the alert in order to show the exception in the alert.
        if (simulationException == null) {
            simulationException = new Alert(Alert.AlertType.ERROR);
            simulationException.setTitle(bundle.getString("ALifeContestController.simulation.title"));
            simulationException.setHeaderText(bundle.getString("ALifeContestController.simulation.header"));
            simulationException.setContentText(bundle.getString("ALifeContestController.simulation.contentText"));
            simulationException.initOwner(mainLayout.getScene().getWindow());
        }

        simulationException.showAndWait();
    }

    public void showDialogNotSupportedYet() {

        if (notSupportedYet == null) {
            notSupportedYet = new Alert(Alert.AlertType.INFORMATION);
            notSupportedYet.setTitle(bundle.getString("ALifeContestController.notSupported.title"));
            notSupportedYet.setHeaderText(bundle.getString("ALifeContestController.notSupported.header"));
            notSupportedYet.setContentText(bundle.getString("ALifeContestController.notSupported.contentText"));
            notSupportedYet.initOwner(mainLayout.getScene().getWindow());
        }

        notSupportedYet.showAndWait();
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


    public void savePreferences(ContestConfig c, boolean nutrientsUpdated) throws ConfigFileWriteException, ValidationException {
        //validate
        //asegurarse que hayan mas de un nutriente.
        if (nutrientsUpdated) {
            if (c.getNutrients().isEmpty())
                //todo: add translation -- use boundle
                throw new ValidationException("Please select one or more nutrients.");
        }

        //update the configuration
        contest.updateConfigFile(c);

        //point to the new configuration
        config = contest.getConfig();

        if (nutrientsUpdated) {
            //update the nutrients page
            updateNutrientsList();
        }

    }

    private void updateNutrientsList() {
        nutrients.clear();
        for (Integer nutrientId : config.getNutrients()) {
            nutrients.add(new KeyBasedModel(nutrientId, bundle.getString("nutrient." + nutrientId)));
        }
        nutrientsList.getItems().setAll(nutrients);
        nutrientsList.getSelectionModel().selectFirst();
    }

    /*
      public List<NutrientDistribution> getCurrentNutrients() {
        List<NutrientDistribution> list = new ArrayList<>();

        List<Integer> current = config.getNutrients();
        Hashtable<Integer, Nutrient> allNutrients = ContestConfig.nutrientOptions();

        for (int nutrientId : current) {
            list.add(new NutrientDistribution(nutrientId, allNutrients.get(nutrientId).getName()));

        }

        return list;
    * */
    public void createReportTxt() {
        //TODO Implement it!!
        showDialogNotSupportedYet();
    }

    public void createReportCsv() {
        //TODO Implement it!!
        showDialogNotSupportedYet();
    }


    void createNewContest() {
        //TODO Implement it!!
        showDialogNotSupportedYet();
    }

    public ResourceBundle getBundle() {
        return bundle;
    }

    public void deleteSelectedBattles(ActionEvent event) {
        int index = battleList.getSelectionModel().getSelectedIndex();

        if (index > -1)
            battleList.getItems().remove(index);

        if (battleList.getItems().isEmpty()) {
            setDisableBattleButtons(true);
        }

    }

    public void deleteAllBattles() {
        battleList.getItems().clear();

        setDisableBattleButtons(true);
    }

    public void runSelectedBattle(ActionEvent event) {
        Battle battle = battleList.getSelectionModel().getSelectedItem();

        if (dialogSimulation == null) {
            dialogSimulation = new ALifeContestSimulationView(mainLayout, contest, bundle);
        }

        try {
            dialogSimulation.simulate(Collections.singletonList(battle));
        } catch (ValidationException e) {
            //todo: add translation
            logger.error("Error to set or run the simulation.", e);
            showDialogSimulationException(e);
        }
    }

    public void runAllBattles(ActionEvent event) {
        List<Battle> battles = battleList.getItems();

        if (dialogSimulation == null) {
            dialogSimulation = new ALifeContestSimulationView(mainLayout, contest, bundle);
        }

        try {
            dialogSimulation.simulate(battles);
        } catch (ValidationException e) {
            logger.error("Error to set or run the simulation.", e);
            showDialogSimulationException(e);
        }
    }

    public void addBattle() {
        //todo: alertar cuando es una batalla duplicada!
        try {
            Integer nutrientId = nutrientsList.getSelectionModel().getSelectedItem().getKey();
            NutrientDistribution nutrient = new NutrientDistribution(nutrientId, bundle.getString("nutrient." + nutrientId));

            Battle battle = new Battle(opponentsList1.getSelectionModel().getSelectedItem(),
                    opponentsList2.getSelectionModel().getSelectedItem(),
                    nutrient);

            if (config.isCompetitionMode()) {
                if (battleList.getItems().contains(battle)) {
                    showExistingBattleAlert();
                } else {
                    battleList.getItems().add(battle);
                }
            } else {
                battleList.getItems().add(battle);
            }

            //ensure that battle buttons are enabled.
            setDisableBattleButtons(false);
            if (battleList.getItems().size() == 1) {
                battleList.getSelectionModel().selectFirst();
            }
        } catch (BattleException e) {
            logger.error(e.getMessage(), e);

            showCreateBattleAlert(e);
        }
    }

    private void setDisableBattleButtons(boolean value) {
        runAllBattlesButton.setDisable(value);
        runSelectedBattleButton.setDisable(value);
        deleteAllBattlesButton.setDisable(value);
        deletedSelectedBattlesButton.setDisable(value);
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
                if (optional.isPresent())
                    duplicate = !optional.get().getButtonData().isCancelButton();
            }
        }

        //todo: improve this. It is not necessary to use Nutrientdistribution
        List<KeyBasedModel> nutrientsKeyList = nutrientsList.getItems();
        List<NutrientDistribution> nutrientDistributionList = new ArrayList<>();

        for (KeyBasedModel key : nutrientsKeyList) {
            nutrientDistributionList.add(new NutrientDistribution(key.getKey(), bundle.getString("nutrient." + key.getKey())));
        }

        List<Battle> list = contest.lastTournament().generateMissingBattles(
                opponentsList1.getItems(),
                nutrientDistributionList,
                duplicate);

        if (!duplicate) {
            //remove battles which are pending to run.
            list.removeAll(battleList.getItems());
        }


        battleList.getItems().addAll(list);

        //ensure that battle buttons are enabled.
        setDisableBattleButtons(false);
        battleList.getSelectionModel().selectFirst();
    }


    public void previousTournament(ActionEvent event) {
        updateRanking(contest.prev());
    }

    public void nextTournament(ActionEvent event) {
        updateRanking(contest.next());
    }

    public void deleteTournament(ActionEvent event) {
        contest.removeSelected(); //TODO: assert result == true
        updateRanking(contest.getSelected());
    }

    public void addTournament(ActionEvent event) {
        try {
            contest.newTournament();
            updateRanking(contest.lastTournament());
        } catch (TournamentException e) {
            showDialogSimulationException(null);
        }
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

    public ContestConfig getConfig() {
        return config;
    }

    public BorderPane getMainLayout() {
        return mainLayout;
    }

    @Override
    public void handle(Event event) {
        if (event instanceof BattleEvent) {
            BattleEvent battleEvent = (BattleEvent) event;
            if (battleEvent.getStatus() == BattleEvent.Status.FINISH) {
                Battle battle = battleEvent.getBattle();
                String pattern = bundle.getString("alifec.battle.status." + battleEvent.getStatus().name());
                String message = MessageFormat.format(pattern, battle.getWinnerName(), battle.getWinnerEnergy());
                updateMessagePanel(message);
                updateRanking(battleEvent);
            }
        }
    }

    private void updateMessagePanel(String message) {
        Platform.runLater(() -> {
            messagePanel.setText(message);
        });
    }

    private void updateRanking(BattleEvent event) {
        Tournament tournament = contest.lastTournament();
        tournament.addBattle(event.getBattle());
        updateRanking(tournament);
    }

    private void updateRanking(Tournament tournament) {
        //todo: improve this!!!
        //maybe the best way is to have panels per tournament (and show only the selected one)
        //and update the list of opponents by sorting the current list by energy or points.
        //check if Platform runLater is neccessary
        Platform.runLater(() -> {
            tournamentPanel.setText(tournament.getName());
            TournamentStatistics statistics = tournament.getStatistics();
            ObservableList<CompetitorView> list = FXCollections.observableArrayList();

            for (ColonyStatistics col : statistics.getColonyStatistics()) {
                list.addAll(new CompetitorView(col, statistics.getMaxEnergy(), bundle));
            }

            SortedList<CompetitorView> sortedList = new SortedList<>(list, new CompetitorViewComparator());

            coloniesStatistics.getItems().setAll(sortedList);

        });
    }
}
