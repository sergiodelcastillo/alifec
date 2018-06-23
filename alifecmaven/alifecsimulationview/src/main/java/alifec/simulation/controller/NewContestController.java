package alifec.simulation.controller;

import alifec.core.exception.ConfigFileException;
import alifec.core.persistence.ContestFileManager;
import alifec.core.persistence.config.ContestConfig;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Year;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * Created by Sergio Del Castillo on 14/06/18.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class NewContestController extends Controller {
    @FXML
    public TextField contestPath;

    @FXML
    public TextField contestName;

    @FXML
    public CheckBox loadContest;

    @FXML
    public CheckBox generateExamples;

    private MainController father;
    private Parent root;

    @Override
    public Stage init(MainController controller, Parent root, ResourceBundle bundle) {
        this.father = controller;
        this.root = root;

        Stage stage = buildDialog(root, bundle.getString("newcontest.title"));

        //reset the values when the interface is displayed
        stage.setOnShown(event -> setDefaults());
        stage.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent e) -> {
            if (KeyCode.ESCAPE == e.getCode()) {
                stage.close();
            }
        });
        return stage;
    }

    @Override
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
        File directory = chooser.showDialog(root.getScene().getWindow());
        try {
            if (directory != null)
                contestPath.setText(directory.getCanonicalPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
