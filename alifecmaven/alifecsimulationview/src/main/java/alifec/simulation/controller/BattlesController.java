package alifec.simulation.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

/**
 * Created by Sergio Del Castillo on 17/06/18.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class BattlesController {

    @FXML
    public ComboBox opponentsList1;

    @FXML
    public ComboBox opponentsList2;

    @FXML
    public ComboBox nutrientsList;

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
}
