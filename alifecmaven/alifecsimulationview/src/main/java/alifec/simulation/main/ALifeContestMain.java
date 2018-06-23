package alifec.simulation.main;

import alifec.core.contest.Contest;
import alifec.core.exception.ConfigFileException;
import alifec.core.exception.ValidationException;
import alifec.core.persistence.ContestFileManager;
import alifec.core.persistence.config.ContestConfig;
import alifec.simulation.controller.ALifeContestController;
import alifec.simulation.controller.ContestLoaderController;
import alifec.simulation.controller.Controller;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
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
        loadContest();

        //initialize the UI
        stage.setTitle(bundle.getString("ALifeContestMain.title"));
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ALifeContestView.fxml"), bundle);
        Parent root = loader.load();

        ((ALifeContestController) loader.getController()).init(bundle, stage);

        stage.setScene(new Scene(root));
        stage.show();
    }

    private void loadContest() throws IOException {

        ContestConfig config = null;
        try {
            config = new ContestConfig();
            config.validate();

        } catch (ConfigFileException | ValidationException ex) {
            logger.error("Can not load the configuration file.", ex);

            if (ex instanceof ConfigFileException) {
                if (((ConfigFileException) ex).getState() == ConfigFileException.Status.DEFAULT_PATH_ERROR) {
                    logger.error("The default path must be valid. The application will shutdown.");
                    Platform.exit();
                }
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ContestLoader.fxml"), bundle);
            Parent root = loader.load();
            ContestLoaderController controller = loader.getController();

            if (ex instanceof ConfigFileException) {
                controller.disableEditFile();
            } else {
                controller.setContestConfig(config);
            }
            //todo: queda implementar la opción de un existing contest!!
            Stage contestLoader = controller.init(null, root, bundle);


            contestLoader.showAndWait();


        }

        logger.info("continue with config:" + config.toString());

        //Todo: remember that the issue to load could be because
        //1. The system was closed unexpectedly
        //2. The configuration file is not OK
        //3. The configuration file does not exist but there are some contest folder in the working directory

    }


}
