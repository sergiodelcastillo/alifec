package alifec.core.contest;

import alifec.core.exception.CompilerException;
import alifec.core.simulation.AllFilter;

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


    /**
     * Compile .java and .cpp files
     * java files: are compiled and saved in the lib/MOs folder
     * C/Cpp files: are build a unique library(cppcolonies.so/.dll) and saved in the lib/MOs folder.
     *
     * @return true if successfully
     */
    public static CompilationResult compileMOs(String MOsPath) {
        System.out.println("\nCompile JAVA Files");
        CompilationResult result = new CompilationResult();
        try {
            for (File f : AllFilter.getFilesJava(MOsPath)) {

                if (compileMOsJava(MOsPath, f.getParent(), f.getName())) {
                    System.out.println(f.getAbsolutePath() + " [OK]");
                } else {
                    System.err.println(f.getAbsolutePath() + " [FAIL]");
                    result.logJavaError("Could not compileMOs " + f.getName() + ". For more details see the logs.");
                    //Message.printErr(null, "Could not compileMOs " + f.getName() + ". For more details use make");
                }
            }
        } catch (CompilerException ex) {
            result.logJavaError(ex.getMessage());
            //Message.printErr(null, ex.getMessage());
        }

        System.out.println("\nCompile C++ Files");
        System.out.print("Update C++ Files: ");

        if (updateTournamentCpp(MOsPath) && updateIncludes(MOsPath)) {
            System.out.println("[OK]");
            System.out.print("Create libcppcolonies ");

            if (compileAllMOsCpp(MOsPath)) {
                System.out.println("[OK]");
            } else {
                System.out.println("[FAIL]");
                result.logCppError("Could not compileMOs one o more microorganisms of C++. For more details use make");
                //Message.printErr(null, "Could not compileMOs one o more microorganisms of C++. For more details use make");
            }

        } else
            System.out.println("[FAIL]");

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
            System.out.println("Fail to compileMOs: " + path + File.separator + name + ". File not found.");
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
        } catch (FileNotFoundException ex) {
            return false;
        } catch (IOException e) {
            e.printStackTrace();
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
        } catch (FileNotFoundException ex) {
            return false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }


}
