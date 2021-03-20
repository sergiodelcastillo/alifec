package alifec.simulation.controller;

import alifec.core.exception.ConfigFileException;
import alifec.core.persistence.ContestFileManager;
import alifec.core.persistence.config.ContestConfig;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Created by Sergio Del Castillo on 14/06/18.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class NewContestController {
    @FXML
    public TextField contestPath;

    @FXML
    public TextField contestName;

    @FXML
    public CheckBox loadContest;

    @FXML
    public CheckBox generateExamples;

    private ALifeContestController father;
    private Stage parent;

    public void setMainController(ALifeContestController controller) {
        this.father = controller;
    }

    public void setParentView(Stage parent) {
        this.parent = parent;

    }

    public void keyHandler(KeyEvent event) {
        if (KeyCode.ESCAPE == event.getCode()) {
            ((Scene) event.getSource()).getWindow().hide();
        }
    }

    public void setDefaults() {
        try {
            String defaultPath = ContestConfig.getDefaultPath();

            contestPath.setText(defaultPath);
            contestName.setText(ContestFileManager.getNextAvailableNameWithoutPrefix(defaultPath));
            loadContest.setSelected(true);
            generateExamples.setSelected(true);

        } catch (ConfigFileException e) {
            e.printStackTrace();
        }
    }

    public void accept(ActionEvent event) {
        //do the job
        father.createNewContest();

        ((Node) event.getSource()).getScene().getWindow().hide();
    }

    public void cancel(ActionEvent event) {
        ((Node) event.getSource()).getScene().getWindow().hide();
    }

    public void choosePath(ActionEvent event) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setInitialDirectory(new File("."));
        //todo: improve it
        File directory = chooser.showDialog(parent);
        try {
            if (Objects.nonNull(directory))
                contestPath.setText(directory.getCanonicalPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
