package alifec.simulation.controller;

import alifec.core.persistence.config.ContestConfig;
import alifec.simulation.util.ConfigProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.ResourceBundle;

/**
 * Created by Sergio Del Castillo on 19/06/18.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class ContestLoaderController extends Controller {
    @FXML
    public TitledPane fixFilePane;
    @FXML
    public TitledPane discardFilePane;
    @FXML
    public RadioButton createFile;
    @FXML
    public RadioButton fixFile;

    @FXML
    public FXCollections configProperties;
    @FXML
    public TableView configPropertiesTable;

    private Parent root;
    private MainController father;

    private ContestConfig config;

    @Override
    public Stage init(MainController controller, Parent root, ResourceBundle bundle) {
        this.father = controller;
        this.root = root;

        Stage stage = buildDialog(root, bundle.getString("contest.loader.title"));

        setDefaults();

        return stage;
    }

    public void setContestConfig(ContestConfig config) {
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

    @Override
    void setDefaults() {
        setDiscardFile();
    }

    private void setDiscardFile() {
        fixFilePane.setVisible(false);
        discardFilePane.setVisible(true);
    }

    private void setFixFile() {
        fixFilePane.setVisible(true);
        discardFilePane.setVisible(false);
    }


    public void editTableField(TableColumn.CellEditEvent event) {
        System.out.println("edit");
        ((ConfigProperty) event.getTableView().getItems().get(event.getTablePosition().getRow())).setContent(event.getNewValue().toString());
        System.out.println(event.getNewValue());

    }

    public ContestConfig getConfig() {
        return config;
    }

    public void disableEditFile() {
        fixFile.setDisable(true);
        createFile.setSelected(true);

        setDiscardFile();
    }
}
