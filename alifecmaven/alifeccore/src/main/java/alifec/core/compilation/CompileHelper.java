package alifec.core.compilation;

import alifec.core.exception.CompileConfigException;
import alifec.core.exception.CompilerException;
import alifec.core.persistence.SourceCodeFileManager;
import alifec.core.persistence.config.CompileConfig;
import alifec.core.persistence.config.ContestConfig;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

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
    private static Logger logger = LogManager.getLogger(CompileHelper.class);

    private static Logger moLogger = LogManager.getLogger("alifec.mo.compilation");
    private static LogOutputStream out = new LogOutputStream(moLogger, Level.INFO);
    private static LogOutputStream err = new LogOutputStream(moLogger, Level.ERROR);

    private ContestConfig config;
    private SourceCodeFileManager persistence;

    public CompileHelper(ContestConfig config) {
        this.config = config;
        this.persistence = new SourceCodeFileManager(config.getMOsPath());
    }

    /**
     * Compile .java and .cpp files
     * java files: are compiled and saved in folder "contest-name"/compiled
     * C/Cpp files: are built in an unique library(libcppcolonies.so/.dll) and saved in folder "contest-name"/compiled.
     *
     * @return true if successfully
     */
    public CompilationResult compileMOs() {
        cleanupCompilationTarget(config.getCompilationTarget());

        CompilationResult result = new CompilationResult();

        compileJavaFiles( null, result);
        compileAllCppFiles(result);

        return result;
    }

    private void compileJavaFiles(String mo, CompilationResult result) {
        logger.info("Compile JAVA Files");

        try {
            for (Path f : persistence.listJavaFiles()) {
                if (mo == null || f.getFileName().toString().equals(mo + ".java")) {
                    if (javaSourceCodeCompilation(f.getParent().toString(), f.getFileName().toString())) {
                        logger.info(f.toString() + " [OK]");
                    } else {
                        logger.error(f.toString() + " [FAIL]");
                        result.logJavaError("Could not compileMO " + f.getFileName().toString() + ". For more details see the logs.");
                    }
                }
            }
        } catch (CompilerException | IOException ex) {
            logger.error(ex.getMessage(), ex);
            result.logJavaError(ex.getMessage());
        }
    }

    private void compileAllCppFiles(CompilationResult result) {
        logger.info("Compile C++ Files");

        if (updateTournamentCpp() && updateIncludes()) {
            logger.info("Update C++ Files: [OK]");

            if (cppCompilationAllSourceCodes()) {
                logger.info("Create libcppcolonies: [OK]");
            } else {
                logger.info("Create libcppcolonies: [FAIL]");
                result.logCppError("Could not compileMOs one o more microorganisms of C++. For more details see log file.");
            }

        } else {
            logger.info("Update C++ Files: [FAIL]");
            result.logCppError("Could not update cpp files. For more details see log file.");
        }
    }

    public CompilationResult compileOneMO(String mo) {
        cleanupCompilationTarget(config.getCompilationTarget());

        CompilationResult compilationResult = new CompilationResult();
        try {
            List<String> javaMOs = persistence.listJavaMOs();
            List<String> cppMos = persistence.listCppMOs();


            if (javaMOs.contains(mo)) {
                compileJavaFiles(mo, compilationResult);
                return compilationResult;
            }

            if (cppMos.contains(mo)) {
                compileAllCppFiles(compilationResult);
                return compilationResult;
            }
            //the mo was not found.
            compilationResult.logJavaError(mo + " was not found in java files.");
            compilationResult.logCppError(mo + " was not found in c++ files.");
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            compilationResult.logJavaError(e.getMessage());
        }
        return compilationResult;
    }

    /**
     * Remove the "compiled" folder of compiled files corresponding to *.java or *.cpp.
     *
     * @param targetFolder
     * @throws IOException
     */
    private void cleanupCompilationTarget(String targetFolder) {
        logger.info("Cleaning up files in directory: " + targetFolder);

        try {
            Path rootDir = Paths.get(targetFolder);

            if (Files.exists(rootDir)) {
                Path dirPath = Paths.get(targetFolder);

                Files.walk(dirPath)
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .peek(file -> logger.info("Deleting: " + file))
                        .forEach(File::delete);
            }

            Files.createDirectory(rootDir);

            logger.info("Cleanup of compiled files [OK] ");
        } catch (IOException ex) {
            logger.error("Cleanup of compiled files [FAIL] ");
            logger.error(ex.getMessage(), ex);
        }
    }

    /**
     * This method use the native compiler(Javac) to compile the java source codes.
     * The compiled codes are stored in folder <b>"contest name"/compiled/</b>
     *
     * @param javaFileName the source code .java
     * @return true if the compilation is successfully
     * @throws CompilerException if can not find the C/C++ compiler
     */
    boolean javaSourceCodeCompilation(String javaFileFolder, String javaFileName) throws CompilerException {
        try {
            JavaCompiler javac = ToolProvider.getSystemJavaCompiler();

            if (javac == null)
                throw new CompilerException();

            configureLogFile(javaFileName, config);
            String javaFilePath = javaFileFolder + File.separator + javaFileName;
            int s = javac.run(null, out, err, "-d", config.getCompilationTarget(), javaFilePath);

            return s == 0;
        } catch (Exception ex) {
            logger.info("Error compiling MOs JAVA: " + javaFileFolder + File.separator + javaFileName + ".");
            return false;
        }

    }

    /**
     * It compiles the MOs C++ and generates a dynamic library which is called
     * "libcppcolonies.dll/libcppcolonies.so according on which Operating System is running.
     * The library is stored in folder <b>"contest name"/compiled</b>.
     *
     * @return true if is successfully
     */
    private boolean cppCompilationAllSourceCodes() {
        try {
            CompileConfig compileConfig = new CompileConfig(config);

            String[] console = {""};
            String compileCommand = null;

            if (compileConfig.isLinux()) {
                if (compileConfig.isOpenJDKJVM()) {
                    compileCommand = compileConfig.getLinuxOpenJdkLine();
                } else if (compileConfig.isOracleJVM()) {
                    compileCommand = compileConfig.getLinuxOracleLine();
                }
                if (null == compileCommand) {
                    //throw new UnsupportedJVMException("");
                    logger.error("Unsupported JVM on GNU/Linux: " + compileConfig.getJvm());
                    return false;
                }
                console = new String[]{"/bin/bash", "-c", compileCommand};

            } else if (compileConfig.isWindows()) {
                if (compileConfig.isOpenJDKJVM()) {
                    //todo: add support to OpenJDK on windows
                } else if (compileConfig.isOracleJVM()) {
                    compileCommand = compileConfig.getWindowsOracleLine();
                }
                if (null == compileCommand) {
                    //throw new UnsupportedJVMException("");
                    logger.error("Unsupported JVM on Windows: " + compileConfig.getJvm());
                    return false;
                }
                console = new String[]{"cmd.exe", "/C", compileCommand};
            }

            logger.trace("Using compile line: " + compileCommand);
            Process p = Runtime.getRuntime().exec(console);
            p.waitFor();

            String buffer;
            BufferedReader readerInputStream = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader readerErrorStream = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            configureLogFile("cpp", config);
            while ((buffer = readerInputStream.readLine()) != null) {
                moLogger.info(buffer);
            }
            readerInputStream.close();
            while ((buffer = readerErrorStream.readLine()) != null) {
                moLogger.error(buffer);
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
        } catch (CompileConfigException e) {
            logger.trace(e.getMessage(), e);
            return false;
        }
    }

    private void configureLogFile(String fileName, ContestConfig config) {
        System.setProperty("moFilename", config.getLogFileName(fileName));
        LoggerContext loggerContext = (LoggerContext) LogManager.getContext(false);
        loggerContext.reconfigure();
    }

    /**
     * Updates the Tournament.cpp file according the name of MOs implemented in C++.
     *
     * @return true if is successfully
     */
    boolean updateTournamentCpp() {
        try {
            List<String> names = persistence.listCppMOs();
            Path env = Paths.get(config.getCppApiFolder() + File.separator + "Environment.cpp");

            if (Files.notExists(env)) {
                Files.createFile(env);
            }

            try (BufferedWriter pw = Files.newBufferedWriter(env)) {
                pw.write("\nbool Environment::addColony(string name, int id){\n");
                pw.write("	CppColony<Microorganism> *mo = NULL;\n\n");

                for (String name : names) {
                    pw.write("	if(name == \"" + name + "\"){\n");
                    pw.write("	   mo = (CppColony < Microorganism > *) new CppColony< " + name + " >(id);\n");
                    pw.write("   }\n");
                    logger.trace("Updating Tournament CPP for MO: " + name);
                }

                pw.write("\n	colonies.push_back(mo);\n\n");
                pw.write("   return mo != NULL;\n");
                pw.write("}\n\n");
            }

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
    boolean updateIncludes() {
        try {
            List<String> files = persistence.listFilenameCpp();
            Path includes = Paths.get(config.getCppApiFolder() + File.separator + "includemos.h");

            if (Files.notExists(includes)) {
                Files.createFile(includes);
            }

            try (BufferedWriter pw = Files.newBufferedWriter(includes)) {
                for (String n : files) {
                    pw.write("#include \"" + n + "\"\n");
                    logger.trace("Updating include for MO: " + n);
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }


}
