package alifec.core.contest;

import alifec.core.exception.CompilerException;
import alifec.core.simulation.SourceCodeFilter;
import org.apache.log4j.Logger;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;
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

    private static String LINUX_ORACLE_COMPILATION_LINE = "g++ -o \"%s/libcppcolonies.so\" -fPIC -Wall -shared -lm -I\"%s\" -I\"%s\" \"%s/lib_CppColony.cpp\"";
    private static String LINUX_OPENJDK_COMPILATION_LINE = "g++ -o \"%s/libcppcolonies.so\" -fPIC -Wall -shared -lm -I\"%s\" -I\"%s\" -I\"%s\" -I\"%s\" \"%s/lib_CppColony.cpp\"";
    private static String WINDOWS_ORACLE_COMPILATION_LINE = "g++ -o \"%s\\libcppcolonies.dll\" -Wl,--add-stdcall-alias -Wall -shared -lm -I\"%s\" -I\"%s\" \"%s\\lib_CppColony.cpp\"";

    /**
     * Compile .java and .cpp files
     * java files: are compiled and saved in folder "contest-name"/compiled
     * C/Cpp files: are built in an unique library(libcppcolonies.so/.dll) and saved in folder "contest-name"/compiled.
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
            logger.error(ex.getMessage(), ex);
            result.logJavaError(ex.getMessage());
        }

        logger.info("Compile C++ Files");

        if (updateTournamentCpp(config) && updateIncludes(config)) {
            logger.info("Update C++ Files: [OK]");

            if (compileAllMOsCpp(config)) {
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
     * Remove the "compiled" folder of compiled files corresponding to *.java or *.cpp.
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
     * This method use the native compiler(Javac) to compile the java source codes.
     * The compiled codes are stored in folder <b>"contest name"/compiled/</b>
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
            logger.info("File not found when compiling MOs JAVA: " + javaFileFolder + File.separator + javaFileName + ".");
            return false;
        }

    }

    /**
     * It compiles the MOs C++ and generates a dynamic library which is called
     * "libcppcolonies.dll/libcppcolonies.so according on which Operating System is running.
     * The library is stored in folder <b>"contest name"/compiled</b>.
     *
     * @param config Configuration of the Contest
     * @return true if is successfully
     */
    static private boolean compileAllMOsCpp(ContestConfig config) {
        //@Todo Use a file with the pattern instead of hiring it in the source code
        try {
            String os = System.getProperty("os.name").toLowerCase();
            String jvm = System.getProperty("java.runtime.name");
            String javaHome = System.getProperty("java.home") + File.separator + "../";

            String[] console = {""};
            String compileCommand = null;

            if (os.contains("linux")) {
                if(jvm.equals("OpenJDK Runtime Environment")){
                    //OpenJDK
                    compileCommand = String.format(LINUX_OPENJDK_COMPILATION_LINE,
                            config.getCompilationTarget(),
                            config.getCppApiFolder(),
                            config.getMOsPath(),
                            javaHome + "include/",
                            javaHome + "include/linux/",
                            config.getCppApiFolder());
                } else if(jvm.equals("Java(TM) SE Runtime Environment")){
                    // ORACLE
                    compileCommand = String.format(LINUX_ORACLE_COMPILATION_LINE,
                            config.getCompilationTarget(),
                            config.getCppApiFolder(),
                            config.getMOsPath(),
                            config.getCppApiFolder());
                }
                if(null == compileCommand){
                    //throw new UnsupportedJVMException("");
                    logger.error("Unsupported JVM on linux: " + jvm);
                    return false;
                }
                console = new String[]{"/bin/bash", "-c", compileCommand};

            } else if (os.contains("windows")) {
                if(jvm.equals("OpenJDK Runtime Environment")) {
                    //todo: add support to OpenJDK on windows
                }else if(jvm.equals("Java(TM) SE Runtime Environment")){
                    // ORACLE
                    compileCommand = String.format(WINDOWS_ORACLE_COMPILATION_LINE,
                            config.getCompilationTarget(),
                            config.getCppApiFolder(),
                            config.getMOsPath(),
                            config.getCppApiFolder());
                }
                if(null == compileCommand){
                    //throw new UnsupportedJVMException("");
                    logger.error("Unsupported JVM on linux: " + jvm);
                    return false;
                }
                console = new String[]{"cmd.exe", "/C", compileCommand};
            }

            logger.trace("Using compile line: " + compileCommand);
            Process p = Runtime.getRuntime().exec(console);
            p.waitFor();

            String buffer = "";
            BufferedReader readerInputStream = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader readerErrorStream = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            while ((buffer = readerInputStream.readLine()) != null) {
                logger.trace(buffer);
            }
            readerInputStream.close();
            while ((buffer = readerErrorStream.readLine()) != null) {
                logger.trace(buffer);
            }
            readerErrorStream.close();
            p.waitFor();


            p.getErrorStream().close();
            p.getInputStream().close();
            p.getOutputStream().close();

            return p.exitValue() == 0;
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
            return false;
        } catch (InterruptedException e) {
            logger.trace(e.getMessage(), e);
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * Updates the Tournament.cpp file according the name of MOs implemented in C++.
     *
     * @return true if is successfully
     */
    static boolean updateTournamentCpp(ContestConfig config) {
        List<String> names = SourceCodeFilter.listNamesCpp(config.getMOsPath());
        File env = new File(config.getCppApiFolder() + File.separator + "Environment.cpp");
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
                logger.trace("Updating Tournament CPP for MO: " + name);
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
     * Creates the file includemos.h with the necessary specific "includes"
     * so all MOs C++ are included in the dynamic library.
     *
     * @return true if  is successfully
     */
    static boolean updateIncludes(ContestConfig config) {
        List<String> files = SourceCodeFilter.listFileCpp(config.getMOsPath());

        File includes = new File(config.getCppApiFolder() + File.separator + "includemos.h");
        try {

            if (!includes.exists())
                includes.createNewFile();

            PrintWriter pw = new PrintWriter(includes);

            for (String n : files) {
                pw.println("#include \"" + n + "\"");
                logger.trace("Updating include for MO: " + n);
            }
            pw.close();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }


}
