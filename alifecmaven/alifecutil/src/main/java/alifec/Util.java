package alifec;

import alifec.core.compilation.CompileHelper;
import alifec.core.exception.ConfigFileException;
import alifec.core.persistence.config.ContestConfig;
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
        }

        showUsage();
        exit();
    }

    private static void showUsage() {
        String usage = loadUsage();
        logger.info(usage);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void compileAll() {
        logger.info("Compile all mos");

        try {
            CompileHelper compiler = new CompileHelper(loadConfiguration());
            compiler.compileMOs();
        } catch (Exception e) {
            logger.error(e.getMessage());
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


