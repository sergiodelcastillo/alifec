package alifec.simulation.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.util.ResourceBundle;

/**
 * Created by Sergio Del Castillo on 25/06/18.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class CompilationErrorController {

    @FXML
    public TextArea textArea;

    public void setText(String text) {
        this.textArea.setText(text);
    }

    public void close(ActionEvent event) {
        ((Node) event.getSource()).getScene().getWindow().hide();
    }
}
