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

import java.io.*;
import java.util.stream.Collectors;


/**
 * Created by Sergio Del Castillo on 30/09/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class Util {

    private static final String USAGE_FILE = "usage.txt";

    private static final Logger logger;

    static {
        //load the configuration first
        if (System.getProperty("log4j.configurationFile") == null) {
            System.setProperty("log4j.configurationFile", "file:app" + File.separator + "log4j2.xml");
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
        }

        showUsage();
        exit();
    }

    private static void showUsage() {
        String usage = loadUsage();
        logger.info(usage, ContestConfig.CONTEST_NAME_PREFIX);
    }

    private static String loadUsage() {
        try {
            InputStream usage = Util.class.getClassLoader().getResourceAsStream(USAGE_FILE);
            if (usage != null) {
                return read(usage);
            }
        } catch (Throwable t) {
            logger.error(t.getMessage(), t);
        }

        logger.error("Could not load " + USAGE_FILE + " file");
        return null;
    }

    public static String read(InputStream input) throws IOException {
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(input))) {
            return buffer.lines().collect(Collectors.joining("\n"));
        }
    }

    private static void compileOne(String mo) {
        logger.info("Compile one " + mo);

        try {
            CompileHelper compiler = new CompileHelper(loadConfiguration());
            compiler.compileOneMO(mo);
        } catch (ConfigFileException e) {
            logger.error(e.getMessage());
            logger.info("Use the -nc <name> option to create a new contest");
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
            logger.info("Use the -nc <name> option to create a new contest");
        } catch (Throwable t) {
            logger.error(t.getMessage(), t);
        }
    }

    private static void newContest(String name) {
        logger.info("Create new contest");

        try {
            ContestConfig config = new ContestConfig(".", ContestConfig.CONTEST_NAME_PREFIX + name);
            new NewContestFolderValidator().validate(config.getContestPath());
            ContestFileManager.buildNewContestFolder(config, Boolean.TRUE);
            config.save();
            logger.info("The contest file was updated as follows: " + config.toString());
        } catch (CreateContestFolderException | ConfigFileException | ValidationException e) {
            logger.error(e.getMessage());
        } catch (Throwable t) {
            logger.error(t.getMessage(), t);
        }
    }

    private static ContestConfig loadConfiguration() throws IOException, ConfigFileException {
        return new ContestConfig(ContestConfig.getDefaultPath());
    }


    private static void exit() {
        logger.trace("Finished.");
        System.exit(0);
    }
}


