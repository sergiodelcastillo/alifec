package alifec.core.persistence.config;

import alifec.core.exception.*;
import alifec.core.simulation.Agar;
import alifec.core.simulation.nutrient.function.*;
import alifec.core.validation.ContestConfigValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Sergio Del Castillo on 05/08/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class ContestConfig {

    static Logger logger = LogManager.getLogger(ContestConfig.class);

    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String DATETIME_FORMAT = "yyyyMMdd-HHmmss";

    public static final String BASE_APP_FOLDER = "app";
    public static final String BASE_DATA_FOLDER = "data";

    private static final String BATTLES_RUN_FILE = "battles_run.csv";
    private static final String BATTLES_FILE = "battles.csv";
    private static final String SIMULATION_RUN = "battles.run";
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

    public static final String REPORT_TXT_FORMAT = "%-20s%-20s%-20s%-20s" + System.lineSeparator();
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
    public static final String COMPILER_CONFIG_FILE = "compiler.properties";

    public static final String COMPILATION_TARGET_FOLDER = "compiled";
    public static final String COMPILATION_LOG_FILENAME = "log-%s-%s";

    /**
     * back up file
     */
    public static final String BACKUP_FOLDER = "Backup";
    public static final String BACKUP_FILENAME_ZIP = "backup-%s.zip";

    public static final String CONTEST_NAME_PREFIX = "Contest-";
    public static final String TOURNAMENT_PREFIX = "Tournament-";
    public static final String TOURNAMENT_FILENAME = "Tournament-%03d";

    public static final int PROGRAMMER_MODE = 0;

    public static final int COMPETITION_MODE = 1;

    public static final int DEFAULT_PAUSE_BETWEEN_BATTLES = 100;

    private static final String PROPERTY_CONTEST_NAME_KEY = "contest_name";
    private static final String PROPERTY_PAUSE_BETWEEN_BATTLES_KEY = "pause_between_battles";
    private static final String PROPERTY_MODE_KEY = "contest_mode";
    private static final String PROPERTY_NUTRIENTS_KEY = "nutrients";

    private static final Integer[] DEFAULT_NUTRIENTS = new Integer[]{
            InclinedPlaneFunction.ID,
            VerticalBarFunction.ID,
            RingsFunction.ID,
            LatticeFunction.ID,
            TwoGaussiansFunction.ID,
            FamineFunction.ID
    };

    /**
     * Absolute path of Contest.
     */
    private final String path;
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

    private List<Integer> nutrients = new ArrayList<>();

    private ContestConfigValidator validator = new ContestConfigValidator();

    private StringBuilder builder;

    /**
     * Read the config File in the project.
     *
     * @return true if is successfully
     * @throws IOException         if can not find the config file
     * @throws ConfigFileException if the config file is not valid
     */
    public ContestConfig() throws ConfigFileException {
        try {
            path = getDefaultPath();
            setDefaults();

            Properties property = new Properties();
            InputStream is = new FileInputStream(getConfigFilePath(path));

            property.load(is);

            for (Object object : property.keySet()) {
                if (!setProperty(object.toString(), property.getProperty(object.toString()))) {
                    logger.warn("Can not set the property: " + object.toString() + "=" + property.getProperty(object.toString()));
                }
            }

            init();

        } catch (FileNotFoundException ex) {
            throw new ConfigFileNotFoundException("The config file was not found.", ex);
        } catch (IOException e) {
            throw new ConfigFileReadException("Error reading the properties in config file", e);
        }
    }

    public ContestConfig(String contestName) throws InvalidUserDirException {
        this(getDefaultPath(), contestName);
    }

    public ContestConfig(String path, String contestName) {
        this.path = path;
        setDefaults();
        setContestName(contestName);
        init();
    }

    private void init() {
        builder = new StringBuilder();
    }

    public static String getDefaultPath() throws InvalidUserDirException {
        try {
            return Paths.get(System.getProperty("user.dir")).toFile().getCanonicalPath();
        } catch (IOException e) {
            throw new InvalidUserDirException("Error detecting the user directory", e);
        }
    }

    public void setDefaults() {
        setMode(ContestConfig.PROGRAMMER_MODE);
        setPauseBetweenBattles(ContestConfig.DEFAULT_PAUSE_BETWEEN_BATTLES);
        setNutrients(Arrays.asList(DEFAULT_NUTRIENTS));
    }

    public void save() throws ConfigFileWriteException {
        Properties property = new Properties();

        property.setProperty(PROPERTY_CONTEST_NAME_KEY, contestName);
        property.setProperty(PROPERTY_MODE_KEY, Integer.toString(mode));
        property.setProperty(PROPERTY_PAUSE_BETWEEN_BATTLES_KEY, Integer.toString(pauseBetweenBattles));
        property.setProperty(PROPERTY_NUTRIENTS_KEY, String.join(",", nutrientsToString()));

        try {
            String basePath = getBaseAppFolder();

            if (Files.notExists(Paths.get(basePath))) {
                throw new ConfigFileWriteException("The base path can not be found: " + basePath, null, this);
            }
            property.store(new FileWriter(this.getConfigFilePath()),
                    "Configuration File\n Warning: do not modify this file");
        } catch (IOException e) {
            throw new ConfigFileWriteException("Can not update the config file: " + getConfigFilePath(), e, this);
        }
        //the property was saved so the system should be restarted.
    }

    private String nutrientsToString() {
        builder.delete(0, builder.length());

        for (Integer nutrient : nutrients) {
            builder.append(nutrient);
            builder.append(",");
        }
        return builder.toString();
    }


    private boolean setProperty(String type, String option) {
        if (type == null || option == null) return false;

        type = type.trim().toLowerCase();
        option = option.trim();

        if (type.isEmpty() || option.isEmpty())
            return false;

        switch (type) {
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
            case PROPERTY_NUTRIENTS_KEY:
                try {
                    nutrients.clear();
                    String[] temp = option.split(",");

                    for (String nutrientString : temp) {
                        try {
                            Integer nutrientInt = Integer.parseInt(nutrientString);

                            if (Agar.existsId(nutrientInt))
                                nutrients.add(nutrientInt);
                            else
                                logger.warn("Nutrient Id unknown: " + nutrientString);
                        } catch (NumberFormatException ex) {
                            logger.warn("Can not interpret Nutrient id : " + nutrientString);
                        }
                    }

                } catch (Exception ex) {
                    logger.error(ex.getMessage(), ex);
                    return false;
                }

        }
        return true;
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
        return getBaseDataFolder() + File.separator + this.contestName;
    }

    public String getBaseDataFolder() {
        return getBaseDataFolder(path);
    }

    public static String getBaseDataFolder(String path) {
        return path + File.separator + BASE_DATA_FOLDER;
    }

    public static String getDefaultBaseDataFolder() throws ConfigFileException {
        return getDefaultPath() + File.separator + BASE_DATA_FOLDER;
    }

    public String getTournamentPath(String tournamentName) {
        return getContestPath() + File.separator + tournamentName;
    }

    public String getBattlesTargetRunFile(String tournamentName) {
        return getTournamentPath(tournamentName) + File.separator + BATTLES_RUN_FILE;
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
        return getConfigFilePath(path);
    }

    public String getConfigFilePath(String path) {
        return getBaseAppFolder(path) + File.separator + CONFIG_FILE;
    }

    public String getBaseAppFolder() {
        return getBaseAppFolder(path);
    }

    public String getBaseAppFolder(String path) {
        if (path == null || path.isEmpty()) path = ".";

        return path + File.separator + BASE_APP_FOLDER;
    }

    public String getMOsPath() {
        return getContestPath() + File.separator + MOS_FOLDER;
    }

    public String getReportPath() {
        return getContestPath() + File.separator + REPORT_FOLDER;
    }

    public String getReportFilenameTxt() {
        String dateString = new SimpleDateFormat(DATE_FORMAT).format(new Date());

        return getReportPath() + File.separator +
                String.format(REPORT_FILENAME_TXT, dateString);
    }


    public String getReportFilenameCsv() {
        String dateString = new SimpleDateFormat(DATE_FORMAT).format(new Date());

        return getReportPath() + File.separator +
                String.format(REPORT_FILENAME_CSV, dateString);
    }

    public String getContestName() {
        return this.contestName;
    }

    public int getPauseBetweenBattles() {
        return this.pauseBetweenBattles;
    }

    public String getBackupFolder() {
        return getContestPath() + File.separator + BACKUP_FOLDER;
    }

    public String getBackupFile() {
        return getBackupFolder() + File.separator +
                String.format(BACKUP_FILENAME_ZIP,
                        new SimpleDateFormat(DATETIME_FORMAT).format(new Date()));
    }

    public String getPath() {
        return path;
    }


    public String getLogFileName(String javaFile) {
        return String.format(COMPILATION_LOG_FILENAME,
                javaFile.replace(".java", ""),
                new SimpleDateFormat(DATETIME_FORMAT).format(new Date()));
    }

    public String getLogFolder() {
        return getContestPath() + File.separator + ContestConfig.LOG_FOLDER;
    }

    public String getCompilationTarget() {
        return getContestPath() + File.separator + ContestConfig.COMPILATION_TARGET_FOLDER;
    }


    public String getCppApiFolder() {
        return getContestPath() + File.separator + CPP_API_FOLDER;
    }


    public String getTournamentFile(int value) {
        return getContestPath() + File.separator + getTournamentFilename(value);
    }

    public String getCompetitorsFile() {
        return getContestPath() + File.separator + COMPETITORS_FILE;
    }


    public String getTournamentFilename(int value) {
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

    public void setNutrients(List<Integer> nut) {
        nutrients.clear();
        nutrients.addAll(nut);
    }

    public List<Integer> getNutrients() {
        return nutrients;
    }

    public String getCompilerConfigFile() {
        return getBaseAppFolder() + File.separator + COMPILER_CONFIG_FILE;
    }

    public void validate() throws ValidationException {
        validator.validate(this);
    }

    public String getSimulationRunFile(String tournament) {
        return getTournamentPath(tournament) + File.separator + SIMULATION_RUN;
    }
}
