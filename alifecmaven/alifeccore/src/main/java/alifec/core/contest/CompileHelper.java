package alifec.core.contest;

import alifec.core.exception.CompilerException;
import alifec.core.simulation.SourceCodeFilter;
import org.apache.log4j.Logger;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;


/**
 * Created by Sergio Del Castillo on 07/08/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class CompileHelper {

    static Logger logger = org.apache.log4j.Logger.getLogger(CompileHelper.class);

    /**
     * Compile .java and .cpp files
     * java files: are compiled and saved in the lib/MOs folder
     * C/Cpp files: are build a unique library(cppcolonies.so/.dll) and saved in the lib/MOs folder.
     *
     * @param config
     * @return true if successfully
     */
    public static CompilationResult compileMOs(ContestConfig config) {
        logger.info("Cleaning up files in directory: " + config.getCompilationTarget());

        try {
            cleanupCompilationTarget(config.getCompilationTarget());
            logger.info("Cleanup of compiled files [OK] ");
        } catch (IOException e) {
            //TODO: mostrar el log del error con un popup o algo similar.
            logger.error("Cleanup of compiled files [FAIL] ");
            logger.error(e.getMessage(), e);
        }


        logger.info("Compile JAVA Files");
        CompilationResult result = new CompilationResult();
        try {
            for (File f : SourceCodeFilter.getFilesJava(config.getMOsPath())) {

                if (compileMOsJava(config, f.getParent(), f.getName())) {
                    logger.info(f.getAbsolutePath() + " [OK]");
                } else {
                    logger.error(f.getAbsolutePath() + " [FAIL]");
                    result.logJavaError("Could not compileMOs " + f.getName() + ". For more details see the logs.");
                }
            }
        } catch (CompilerException ex) {
            result.logJavaError(ex.getMessage());
        }

        logger.info("Compile C++ Files");

        if (updateTournamentCpp(config.getMOsPath()) && updateIncludes(config.getMOsPath())) {
            logger.info("Update C++ Files: [OK]");

            if (compileAllMOsCpp(config.getMOsPath())) {
                logger.info("Create libcppcolonies: [OK]");
            } else {
                logger.info("Create libcppcolonies: [FAIL]");
                result.logCppError("Could not compileMOs one o more microorganisms of C++. For more details see log file.");
            }

        } else {
            logger.info("Update C++ Files: [FAIL]");
            result.logCppError("Could not update cpp files. For more details see log file.");
        }

        return result;
    }

    /**
     * Remove the folder of compiled *.java or *.cpp
     *
     * @param targetFolder
     * @throws IOException
     */
    private static void cleanupCompilationTarget(String targetFolder) throws IOException {
        File rootDir = new File(targetFolder);

        if (rootDir.exists()) {
            Path dirPath = Paths.get(targetFolder);

            Files.walk(dirPath)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .peek(file -> logger.info("Deleting: " + file))
                    .forEach(File::delete);
        }

        if (!rootDir.mkdir())
            throw new IOException("Can not create the compilation target: " + rootDir.getAbsolutePath());

    }

    /**
     * this method use the native compiler(Javac) to compileMOs the java codes.
     * The compiled codes are saved in the lib/MOs folder
     *
     * @param config       the config file
     * @param javaFileName the source code .java
     * @return true if the compilation is successfully
     * @throws CompilerException if can not find the C/C++ compiler
     */
    static boolean compileMOsJava(ContestConfig config, String javaFileFolder, String javaFileName) throws CompilerException {
        try {
            JavaCompiler javac = ToolProvider.getSystemJavaCompiler();

            if (javac == null)
                throw new CompilerException();

            File out = config.getCompilationFilePath(javaFileName);
            DataOutputStream err = new DataOutputStream(
                    new BufferedOutputStream(
                            new FileOutputStream(out)));
            int s = javac.run(null, null, err, "-d", config.getCompilationTarget(), javaFileFolder + File.separator + javaFileName);

            if (s == 0)
                out.deleteOnExit();

            return s == 0;
        } catch (FileNotFoundException ex) {
            logger.info("Failed to compileMOs: " + javaFileFolder + File.separator + javaFileName + ". File not found.");
            return false;
        }

    }

    /**
     * Compila todos los MOs C++ y genera una libreria dinamica
     * llamada libcppcolonies.dll o libcppcolonies.so de acuerdo al
     * sistema operativo. La librería se almacena en lib/MOs
     *
     * @param MOsPath URL of Microorganism sources
     * @return true if is successfully
     */
    static private boolean compileAllMOsCpp(String MOsPath) {
        //@Todo Use a file with the pattern instead of hiring it in the source code
        try {

            String os = System.getProperty("os.name").toLowerCase();
            String[] console = {""};

            if (os.contains("linux")) {
                String com = "g++ -o " + MOsPath + "/lib/MOs/libcppcolonies.so -fPIC -Wall -shared -lm -Icpp -I\"" +
                        MOsPath + "\"  cpp/lib_CppColony.cpp";

                console = new String[]{"/bin/bash", "-c", com};
            } else if (os.contains("windows")) {
                String com = "g++ -o " + MOsPath + "/lib/MOs/libcppcolonies.dll -Wl,--add-stdcall-alias -Wall -shared -lm -Icpp -I\"" +
                        MOsPath + "\"  cpp/lib_CppColony.cpp";

                console = new String[]{"cmd.exe", "/C", com};
            }

            Process p = Runtime.getRuntime().exec(console);
            p.waitFor();

            p.getErrorStream().close();
            p.getInputStream().close();
            p.getOutputStream().close();

            return p.exitValue() == 0;
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * Crea el archivo Tournament.cpp con los nombres que se
     * pasan como argumento.
     *
     * @return true if is successfully
     */
    static boolean updateTournamentCpp(String MOsPath) {
        List<String> names = SourceCodeFilter.listNamesCpp(MOsPath);
        File env = new File(MOsPath + "/lib/MOs/" + File.separator + "Environment.cpp");
        try {
            if (!env.exists())
                env.createNewFile();

            PrintWriter pw = new PrintWriter(env);

            pw.println("");
            pw.println("bool Environment::addColony(string name, int id){");
            pw.println("	CppColony<Microorganism> *mo = NULL;");
            pw.println("");

            for (String name : names) {
                pw.println("	if(name == \"" + name + "\"){");
                pw.println("	   mo = (CppColony < Microorganism > *) new CppColony< " + name + " >(id);");
                pw.println("   }");
            }

            pw.println("");
            pw.println("	colonies.push_back(mo);");
            pw.println("");
            pw.println("   return mo != NULL;");
            pw.println("}");
            pw.println("");
            pw.close();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }


    /**
     * Crea el archivo includemmos.h con los nombres que se
     * pasan como argumento:
     *
     * @return true if  is successfully
     */
    static boolean updateIncludes(String MOsPath) {
        List<String> files = SourceCodeFilter.listFileCpp(MOsPath);

        File includes = new File(MOsPath + "/lib/MOs/" + File.separator + "includemos.h");
        try {

            if (!includes.exists())
                includes.createNewFile();

            PrintWriter pw = new PrintWriter(includes);

            for (String n : files) {
                pw.println("#include \"" + n + "\"");
            }
            pw.close();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }


}
