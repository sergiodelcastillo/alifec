package alifec.simulation.controller;

import alifec.core.exception.ConfigFileException;
import alifec.core.exception.ConfigFileWriteException;
import alifec.core.exception.InvalidUserDirException;
import alifec.core.persistence.ContestFileManager;
import alifec.core.persistence.config.ContestConfig;
import alifec.simulation.util.ConfigProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Sergio Del Castillo on 19/06/18.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class ContestLoaderController extends Controller {
    private Logger logger = LogManager.getLogger(getClass());
    @FXML
    public TitledPane updateConfigFilePane;
    @FXML
    public TitledPane newContestPane;
    @FXML
    public TitledPane setExistingContestPane;
    @FXML
    public RadioButton newContestRadioButton;
    @FXML
    public RadioButton updateConfigFileRadiobutton;
    @FXML
    public RadioButton setExistingContestRadioButton;

    @FXML
    public FXCollections configProperties;
    @FXML
    public TableView configPropertiesTable;

    @FXML
    public ComboBox existingContestCombobox;

    private Parent root;
    private MainController father;

    private ContestConfig config;

    private boolean cancelled;

    @Override
    public Stage init(MainController controller, Parent root, ResourceBundle bundle) {
        this.father = controller;
        this.root = root;

        Stage stage = buildDialog(root, bundle.getString("contest.loader.title"));

        setDefaults();

        return stage;
    }

    public void allowEditFileOption(ContestConfig config) {
        this.config = config;

        configPropertiesTable.getItems().clear();
        configPropertiesTable.getItems().add(new ConfigProperty("contest_name", config.getContestName()));
        configPropertiesTable.getItems().add(new ConfigProperty("contest_mode", config.getMode()));
        configPropertiesTable.getItems().add(new ConfigProperty("nutrients", config.getNutrients()));
        configPropertiesTable.getItems().add(new ConfigProperty("pause_between_battles", config.getPauseBetweenBattles()));

    }

    public void discardFile(ActionEvent event) {
        setDiscardFile();
    }


    public void fixFile(ActionEvent event) {
        setFixFile();
    }

    public void setExistingContest(ActionEvent event) {
        setExistingContest();
    }

    @Override
    void setDefaults() {
        setDiscardFile();
    }

    private void setDiscardFile() {
        updateConfigFilePane.setVisible(false);
        setExistingContestPane.setVisible(false);
        newContestPane.setVisible(true);
    }

    private void setFixFile() {
        updateConfigFilePane.setVisible(true);
        setExistingContestPane.setVisible(false);
        newContestPane.setVisible(false);
    }

    private void setExistingContest() {
        updateConfigFilePane.setVisible(false);
        setExistingContestPane.setVisible(true);
        newContestPane.setVisible(false);
    }

    public void editTableField(TableColumn.CellEditEvent event) {
        System.out.println("edit");
        ((ConfigProperty) event.getTableView().getItems().get(event.getTablePosition().getRow())).setContent(event.getNewValue().toString());
        System.out.println(event.getNewValue());

    }

    public ContestConfig getUpdatedConfig() {
        return config;
    }

    public void allowCreateFileOption() {
        updateConfigFileRadiobutton.setDisable(true);
        newContestRadioButton.setSelected(true);

        //set default selection
        setDiscardFile();

        //find contests
        List<String> contestList = ContestFileManager.listContest();

        if (contestList.isEmpty()) {
            setExistingContestRadioButton.setDisable(true);
        } else {
            for (String contest : contestList) {
                existingContestCombobox.getItems().add(contest);
            }
            existingContestCombobox.getSelectionModel().selectFirst();
        }
    }


    public void onCancel(ActionEvent event) {
        logger.info("Application loader was canceled");
        ((Node) event.getSource()).getScene().getWindow().hide();
        cancelled = true;
    }

    public void onAccept(ActionEvent event) {
        if (newContestRadioButton.isSelected()) {
            //create new Contest!
            System.out.println("create new contest");
        } else if (updateConfigFileRadiobutton.isSelected()) {
            //update config file
            System.out.println("update config file");
        } else if (setExistingContestRadioButton.isSelected()) {
            //set existing
            System.out.println("set existing contest");
            String contestName = existingContestCombobox.getSelectionModel().getSelectedItem().toString();

            try {
                config = new ContestConfig(contestName);
                config.save();
            } catch (InvalidUserDirException e) {
                logger.error("Error reading java property user.dir.");
                //todo: alert something!!
                return;
            } catch (ConfigFileWriteException e) {
                logger.error("Error while updating config file.", e);
                //todo: alert something!
                return;
            }
        }

        ((Node) event.getSource()).getScene().getWindow().hide();
        cancelled = false;
    }

    public boolean isCancelled() {
        return cancelled;
    }


}
