package alifec;

import alifec.core.compilation.CompileHelper;
import alifec.core.exception.ConfigFileException;
import alifec.core.persistence.config.ContestConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;


/**
 * Created by Sergio Del Castillo on 30/09/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class Util {

    static Logger logger;

    static {
        //load the configuration first
        if (System.getProperty("log4j.configurationFile") == null) {
            System.setProperty("log4j.configurationFile", "file:app" + File.separator + "log4j2.xml");
        }

        logger = LogManager.getLogger(Util.class);
    }

    private static String USAGE = "Artificial Life Contest Command Line Utility.\n" +
            "It reads the contest configuration and compile according the parameters.\n" +
            "Usage: java -jar util-<version>.jar [OPTION] [PARAMETER]\n\n" +
            "The following options are available:\n" +
            "\t-c, --compile <mo name>\t\t\tCompile a specific MO.\n" +
            "\t-ca, --compile-all\t\t\tCompile all MOs.\n\n" +
            "Examples:\n" +
            "\tjava -jar util-01.jar -c myMo\t\t Compiles myMo, it could be java code or c++ code. " +
            "In case of c++ code all mos will be compiled.\n" +
            "\tjava -jar util-01.jar --compile-all\t It will compile all MOs java and c++.\n";

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

        logger.info(USAGE);
        exit();
    }

    private static void compileOne(String mo) {
        logger.info("Compile one " + mo);

        ContestConfig config = null;
        try {
            config = loadConfiguration();
            CompileHelper.compileOneMO(config, mo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void compileAll() {
        logger.info("Compile all mos");

        try {
            ContestConfig config = loadConfiguration();
            CompileHelper.compileMOs(config);
        } catch (Exception e) {
            e.printStackTrace();
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


