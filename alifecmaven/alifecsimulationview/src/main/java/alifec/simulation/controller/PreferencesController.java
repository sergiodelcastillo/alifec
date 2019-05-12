package alifec.simulation.controller;

import alifec.core.persistence.config.ContestConfig;
import alifec.core.simulation.nutrient.BallsNutrient;
import alifec.core.simulation.nutrient.function.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.WindowEvent;

import java.util.List;

/**
 * Created by Sergio Del Castillo on 14/06/18.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class PreferencesController {

    @FXML
    public ComboBox<String> competitorOptions;
    @FXML
    public ComboBox<String> pauseBetweenBattlesCombobox;
    @FXML
    public TextField contestPath;
    @FXML
    public TextField contestName;
    @FXML
    public CheckBox ballsNutrient;
    @FXML
    public CheckBox gaussiansNutrient;
    @FXML
    public CheckBox latticeNutrient;
    @FXML
    public CheckBox ringsNutrient;
    @FXML
    public CheckBox verticalBarNutrient;
    @FXML
    public CheckBox famineNutrient;
    @FXML
    public CheckBox inclinedPlaneNutrient;

    private ALifeContestController controller;
    private ContestConfig configTmp;


    public void setMainController(ALifeContestController controller) {
        this.controller = controller;
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
        controller.savePreferences();

        ((Node) event.getSource()).getScene().getWindow().hide();
    }

    public void changeContest(ActionEvent event) {
        System.out.println("change contest");
    }


    public void setPreferences(WindowEvent windowEvent) {
        configTmp = new ContestConfig(controller.getConfig());

        String name = configTmp.getContestName();
        contestName.setText(name);

        String path = configTmp.getContestPath();
        contestPath.setText(path);

        int pause = configTmp.getPauseBetweenBattles();
        pauseBetweenBattlesCombobox.getSelectionModel().select(String.valueOf(pause));

        int mode = configTmp.getMode();
        competitorOptions.getSelectionModel().select(mode);

        List<Integer> nutrients = configTmp.getNutrients();

        for (Integer nutrient : nutrients) {
            if (BallsNutrient.ID == nutrient)
                ballsNutrient.setSelected(true);
            else if (TwoGaussiansFunction.ID == nutrient)
                gaussiansNutrient.setSelected(true);
            else if (LatticeFunction.ID == nutrient)
                latticeNutrient.setSelected(true);
            else if (RingsFunction.ID == nutrient)
                ringsNutrient.setSelected(true);
            else if (VerticalBarFunction.ID == nutrient)
                verticalBarNutrient.setSelected(true);
            else if (FamineFunction.ID == nutrient)
                famineNutrient.setSelected(true);
            else if (InclinedPlaneFunction.ID == nutrient)
                inclinedPlaneNutrient.setSelected(true);
        }


        //todo: update the fields!!
    }

    public void changeBalls(ActionEvent event) {
        changeNutrient(((CheckBox) event.getSource()).isSelected(), BallsNutrient.ID);
    }

    public void changeGaussians(ActionEvent event) {
        changeNutrient(((CheckBox) event.getSource()).isSelected(), TwoGaussiansFunction.ID);
    }

    public void changeLattice(ActionEvent event) {
        changeNutrient(((CheckBox) event.getSource()).isSelected(), LatticeFunction.ID);
    }

    public void changeRings(ActionEvent event) {
        changeNutrient(((CheckBox) event.getSource()).isSelected(), RingsFunction.ID);
    }

    public void changeFamine(ActionEvent event) {
        changeNutrient(((CheckBox) event.getSource()).isSelected(), FamineFunction.ID);
    }

    public void changeVerticalBar(ActionEvent event) {
        changeNutrient(((CheckBox) event.getSource()).isSelected(), VerticalBarFunction.ID);
    }

    public void changeInclinedPlane(ActionEvent event) {
        changeNutrient(((CheckBox) event.getSource()).isSelected(), InclinedPlaneFunction.ID);
    }

    private void changeNutrient(boolean add, Integer id) {
        if (add) configTmp.getNutrients().add(id);
        else configTmp.getNutrients().remove(id);
    }

    public void changeToJava2D(ActionEvent event) {

    }
}
