package alifec.simulation.controller;

import alifec.core.exception.ConfigFileWriteException;
import alifec.core.exception.CreateContestFolderException;
import alifec.core.exception.InvalidUserDirException;
import alifec.core.exception.ValidationException;
import alifec.core.persistence.ContestFileManager;
import alifec.core.persistence.config.ContestConfig;
import alifec.core.validation.NewContestFolderValidator;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
public class ContestLoaderController {
    @FXML
    public TitledPane newContestPane;
    @FXML
    public TitledPane setExistingContestPane;
    @FXML
    public RadioButton newContestRadioButton;
    @FXML
    public RadioButton setExistingContestRadioButton;
    @FXML
    public FXCollections configProperties;
    @FXML
    public TableView configPropertiesTable;
    @FXML
    public ComboBox existingContestCombobox;
    @FXML
    public CheckBox generateExamples;
    @FXML
    public TextField contestNameField;
    @FXML
    public TextField existingContestPathField;
    @FXML
    public TextField contestPathField;

    private Alert invalidConfiguration;
    private Alert createContestException;

    private Logger logger = LogManager.getLogger(getClass());
    private ContestConfig config;

    private boolean cancelledOrFailed;
    private ResourceBundle bundle;
    private Stage root;

    private NewContestFolderValidator newContestValidator;

    public ContestLoaderController() {
        this.newContestValidator = new NewContestFolderValidator();
    }


    public void newContest(ActionEvent event) {
        setCreateNewContest();
    }

    public void existingContest(ActionEvent event) {
        setExistingContest();
    }

    private void setCreateNewContest() {
        setExistingContestPane.setVisible(false);
        newContestPane.setVisible(true);

        try {
            //do an effort to set the contest path!
            contestPathField.setText(ContestConfig.getDefaultBaseAppFolder());
        } catch (InvalidUserDirException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void setExistingContest() {
        setExistingContestPane.setVisible(true);
        newContestPane.setVisible(false);

        try {
            //do an effort to set the contest path!
            existingContestPathField.setText(ContestConfig.getDefaultBaseAppFolder());
        } catch (InvalidUserDirException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public ContestConfig getUpdatedConfig() {
        return config;
    }

    public void allowCreateFileOption(List<String> contestList) {
        newContestRadioButton.setSelected(true);

        //set default selection
        setCreateNewContest();


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
        logger.info("Application loader was canceled by user.");
        ((Node) event.getSource()).getScene().getWindow().hide();
        setCancelledOrFailed(true);
    }

    public void onAccept(ActionEvent event) {
        if (newContestRadioButton.isSelected()) {
            doCreateContest(event);
        } else if (setExistingContestRadioButton.isSelected()) {
            doSetExistingContest(event);
        }
    }

    private void doSetExistingContest(ActionEvent event) {
        String contestName = existingContestCombobox.getSelectionModel().getSelectedItem().toString();

        try {
            config = new ContestConfig(bundle, contestName);
            config.save();

            logger.info("Configuration file updated successfully: " + config.toString());
            setCancelledOrFailed(false);
            ((Node) event.getSource()).getScene().getWindow().hide();

        } catch (InvalidUserDirException e) {
            logger.error("Error reading java property user.dir.");
            //todo: alert something!!
        } catch (ConfigFileWriteException e) {
            logger.error("Error while updating config file.", e);
            //todo: alert something!
        }
    }

    private void doCreateContest(ActionEvent event) {
        boolean examples = generateExamples.isSelected();
        String contestName = ContestConfig.CONTEST_NAME_PREFIX + contestNameField.getText();

        try {
            ContestConfig newConfig = new ContestConfig(bundle, contestName);
            newContestValidator.validate(newConfig.getContestPath());

            newConfig.save();
            ContestFileManager.buildNewContestFolder(newConfig, examples);

            config = newConfig;
            setCancelledOrFailed(false);
            ((Node) event.getSource()).getScene().getWindow().hide();

        } catch (ValidationException ex) {
            logger.debug(ex.getMessage(), ex);
            showDialogInvalidConfiguration(ex);
        } catch (InvalidUserDirException e) {
            logger.error(e.getMessage(), e);
            //todo: add an alert!!
        } catch (CreateContestFolderException ex) {
            logger.error(ex.getMessage(), ex);
            showDialogCreateContestException(ex);
        } catch (ConfigFileWriteException e) {
            //todo: mandar algun mensaje!!
            logger.error(e.getMessage(), e);
        }
    }

    private void showDialogCreateContestException(CreateContestFolderException e) {
        if (createContestException == null) {
            createContestException = new Alert(Alert.AlertType.ERROR);
            createContestException.setTitle(bundle.getString("Create contest failed"));
            createContestException.setHeaderText(bundle.getString("Could not create the contest"));
            createContestException.initOwner(root.getScene().getWindow());
        }

        createContestException.setContentText(e.getMessage());
        createContestException.showAndWait();
    }


    private void showDialogInvalidConfiguration(ValidationException e) {
        if (invalidConfiguration == null) {
            invalidConfiguration = new Alert(Alert.AlertType.ERROR);
            invalidConfiguration.setTitle(bundle.getString("contest.loader.validation.title"));
            invalidConfiguration.setHeaderText(bundle.getString("contest.loader.validation.header"));
            invalidConfiguration.initOwner(root.getScene().getWindow());
        }

        invalidConfiguration.setContentText(e.getMessage());
        invalidConfiguration.showAndWait();
    }

    public boolean isCancelledOrFailed() {
        return cancelledOrFailed;
    }

    public void setCancelledOrFailed(boolean status) {
        cancelledOrFailed = status;

        if (status) {
            config = null;
        }
    }

    public void keyHandler(KeyEvent event) {
        if (KeyCode.ESCAPE == event.getCode()) {
            ((Scene) event.getSource()).getWindow().hide();
        }
    }

    public void init(ResourceBundle bundle, Stage root) {
        this.bundle = bundle;
        this.root = root;

    }
}
