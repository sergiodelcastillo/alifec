package alifec.core.compilation;

import alifec.core.exception.CompileConfigException;
import alifec.core.exception.CompilerException;
import alifec.core.exception.UnsupportedJVMException;
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
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;


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

        compileJavaFiles(null, result);
        compileAllCppFiles(result);

        return result;
    }

    private void compileJavaFiles(String mo, CompilationResult result) {
        logger.info("Compile JAVA Files");

        try {
            for (Path f : persistence.listJavaFiles()) {
                if (Objects.isNull(mo) || f.getFileName().toString().equals(mo + ".java")) {
                    if (javaSourceCodeCompilation(f.getParent().toString(), f.getFileName().toString())) {
                        logger.info(f.toString() + " [OK]");
                    } else {
                        logger.error(f.toString() + " [FAIL]");
                        result.logJavaError("Could not compile the Java colony " + f.getFileName().toString() + ".");
                    }
                }
            }
        } catch (IOException ex) {
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
                result.logCppError("Could not compile cpp colonies.");
            }

        } else {
            logger.info("Update C++ Files: [FAIL]");
            result.logCppError("Could not update cpp files.");
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
                try (Stream<Path> files = Files.walk(Paths.get(targetFolder))) {
                    files.sorted(Comparator.reverseOrder())
                            .map(Path::toFile)
                            .peek(file -> logger.info("Deleting: " + file))
                            .forEach(File::delete);
                }
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
    private boolean javaSourceCodeCompilation(String javaFileFolder, String javaFileName) {
        try {
            JavaCompiler javac = ToolProvider.getSystemJavaCompiler();

            if (Objects.isNull(javac))
                throw new CompilerException();

            configureLogFile(javaFileName, config);
            String javaFilePath = javaFileFolder + File.separator + javaFileName;

            //the core and the view part of the framework are java modules,
            // so the property jdk.module.path should be set in order to run ALifeC.
            //todo: check if it is ok by using only one propery or if it is possible to use jdk.module.path;java.class.path
            String modulesPath = System.getProperty("jdk.module.path");
            String classPath = System.getProperty("java.class.path");

            logger.debug("jdk.module.path: " + modulesPath);
            logger.debug("java.class.path: " + classPath);


            //make an effort to find modules in the class path if the module path was not maintained.
            if (Objects.isNull(modulesPath))
                modulesPath = classPath;
            else if (Objects.nonNull(classPath))
                modulesPath += File.pathSeparator + classPath;

            logger.debug("complete path: " + modulesPath);

            int s = javac.run(null, out, err, "-cp", modulesPath, "-d", config.getCompilationTarget(), javaFilePath);

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
        Process p = null;

        try {
            CompileConfig compileConfig = new CompileConfig(config);
            String[] console = buildCommandLine(compileConfig);

            logger.trace("Using compile line: " + Arrays.toString(console));

            p = Runtime.getRuntime().exec(console);
            //todo: check waitfor!!!
            p.waitFor();

            processOutputOfCommand(p);
            p.waitFor();

            return p.exitValue() == 0;
        } catch (IOException | UnsupportedJVMException ex) {
            logger.error(ex.getMessage(), ex);
            return false;
        } catch (InterruptedException e) {
            logger.trace(e.getMessage(), e);
            Thread.currentThread().interrupt();
            return false;
        } catch (CompileConfigException e) {
            logger.trace(e.getMessage(), e);
            return false;
        } finally {
            try {
                //TODO: use try with resources!
                if (Objects.nonNull(p)) {
                    p.getErrorStream().close();
                    p.getInputStream().close();
                    p.getOutputStream().close();
                }
            } catch (IOException ex) {
                logger.error(ex.getMessage(), ex);
            }

        }
    }

    private void processOutputOfCommand(Process p) throws IOException {
        try (BufferedReader readerInputStream = new BufferedReader(new InputStreamReader(p.getInputStream()));
             BufferedReader readerErrorStream = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        ) {
            String buffer;

            configureLogFile("cpp", config);

            while (Objects.nonNull(buffer = readerInputStream.readLine())) {
                moLogger.info(buffer);
            }

            while (Objects.nonNull(buffer = readerErrorStream.readLine())) {
                moLogger.error(buffer);
            }
        }
    }

    private String[] buildCommandLine(CompileConfig config) throws UnsupportedJVMException {
        String compileCommand = null;

        if (config.isLinux()) {
            if (config.isOpenJDKJVM()) {
                compileCommand = config.getLinuxOpenJdkLine();
            } else if (config.isOracleJVM()) {
                compileCommand = config.getLinuxOracleLine();
            }
            if (Objects.isNull(compileCommand)) {
                throw new UnsupportedJVMException("Unsupported JVM on GNU/Linux: " + config.getJvm());
            }
            return new String[]{"/bin/bash", "-c", compileCommand};

        } else if (config.isWindows()) {
            if (config.isOpenJDKJVM()) {
                //todo: add support to OpenJDK on windows
            } else if (config.isOracleJVM()) {
                compileCommand = config.getWindowsOracleLine();
            }
            if (Objects.isNull(compileCommand)) {
                throw new UnsupportedJVMException("Unsupported JVM on Windows: " + config.getJvm());
            }
            return new String[]{"cmd.exe", "/C", compileCommand};
        }

        throw new UnsupportedJVMException("Unsupported JVM on Platform: " + config.getOs());
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
                pw.newLine();
                pw.write("bool Environment::addColony(string name, int id){");
                pw.newLine();
                pw.write("	CppColony<Microorganism> *mo = NULL;");
                pw.newLine();
                pw.newLine();

                for (String name : names) {
                    pw.write("	if(name == \"" + name + "\"){");
                    pw.newLine();
                    pw.write("	   mo = (CppColony < Microorganism > *) new CppColony< " + name + " >(id);");
                    pw.newLine();
                    pw.write("   }");
                    pw.newLine();
                    logger.trace("Updating Tournament CPP for MO: " + name);
                }

                pw.newLine();
                pw.write("	colonies.push_back(mo);");
                pw.newLine();
                pw.newLine();
                pw.write("   return mo != NULL;");
                pw.newLine();
                pw.write("}");
                pw.newLine();
                pw.newLine();
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
                    pw.write("#include \"" + n + "\"");
                    pw.newLine();
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
