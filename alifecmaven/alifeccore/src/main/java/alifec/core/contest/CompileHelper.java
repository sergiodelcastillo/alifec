package alifec.core.contest;

import alifec.core.exception.CompilerException;
import alifec.core.simulation.AllFilter;
import org.apache.log4j.Logger;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;
import java.util.Vector;

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
     * @return true if successfully
     */
    public static CompilationResult compileMOs(String MOsPath) {
        logger.info("Compile JAVA Files");
        CompilationResult result = new CompilationResult();
        try {
            for (File f : AllFilter.getFilesJava(MOsPath)) {

                if (compileMOsJava(MOsPath, f.getParent(), f.getName())) {
                    logger.info(f.getAbsolutePath() + " [OK]");
                } else {
                    logger.error(f.getAbsolutePath() + " [FAIL]");
                    result.logJavaError("Could not compileMOs " + f.getName() + ". For more details see the logs.");
                    //Message.printErr(null, "Could not compileMOs " + f.getName() + ". For more details use make");
                }
            }
        } catch (CompilerException ex) {
            result.logJavaError(ex.getMessage());
            //Message.printErr(null, ex.getMessage());
        }

        logger.info("Compile C++ Files");

        if (updateTournamentCpp(MOsPath) && updateIncludes(MOsPath)) {
            logger.info("Update C++ Files: [OK]");

            if (compileAllMOsCpp(MOsPath)) {
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
     * this method use the native compiler(Javac) to compileMOs the java codes.
     * The compiled codes are saved in the lib/MOs folder
     *
     * @param path absolute path of C/C++ microorganism
     * @param name of destination library
     * @return true if the compilation is successfully
     * @throws CompilerException if can not find the C/C++ compiler
     */
    static boolean compileMOsJava(String MOsPath, String path, String name) throws CompilerException {
        try {
            JavaCompiler javac = ToolProvider.getSystemJavaCompiler();

            if (javac == null)
                throw new CompilerException();

            File out = LogPrintter.getErrorFile(path, name);
            DataOutputStream err = new DataOutputStream(
                    new BufferedOutputStream(
                            new FileOutputStream(out)));
            int s = javac.run(null, null, err, "-d", MOsPath, path + File.separator + name);

            if (s == 0)
                out.deleteOnExit();

            return s == 0;
        } catch (FileNotFoundException ex) {
            logger.info("Fail to compileMOs: " + path + File.separator + name + ". File not found.");
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
        Vector<String> names = AllFilter.listNamesCpp(MOsPath);
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
        Vector<String> files = AllFilter.list_file_cpp(MOsPath);

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
