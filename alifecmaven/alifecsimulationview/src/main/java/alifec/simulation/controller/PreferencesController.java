package alifec.simulation.controller;

import alifec.core.exception.ConfigFileWriteException;
import alifec.core.exception.ValidationException;
import alifec.core.persistence.config.ContestConfig;
import alifec.core.simulation.nutrient.Nutrient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.WindowEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * Created by Sergio Del Castillo on 14/06/18.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class PreferencesController {
    @FXML
    public ComboBox<KeyBasedModel> contestModeCombobox;
    @FXML
    public ComboBox<String> pauseBetweenBattlesCombobox;
    @FXML
    public TextField contestPath;
    @FXML
    public TextField contestName;
    @FXML
    public GridPane nutrientsPane;


    private Logger logger = LogManager.getLogger(PreferencesController.class.getName());
    private ALifeContestController controller;
    private ContestConfig configTmp;
    private boolean nutrientsUpdated;
    private boolean pauseBetweenBattlesUpdated;
    private boolean contestModeUpdated;

    private Alert invalidConfiguration;
    private Alert saveError;
    private ResourceBundle bundle;

    private List<KeyBasedModel> modes;
    private Hashtable<Integer, CheckBox> nutrientsCheckboxes;
    private boolean ignoreNutrientsModification;

    public void setMainController(ALifeContestController controller) {
        this.controller = controller;
        this.bundle = controller.getBundle();

        modes = new ArrayList<>();
        modes.add(new KeyBasedModel(ContestConfig.PROGRAMMER_MODE, bundle.getString("PreferencesController.programmerMode")));
        modes.add(new KeyBasedModel(ContestConfig.COMPETITION_MODE, bundle.getString("PreferencesController.competitionMode")));

        nutrientsCheckboxes = new Hashtable<>();

        int row = 0, column = 0, MAX_COLUMN = 4;

        for (Nutrient nut : ContestConfig.nutrientOptions().values()) {
            CheckBox checkbox = new CheckBox(bundle.getString("nutrient." + nut.getId()));
            checkbox.selectedProperty().addListener((observable, oldValue, newValue) -> changeNutrient(newValue, nut.getId()));
            nutrientsCheckboxes.put(nut.getId(), checkbox);
            nutrientsPane.add(checkbox, column, row);

            column++;
            if (column >= MAX_COLUMN) {
                column = 0;
                row++;
            }
        }
    }


    public void keyHandler(KeyEvent event) {
        if (KeyCode.ESCAPE == event.getCode()) {
            ((Scene) event.getSource()).getWindow().hide();
        }
    }

    public void cancel(ActionEvent event) {
        ((Node) event.getSource()).getScene().getWindow().hide();
    }

    public void accept(ActionEvent event) {
        //DO THE WORK!!
        if (nutrientsUpdated || pauseBetweenBattlesUpdated || contestModeUpdated) {
            try {
                controller.savePreferences(configTmp, nutrientsUpdated);

                ((Node) event.getSource()).getScene().getWindow().hide();

                //release the config reference.
                configTmp = null;
            } catch (ConfigFileWriteException e) {
                logger.error(e.getMessage(), e);
                showDialogSaveError(e);
            } catch (ValidationException e) {
                logger.error(e.getMessage(), e);
                showDialogInvalidConfiguration(e);
            }
        }
    }

    public void changeContest(ActionEvent event) {
        controller.showDialogNotSupportedYet();
    }


    /**
     * Update the preferences dialog before showing it.
     *
     * @param windowEvent
     */
    public void setPreferences(WindowEvent windowEvent) {
        configTmp = new ContestConfig(controller.getConfig());

        String name = configTmp.getContestName();
        contestName.setText(name);

        String path = configTmp.getContestPath();
        contestPath.setText(path);

        // load the list of options to set pause between battle and set the current configuration
        int pause = configTmp.getPauseBetweenBattles();
        pauseBetweenBattlesCombobox.getItems().setAll(ContestConfig.pauseBetweenBattlesOptions());
        pauseBetweenBattlesCombobox.getSelectionModel().select(String.valueOf(pause));

        //load the list of options to set modes and set the current mode.
        int mode = configTmp.getMode();
        contestModeCombobox.getItems().setAll(modes);
        contestModeCombobox.getSelectionModel().select(mode);


        //the variable ignoreNutrientsModification allows to skip the change in the
        //event handler during the configuration of the current status.
        ignoreNutrientsModification = true;
        for (CheckBox nutrient : nutrientsCheckboxes.values()) {
            nutrient.setSelected(false);
        }
        for (Integer nutrient : configTmp.getNutrients()) {
            nutrientsCheckboxes.get(nutrient).setSelected(true);
        }
        ignoreNutrientsModification = false;

        nutrientsUpdated = false;
        pauseBetweenBattlesUpdated = false;
        contestModeUpdated = false;
    }

    private void changeNutrient(boolean add, Integer id) {
        if (ignoreNutrientsModification) return;

        nutrientsUpdated = true;
        if (add) configTmp.getNutrients().add(id);
        else configTmp.getNutrients().remove(id);
    }

    public void changePauseBetweenBattles(ActionEvent event) {
        String selected = pauseBetweenBattlesCombobox.getSelectionModel().getSelectedItem();

        if (Objects.isNull(selected)) return;

        configTmp.setPauseBetweenBattles(Integer.valueOf(selected));

        pauseBetweenBattlesUpdated = true;
    }

    public void changeContestMode(ActionEvent event) {
        KeyBasedModel selectedItem = contestModeCombobox.getSelectionModel().getSelectedItem();

        if (Objects.isNull(selectedItem)) return;

        configTmp.setMode(selectedItem.getKey());
        contestModeUpdated = true;
    }

    private void showDialogInvalidConfiguration(ValidationException e) {
        if (Objects.isNull(invalidConfiguration)) {
            invalidConfiguration = new Alert(Alert.AlertType.ERROR);
            invalidConfiguration.setTitle(bundle.getString("PreferencesController.InvalidConfiguration.title"));
            invalidConfiguration.setHeaderText(bundle.getString("PreferencesController.InvalidConfiguration.header"));
            invalidConfiguration.initOwner(controller.getMainLayout().getScene().getWindow());
        }

        invalidConfiguration.setContentText(e.getMessage());
        invalidConfiguration.showAndWait();
    }

    private void showDialogSaveError(ConfigFileWriteException e) {
        if (Objects.isNull(saveError)) {
            saveError = new Alert(Alert.AlertType.ERROR);
            saveError.setTitle(bundle.getString("PreferencesController.saveError.title"));
            saveError.setHeaderText(bundle.getString("PreferencesController.saveError.header"));
            saveError.initOwner(controller.getMainLayout().getScene().getWindow());
        }

        saveError.setContentText(e.getMessage());
        saveError.showAndWait();
    }

}
