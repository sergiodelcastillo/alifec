package alifec.simulation.main;

import alifec.core.compilation.CompilationResult;
import alifec.core.compilation.CompileHelper;
import alifec.core.event.EventBus;
import alifec.core.exception.*;
import alifec.core.persistence.ContestFileManager;
import alifec.core.persistence.config.ContestConfig;
import alifec.simulation.controller.ALifeContestController;
import alifec.simulation.controller.CompilationErrorController;
import alifec.simulation.controller.ContestLoaderController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.*;

/**
 * Created by Sergio Del Castillo on 14/06/18.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class ALifeContestMain extends Application {
    private Logger logger = LogManager.getLogger(ALifeContestMain.class.getName());
    private final ResourceBundle bundle;

    public static void main(String[] args) {
        // launch the application
        Application.launch(ALifeContestMain.class, args);
    }


    public ALifeContestMain() {
        logProperties();

        Locale currentLocale = Locale.ENGLISH;
        //TODO: set the default locale from configuration or load english instead.

        bundle = ResourceBundle.getBundle("i18n/messages", currentLocale);
    }

    private void logProperties() {
        if (logger.isTraceEnabled()) {
            logger.trace("System Properties:");

            Properties p = System.getProperties();
            for (Object o : p.keySet()) {
                String key = (String) o;
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
            logger.fatal("The contest file was not loaded. Application wont start.");
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

    private void compileColonies(ContestConfig config) throws IOException {
        CompileHelper compiler = new CompileHelper(config);
        CompilationResult result = compiler.compileMOs();

        //todo: check this part. Not sure if the application should always load and do not alert
        // or alert every compilation error.
        if (false && result.haveErrors()) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CompilationError.fxml"), bundle);
            Stage root = loader.load();

            StringBuilder builder = new StringBuilder();

            for (String error : result.getJavaMessages()) {
                builder.append(error).append('\n');
            }
            for (String error : result.getCppMessages()) {
                builder.append(error).append('\n');
            }

            ((CompilationErrorController) loader.getController()).setText(builder.toString());

            root.showAndWait();
        }
    }

    private ContestConfig loadContest() {
        ContestConfig config = null;

        try {
            config = new ContestConfig(bundle);
        } catch (ConfigFileException ex) {
            logger.info("Could not load the configuration file: {}", ex.getMessage());

            if (ex instanceof InvalidUserDirException) {
                logger.error(ex.getMessage(), ex);
                return null;
            }

            if (ex instanceof ConfigFileReadException) {
                logger.error(ex.getMessage(), ex);
                return null;
            }

            if (ex instanceof ConfigFileNotFoundException) {
                config = createOrSetContest();
            }
        } catch (Throwable t) {
            logger.error("Unknown error:", t);
            return null;
        }

        //the config could be null, because the user cancelled the creation.
        if (config == null) return null;

        try {
            config.validate();
            logger.info("Config file loaded successfully:" + config.toString());
        } catch (ValidationException e) {
            logger.error(e.getMessage(), e);
            logger.error("The configuration file is not valid so it will be ignored. " +
                    "The application will request to create a new contest or set existing one.");
            config = createOrSetContest();
        }

        return config;
    }


    private ContestConfig createOrSetContest() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ContestLoader.fxml"), bundle);
        Stage root;
        try {
            root = loader.load();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
        ContestLoaderController controller = loader.getController();

        controller.init(bundle, root);

        //find contests
        List<String> contestList;
        try {
            contestList = ContestFileManager.listContest();
        } catch (ConfigFileException | IOException e) {
            logger.warn(e.getMessage(), e);
            contestList = new ArrayList<>();
        }
        controller.allowCreateFileOption(contestList);

        root.showAndWait();

        if (controller.isCancelledOrFailed()) {
            return null;
        }

        return controller.getUpdatedConfig();
    }


    @Override
    public void stop() throws Exception {
        super.stop();

        logger.info("Good bye!.");

        //close the bus to avoid running theads issues.
        EventBus.exit();

        // close the javafx platform
        Platform.exit();
    }

}
