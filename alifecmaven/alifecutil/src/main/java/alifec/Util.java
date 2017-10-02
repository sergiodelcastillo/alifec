package alifec;

import alifec.core.compilation.CompileHelper;
import alifec.core.exception.ConfigFileException;
import alifec.core.persistence.ContestConfig;

import java.io.File;
import java.io.IOException;


/**
 * Created by Sergio Del Castillo on 30/09/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 *  java -classpath "/home/yeyo/work/alifec_new/alifec/alifecmaven/alifecutil/target/alifec-util-1.0-SNAPSHOT.jar:/home/yeyo/work/alifec_new/alifec/alifecmaven/alifeccore/target/alifec-core-1.0-SNAPSHOT.jar:/home/yeyo/.m2/repository/org/apache/logging/log4j/log4j-api/2.8.2/log4j-api-2.8.2.jar:/home/yeyo/.m2/repository/org/apache/logging/log4j/log4j-core/2.8.2/log4j-core-2.8.2.jar:/home/yeyo/.m2/repository/org/apache/logging/log4j/log4j-iostreams/2.8.2/log4j-iostreams-2.8.2.jar"  alifec.Util -c "asdf"
 */
public class Util {

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
        if (args.length > 0) {
            if ("-c".equals(args[0]) || "--compile".equals(args[0])) {
                if (args.length == 2) {
                    compileOne(args[1]);
                    System.exit(0);
                } else {
                    System.out.println("ERROR: -c option requires one parameter.\n");
                }
            }

            if ("-ca".equals(args[0]) || "--compile-all".equals(args[0])) {
                compileAll();
                System.exit(0);
            }
        }

        System.out.println(USAGE);
        System.exit(0);
    }

    private static void compileOne(String mo) {
        System.out.println("compile one " + mo);

        ContestConfig config = null;
        try {
            config = loadConfiguration();
            CompileHelper.compileOneMO(config, mo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void compileAll() {
        System.out.println("compile all mos");

        try {
            ContestConfig config = loadConfiguration();
            CompileHelper.compileMOs(config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static ContestConfig loadConfiguration() throws IOException, ConfigFileException {
        String path = new File(System.getProperty("user.dir")).getCanonicalPath();
        //logger.info("Loading contest: " + path);

        ContestConfig config = ContestConfig.buildFromFile(path);

        return config;

    }

    private static void configLogging() {
        //todo... define an specific logger for the utility. Maybe only write in standard out

        if (System.getProperty("log4j.configurationFile") == null) {
            System.setProperty("log4j.configurationFile", "file:app" + File.separator + "log4j2.xml");
        }
    }
}


