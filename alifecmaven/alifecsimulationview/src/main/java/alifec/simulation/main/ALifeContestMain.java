package alifec.simulation.main;

import alifec.core.compilation.CompilationResult;
import alifec.core.compilation.CompileHelper;
import alifec.core.exception.ConfigFileException;
import alifec.core.exception.InvalidUserDirException;
import alifec.core.exception.ValidationException;
import alifec.core.persistence.config.ContestConfig;
import alifec.simulation.controller.ALifeContestController;
import alifec.simulation.controller.ContestLoaderController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * Created by Sergio Del Castillo on 14/06/18.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class ALifeContestMain extends Application {
    private Logger logger = LogManager.getLogger(ALifeContestMain.class.getName());
    private final ResourceBundle bundle;

    static {
        //it have to be static because other class could have an static logger.
        if (System.getProperty("log4j.configurationFile") == null) {
            System.setProperty("log4j.configurationFile", "file:app/log4j2.xml");
        }
    }

    public static void main(String[] args) {

        // launch the application
        Application.launch(ALifeContestMain.class, args);
    }


    public ALifeContestMain() {
        logProperties();

        Locale currentLocale = Locale.ENGLISH;
        //TODO: set the default locale from configuration or load english instead.

        bundle = ResourceBundle.getBundle("i18n/messages", currentLocale);
        System.out.println("Init constructor application");
    }

    private void logProperties() {
        if (logger.isTraceEnabled()) {
            logger.trace("System Properties:");

            Properties p = System.getProperties();
            Enumeration keys = p.keys();
            while (keys.hasMoreElements()) {
                String key = (String) keys.nextElement();
                String value = (String) p.get(key);
                logger.trace(" < " + key + ": " + value + " >");
            }
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        //load and existing contest or create a new one.
        ContestConfig config = loadContest();

        if (config == null) {
            logger.warn("The contest file was not loaded. Application wont start.");
            Platform.exit();
        } else {
            //compile the colonies
            compileColonies(config);

            //initialize the UI
            stage.setTitle(bundle.getString("ALifeContestMain.title"));
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ALifeContestView.fxml"), bundle);
            Parent root = loader.load();

            ((ALifeContestController) loader.getController()).init(bundle, stage, config);

            stage.setScene(new Scene(root));
            stage.show();
        }
    }

    private void compileColonies(ContestConfig config) {
        CompileHelper compiler = new CompileHelper(config);
        CompilationResult result = compiler.compileMOs();

        if (result.haveErrors()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Compilation error");
            alert.setHeaderText("At least one MO was not compiled successful.");
            Text contentInfo = new Text("The application will continue to run without the non-compiled source codes. Please check the compilation log for more details.");
            contentInfo.setWrappingWidth(470);
            TextArea detailsArea = new TextArea();
            detailsArea.setEditable(false);
            detailsArea.setWrapText(true);
            detailsArea.setMaxWidth(Double.MAX_VALUE);
            detailsArea.setMaxHeight(Double.MAX_VALUE);
            GridPane.setVgrow(detailsArea, Priority.ALWAYS);
            GridPane.setHgrow(detailsArea, Priority.ALWAYS);

            GridPane content = new GridPane();
            content.setHgap(5);
            content.setVgap(5);

            content.add(contentInfo, 0, 0);
            content.add(detailsArea, 0, 1);
            content.setPrefWidth(500);
            content.setPrefHeight(150);

            StringBuilder builder = new StringBuilder();

            for (String error : result.getJavaMessages()) {
                builder.append(error).append('\n');
            }
            for (String error : result.getCppMessages()) {
                builder.append(error).append('\n');
            }

            detailsArea.setText(builder.toString());

            alert.getDialogPane().setContent(content);
            alert.showAndWait();
        }
    }

    private ContestConfig loadContest() throws IOException {

        ContestConfig config = null;
        try {
            config = new ContestConfig();
            config.validate();

        } catch (ConfigFileException | ValidationException ex) {
            logger.info("Can not load the configuration file.", ex);

            if (ex instanceof InvalidUserDirException) {
                logger.error("The default path must be valid.");
                return null;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ContestLoader.fxml"), bundle);
            Parent root = loader.load();
            ContestLoaderController controller = loader.getController();

            if (ex instanceof ConfigFileException) {
                controller.allowCreateFileOption();
            } else {
                controller.allowEditFileOption(config);
            }

            Stage contestLoader = controller.init(null, root, bundle);

            contestLoader.showAndWait();

            if (controller.isCancelled()) return null;

            return controller.getUpdatedConfig();
        } catch (Throwable t) {
            logger.error("Unknown error:", t);
            return null;
        }

        logger.info("Config file loaded successfully:" + config.toString());

        return config;
    }


}
