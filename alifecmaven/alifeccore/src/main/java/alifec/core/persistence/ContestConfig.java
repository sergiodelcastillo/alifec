package alifec.core.persistence;

import alifec.core.exception.ConfigFileException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * Created by Sergio Del Castillo on 05/08/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class ContestConfig {

    static Logger logger = LogManager.getLogger(ContestConfig.class);

    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATETIME_FORMAT = "yyyyMMdd-HHmmss";

    public static final String BASE_FOLDER = "app";

    /**
     * List of nutrients that are used in contest.
     * generic form: "nutrient_id"
     * name : is the nutrient name used in each tournament
     */
    public static final String NUTRIENTS_FILE = "nutrients";

    public static final String BATTLES_BACKUP_FILE = "battles_backup.csv";
    public static final String BATTLES_FILE = "battles.csv";
    /**
     * Folder of source colonies.
     */
    public static final String MOS_FOLDER = "MOs";

    public static final String CPP_API_FOLDER = "cpp";
    /**
     * Folder of reports.
     */
    public static final String REPORT_FOLDER = "Report";
    public static final String REPORT_FILENAME_TXT = "report-%s.txt";
    public static final String REPORT_FILENAME_CSV = "report-%s.csv";

    public static final String REPORT_TXT_FORMAT = "%-20s%-20s%-20s%-20s\n";
    public static final String REPORT_CSV_FORMAT = "%s,%s,%s,%s,%s";

    public static final String COMPETITORS_FILE = "competitors";
    /**
     * Log Folder.
     */
    public static final String LOG_FOLDER = "Log";

    /**
     * Configuration file.
     */
    public static final String CONFIG_FILE = "config";

    public static final String COMPILATION_TARGET_FOLDER = "compiled";
    public static final String COMPILATION_LOG_FILENAME = "log-%s-%s";

    /**
     * back up file
     */
    public static final String BACKUP_FOLDER = "Backup";
    public static final String BACKUP_FILENAME_ZIP = "backup-%s.zip";

    public static final String CONTEST_NAME_PREFIX = "Contest-";
    public static final String CONTEST_FILENAME = "Contest-%03d";
    public static final String TOURNAMENT_PREFIX = "Tournament-";
    public static final String TOURNAMENT_FILENAME = "Tournament-%03d";

    public static final int PROGRAMMER_MODE = 0;

    public static final int COMPETITION_MODE = 1;

    public static final int DEFAULT_PAUSE_BETWEEN_BATTLES = 100;

    private static final String PROPERTY_PATH_KEY = "contest_path";
    private static final String PROPERTY_CONTEST_NAME_KEY = "contest_name";
    private static final String PROPERTY_PAUSE_BETWEEN_BATTLES_KEY = "pause_between_battles";
    private static final String PROPERTY_MODE_KEY = "contest_mode";

    /**
     * Absolute path of Contest.
     */
    private String path = "";
    /**
     * name of contest
     */
    private String contestName = "";

    /**
     * mode of contest:
     * programmer (mode = 0): the competitor should use this mode.
     * competition(mode = 1): reserved to compete.
     */
    private int mode = 0;

    /**
     * time (in Milliseconds) between battles.
     */
    private int pauseBetweenBattles = 5;

    private ContestConfig() {
    }

    public static void main(String[] args) {
        String res = String.format(ContestConfig.CONTEST_FILENAME, 1);

        System.out.printf("res=" + res);
    }

    /**
     * Read the config File in the project.
     *
     * @param path path to read the config
     * @return true if is successfully
     * @throws IOException         if can not find the config file
     * @throws ConfigFileException if the config file is not valid
     */
    public static ContestConfig buildFromFile(String path) throws ConfigFileException {
        ContestConfig config = new ContestConfig();
        try {
            Properties property = new Properties();
            InputStream is = new FileInputStream(getConfigFilePath(path));

            property.load(is);
            config.setPath(path);
            for (Object object : property.keySet()) {

                if (!config.setProperty(object.toString(), property.getProperty(object.toString()))) {
                    logger.warn("Can not set the property: " + object.toString() + "=" + property.getProperty(object.toString()));
                }
            }
        } catch (IOException ex) {
            throw new ConfigFileException("Error loading the config file in path: " + path, ex, config);
        }

        try {
            //validate if the configuration is Ok.
            config.validate();
        } catch (ConfigFileException e) {
            logger.warn(config.toString());
            throw e;
        }

        return config;
    }


    public static ContestConfig buildNewConfigFile(String path, String contestName) {
        ContestConfig config = new ContestConfig();
        config.setMode(ContestConfig.PROGRAMMER_MODE);
        config.setPath(path);
        config.setContestName(contestName);
        config.setPauseBetweenBattles(ContestConfig.DEFAULT_PAUSE_BETWEEN_BATTLES);

        return config;
    }

    public void save() throws ConfigFileException {
        Properties property = new Properties();

        property.setProperty(PROPERTY_PATH_KEY, path);
        property.setProperty(PROPERTY_CONTEST_NAME_KEY, contestName);
        property.setProperty(PROPERTY_MODE_KEY, Integer.toString(mode));
        property.setProperty(PROPERTY_PAUSE_BETWEEN_BATTLES_KEY, Integer.toString(pauseBetweenBattles));

        try {
            String basePath = getBaseFolder(path);

            if (Files.notExists(Paths.get(basePath))) {
                throw new ConfigFileException("The base path can not be found: " + basePath, this);
            }
            property.store(new FileWriter(this.getConfigFilePath()),
                    "Configuration File\n Warning: do not modify this file");
        } catch (IOException e) {
            throw new ConfigFileException("Can not update the config file: " + getConfigFilePath(), this);
        }
        //the property was saved so the system should be restarted.
    }

    public void validate() throws ConfigFileException {
        if (pauseBetweenBattles < 0) {
            throw new ConfigFileException("property pause_between_battles must have a positive integer.", this);
        }

        if (mode < 0 || mode > 1) {
            throw new ConfigFileException("property mode must have values 0 or 1.", this);
        }

        if (path.isEmpty()) {
            throw new ConfigFileException("The contest path is an empty string.", this);
        }

        if (contestName.isEmpty()) {
            throw new ConfigFileException("The contest name is an empty string.", this);
        }
        try {
            File f = new File(path);
            if (!f.exists())
                throw new ConfigFileException("The contest path folder does not exists: " + f.getCanonicalPath(), this);

            f = new File(path + File.separator + contestName);
            if (!f.exists())
                throw new ConfigFileException("The contest name folder does not exists: " + f.getCanonicalPath(), this);
        } catch (IOException ex) {
            throw new ConfigFileException("The contest path or contest name does not exists.", this);
        }
    }

    private boolean setProperty(String type, String option) {
        if (type == null || option == null) return false;

        type = type.trim().toLowerCase();
        option = option.trim();

        if (type.isEmpty() || option.isEmpty())
            return false;

        switch (type) {
            case PROPERTY_PATH_KEY:
                try {
                    path = new File(option).getCanonicalPath();
                } catch (IOException ex) {
                    logger.error(ex.getMessage(), ex);
                    return false;
                }
                break;
            case PROPERTY_CONTEST_NAME_KEY:
                contestName = option;
                break;
            case PROPERTY_MODE_KEY:
                try {
                    mode = Integer.parseInt(option);
                } catch (NumberFormatException ex) {
                    logger.error(ex.getMessage(), ex);
                    return false;
                }
                break;
            case PROPERTY_PAUSE_BETWEEN_BATTLES_KEY:
                try {
                    pauseBetweenBattles = Integer.parseInt(option);
                } catch (NumberFormatException ex) {
                    logger.error(ex.getMessage(), ex);
                    return false;
                }
                break;
        }
        return true;
    }


    public void setPath(String path) {
        this.path = path;
    }

    public void setContestName(String name) {
        this.contestName = name;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public void setPauseBetweenBattles(int pauseBetweenBattles) {
        this.pauseBetweenBattles = pauseBetweenBattles;
    }

    public String getContestPath() {
        return getContestPath(this.path, this.contestName);
    }

    public static String getContestPath(String absolutePath, String contestName) {
        return absolutePath + File.separator + contestName;
    }

    public String getTournamentPath(String tournamentName) {
        return getContestPath() + File.separator + tournamentName;
    }

    public String getBattlesBackupFile(String tournamentName) {
        return getTournamentPath(tournamentName) + File.separator + BATTLES_BACKUP_FILE;
    }

    public String getBattlesFile(String tournamentName) {
        return getTournamentPath(tournamentName) + File.separator + BATTLES_FILE;
    }

    public int getMode() {
        return mode;
    }

    public boolean isCompetitionMode() {
        return mode == ContestConfig.COMPETITION_MODE;
    }

    public boolean isProgrammerMode() {
        return mode == ContestConfig.PROGRAMMER_MODE;
    }

    public String getConfigFilePath() {
        return getConfigFilePath(this.path);
    }

    public static String getConfigFilePath(String path) {
        return getBaseFolder(path) + File.separator + CONFIG_FILE;
    }

    public static String getBaseFolder(String path) {
        if (path == null || path.isEmpty()) path = ".";

        return path + File.separator + BASE_FOLDER;
    }

    public static boolean existsConfigFile(String path) {
        return new File(getConfigFilePath(path)).exists();
    }

    public String getNutrientsFilePath() {
        return getNutrientsFilePath(path, contestName);
    }

    public static String getNutrientsFilePath(String absolutePath, String contestName) {
        return getContestPath(absolutePath, contestName) + File.separator + NUTRIENTS_FILE;
    }

    public String getMOsPath() {
        return getMOsPath(path, contestName);
    }

    public static String getMOsPath(String absolutePath, String contestName) {
        return getContestPath(absolutePath, contestName) + File.separator + MOS_FOLDER;
    }

    public String getReportPath() {
        return getReportPath(path, contestName);
    }

    public static String getReportPath(String absolutePath, String contestName) {
        return getContestPath(absolutePath, contestName) + File.separator + REPORT_FOLDER;
    }

    public String getReportFilenameTxt() {
        return getReportFilenameTxt(path, contestName);
    }

    public static String getReportFilenameTxt(String absolutePath, String contestName) {
        String dateString = new SimpleDateFormat(DATE_FORMAT).format(new Date());

        return getReportPath(absolutePath, contestName) + File.separator +
                String.format(REPORT_FILENAME_TXT, dateString);
    }

    public String getReportFilenameCsv() {
        return getReportFilenameCsv(path, contestName);
    }

    public static String getReportFilenameCsv(String absolutePath, String contestName) {
        String dateString = new SimpleDateFormat(DATE_FORMAT).format(new Date());

        return getReportPath(absolutePath, contestName) + File.separator +
                String.format(REPORT_FILENAME_CSV, dateString);
    }

    public String getContestName() {
        return this.contestName;
    }

    public int getPauseBetweenBattles() {
        return this.pauseBetweenBattles;
    }

    public String getBackupFolder() {
        return getBackupFolder(path, contestName);
    }

    public static String getBackupFolder(String path, String contestName) {
        return getContestPath(path, contestName) + File.separator + BACKUP_FOLDER;
    }

    public String getBackupFile() {
        return getBackupFile(path, contestName);
    }

    public static String getBackupFile(String path, String contestName) {
        return getBackupFolder(path, contestName) + File.separator +
                String.format(BACKUP_FILENAME_ZIP,
                        new SimpleDateFormat(DATETIME_FORMAT).format(new Date()));
    }

    public String getPath() {
        return path;
    }


    public File getCompilationLogFile(String javaFile) throws IOException {
        String logFolderPath = getLogFolder();
        File logFolder = new File(logFolderPath);
        if (!logFolder.exists()) {
            if (!logFolder.mkdir())
                throw new IOException("Error while creating folder: " + logFolder);
        }

        String compilationFile = logFolderPath + File.separator + getLogFileName(javaFile);

        return new File(compilationFile);
    }

    public String getLogFileName(String javaFile) {
        return String.format(COMPILATION_LOG_FILENAME,
                javaFile.replace(".java", ""),
                new SimpleDateFormat(DATETIME_FORMAT).format(new Date()));
    }

    public String getLogFolder() {
        return getLogFolder(this.path, this.contestName);
    }

    public static String getLogFolder(String path, String contestName) {
        return getContestPath(path, contestName) + File.separator + ContestConfig.LOG_FOLDER;
    }

    public String getCompilationTarget() {
        return getCompilationTarget(path, contestName);
    }

    public static String getCompilationTarget(String path, String contestName) {
        return getContestPath(path, contestName) + File.separator + ContestConfig.COMPILATION_TARGET_FOLDER;
    }

    public String getCppApiFolder() {
        return getCppApiFolder(path, contestName);
    }

    public static String getCppApiFolder(String path, String contestName) {
        return getContestPath(path, contestName) + File.separator + CPP_API_FOLDER;
    }

    public String getTournamentFile(int value) {
        return getTournamentFile(path, contestName, value);
    }

    public static String getTournamentFile(String path, String contestName, int value) {
        return getContestPath(path, contestName) + File.separator + getTournamentFilename(value);
    }

    public String getCompetitorsFile() {
        return getCompetitorsFile(path, contestName);
    }

    public static String getCompetitorsFile(String path, String contestName) {
        return getContestPath(path, contestName) + File.separator + COMPETITORS_FILE;
    }

    public static String getTournamentFilename(int value) {
        return String.format(TOURNAMENT_FILENAME, value);
    }


    @Override
    public String toString() {
        return "ContestConfig{" +
                "path='" + path + '\'' +
                ", contestName='" + contestName + '\'' +
                ", mode=" + mode +
                ", pauseBetweenBattles=" + pauseBetweenBattles +
                '}';
    }


    public static boolean removeConfigFile(String path) {
        File config = new File(getConfigFilePath(path));

        return config.delete();
    }
}
