package alifec.core.persistence.config;

import alifec.core.exception.CompileConfigException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Sergio Del Castillo on 17/10/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class CompileConfig {
    static Logger logger = LogManager.getLogger(CompileConfig.class);

    private static final String LINUX_ORACLE_KEY = "linux.oracle";
    private static final String LINUX_OPENJDK_KEY = "linux.openjdk";
    private static final String WINDOWS_ORACLE_KEY = "windows.oracle";

    private final String javaHome;
    private final String jvm;
    private final String os;
    private final String compilationTarget;
    private final String cppApiFolder;
    private final String mosPath;

    private String linuxOracle;
    private String linuxOpenJdk;
    private String windowsOracle;


    public CompileConfig(String compilerConfigFile, String compilationTarget, String cppApiFolder, String mosPath) throws CompileConfigException {
        Properties property = new Properties();

        try (InputStream is = new FileInputStream(compilerConfigFile)) {
            property.load(is);

            for (Object object : property.keySet()) {
                if (!setProperty(object.toString(), property.getProperty(object.toString()))) {
                    logger.warn("Can not set the property: " + object.toString() + "=" + property.getProperty(object.toString()));
                }
            }
        } catch (IOException e) {
            throw new CompileConfigException("Error loading the config file in path: " + compilerConfigFile, e, this);
        }

        try {
            //validate if the configuration is Ok.
            validate();
        } catch (CompileConfigException e) {
            logger.warn(e.getMessage(), e);
            throw e;
        }

        this.os = System.getProperty("os.name").toLowerCase();
        this.jvm = System.getProperty("java.runtime.name");
        this.javaHome = System.getProperty("java.home") + File.separator;
        this.compilationTarget = compilationTarget;
        this.cppApiFolder = cppApiFolder;
        this.mosPath = mosPath;
    }

    public CompileConfig(ContestConfig config) throws CompileConfigException {
        this(config.getCompilerConfigFile(),
                config.getCompilationTarget(),
                config.getCppApiFolder(),
                config.getMOsPath());
    }

    private void validate() throws CompileConfigException {
        if (linuxOracle == null || linuxOracle.isEmpty())
            throw new CompileConfigException("The property for java on GNU/Linux using Oracle JVM was not set.", this);

        if (linuxOpenJdk == null || linuxOpenJdk.isEmpty())
            throw new CompileConfigException("The property for java on GNU/Linux using OpenJDK JVM was not set.", this);

        if (windowsOracle == null || windowsOracle.isEmpty())
            throw new CompileConfigException("The property for java on Windows using Oracle JVM was not set.", this);
    }

    private boolean setProperty(String type, String option) {
        if (type == null || option == null) return false;

        type = type.trim().toLowerCase();
        option = option.trim();

        if (type.isEmpty() || option.isEmpty())
            return false;

        switch (type) {
            case LINUX_ORACLE_KEY:
                linuxOracle = option;
                break;
            case LINUX_OPENJDK_KEY:
                linuxOpenJdk = option;
                break;
            case WINDOWS_ORACLE_KEY:
                windowsOracle = option;
                break;
            default:
                return false;
        }

        return true;
    }

    public String getLinuxOracleLine() {
        return String.format(linuxOracle,
                compilationTarget,
                cppApiFolder,
                mosPath,
                javaHome + "include/",
                javaHome + "include/linux/",
                cppApiFolder);
    }

    public String getLinuxOpenJdkLine() {
        return String.format(linuxOpenJdk,
                compilationTarget,
                cppApiFolder,
                mosPath,
                javaHome + "include/",
                javaHome + "include/linux/",
                cppApiFolder);
    }

    public String getWindowsOracleLine() {
        return String.format(windowsOracle,
                compilationTarget,
                cppApiFolder,
                mosPath,
                javaHome + "include/",
                javaHome + "include/win32/",
                cppApiFolder);
    }

    public String getLinuxOracle() {
        return linuxOracle;
    }

    public String getLinuxOpenJdk() {
        return linuxOpenJdk;
    }

    public String getWindowsOracle() {
        return windowsOracle;
    }

    public String getJavaHome() {
        return javaHome;
    }

    public String getJvm() {
        return jvm;
    }

    public String getOs() {
        return os;
    }

    public boolean isLinux() {
        return os.toLowerCase().contains("linux");
    }

    public boolean isWindows() {
        return os.toLowerCase().contains("windows");
    }

    public boolean isOpenJDKJVM() {
        return jvm.equals("OpenJDK Runtime Environment");
    }

    public boolean isOracleJVM() {
        return jvm.equals("Java(TM) SE Runtime Environment");
    }

}
