package alifec;

import alifec.core.compilation.CompileHelper;
import alifec.core.exception.ConfigFileException;
import alifec.core.exception.CreateContestFolderException;
import alifec.core.exception.ValidationException;
import alifec.core.persistence.ContestFileManager;
import alifec.core.persistence.config.ContestConfig;
import alifec.core.validation.NewContestFolderValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;


/**
 * Created by Sergio Del Castillo on 30/09/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class Util {

    private static final Logger logger;

    static {
        //load the configuration first
        if (System.getProperty("log4j.configurationFile") == null) {
            System.setProperty("log4j.configurationFile", "file:app" + File.separator + "log/log4j2.xml");
        }

        logger = LogManager.getLogger(Util.class);
    }

    public static void main(String[] args) {
        logger.trace("Starting Artificial Life Contest Command Line Utility.");

        if (args.length > 0) {
            if ("-c".equals(args[0]) || "--compile".equals(args[0])) {
                if (args.length == 2) {
                    compileOne(args[1]);
                    exit();
                } else {
                    logger.error("-c option requires one parameter.");
                }
            }

            if ("-ca".equals(args[0]) || "--compile-all".equals(args[0])) {
                compileAll();
                exit();
            }

            if ("-nc".equals(args[0]) || "--new-contest".equals(args[0])) {
                if (args.length == 2) {
                    newContest(args[1]);
                    exit();
                } else {
                    logger.error("-nc option requires one parameter.");
                }
            }

            if ("-sc".equals(args[0]) || "--set-contest".equals(args[0])) {
                if (args.length == 2) {
                    setContest(args[1]);
                    exit();
                } else {
                    logger.error("-sc option requires one parameter.");
                }
            }
        }

        new Usage().show();
        exit();
    }

    private static void compileOne(String mo) {
        logger.info("Compile one " + mo);

        try {
            CompileHelper compiler = new CompileHelper(loadConfiguration());
            compiler.compileOneMO(mo);
        } catch (ConfigFileException e) {
            logger.error(e.getMessage());
            logger.info("Use the -sc option to set an existing contest as default or " +
                    "the -nc option to create a new one");
        } catch (Throwable t) {
            logger.error(t.getMessage(), t);
        }
    }

    private static void compileAll() {
        logger.info("Compile all mos");

        try {
            CompileHelper compiler = new CompileHelper(loadConfiguration());
            compiler.compileMOs();
        } catch (ConfigFileException e) {
            logger.error(e.getMessage());
            logger.info("Use the -sc option to set an existing contest as default or " +
                    "the -nc option to create a new one");
        } catch (Throwable t) {
            logger.error(t.getMessage(), t);
        }
    }

    private static void newContest(String name) {
        logger.info("Create new contest");

        try {
            ResourceBundle bundle = ResourceBundle.getBundle("i18n/messages", Locale.ENGLISH);
            ContestConfig config = new ContestConfig(bundle, ContestConfig.CONTEST_NAME_PREFIX + name);
            new NewContestFolderValidator().validate(config.getContestPath());
            ContestFileManager.buildNewContest(config, Boolean.TRUE);
            saveConfigFile(config);
        } catch (CreateContestFolderException | ConfigFileException | ValidationException e) {
            logger.error(e.getMessage());
            logger.info("Use the -sc option to set an existing contest as default or " +
                    "the -nc option to create a new one");
        } catch (Throwable t) {
            logger.error(t.getMessage(), t);
        }
    }

    private static void setContest(String name) {
        logger.info("Set default contest");

        try {
            ResourceBundle bundle = ResourceBundle.getBundle("i18n/messages", Locale.ENGLISH);

            ContestConfig config = new ContestConfig(bundle, ContestConfig.CONTEST_NAME_PREFIX + name);
            saveConfigFile(config);
        } catch (ConfigFileException e) {
            logger.error(e.getMessage());
        } catch (Throwable t) {
            logger.error(t.getMessage(), t);
        }
    }

    private static ContestConfig loadConfiguration() throws IOException, ConfigFileException, ValidationException {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("i18n/messages", Locale.ENGLISH);

            ContestConfig config = new ContestConfig(bundle);
            config.validate();
            return config;
        } catch (ConfigFileException | ValidationException ex) {
            if (ex.getCause() instanceof FileNotFoundException) {
                ContestConfig config = tryToLoadContest();
                if (config != null) {
                    return config;
                }
            }
            throw ex;
        }
    }

    private static ContestConfig tryToLoadContest() throws ConfigFileException, IOException {
        ResourceBundle bundle = ResourceBundle.getBundle("i18n/messages", Locale.ENGLISH);

        List<String> list = ContestFileManager.listContest();
        if (list.size() == 1) {
            String name = list.get(0);
            ContestConfig config = new ContestConfig(bundle, name);
            saveConfigFile(config);
            return config;
        }
        return null;
    }

    private static void saveConfigFile(ContestConfig config) throws ConfigFileException {
        config.save();
        logger.info("The contest file was updated as follows: " + config.toString());
    }


    private static void exit() {
        logger.trace("Finished.");
        System.exit(0);
    }
}


