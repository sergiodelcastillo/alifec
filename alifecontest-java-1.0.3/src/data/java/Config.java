package data.java;

import controller.java.Validator;
import controller.java.contest.ContestMode;
import exceptions.CreateConfigException;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;


/**
 * Created by IntelliJ IDEA.
 * User: Sergio Del Castillo
 * E-mail: sergio.jose.delcastillo@gmail.com
 * Date: Apr 15, 2010
 * Time: 2:21:01 AM
 */
public class Config {

    /* Environment path configuration*/

    public static final String MOS_FOLDER = "MOs";
    public static final String MOS_LOG_FOLDER = "log";
    public static final String MOS_BACKUP_FOLDER = "backup";
    public static final String REPORT_FOLDER = "report";
    public static final String LOG_FOLDER = "log";
    public static final String BIN_FOLDER = "bin";
    public static final String SRC_FOLDER = "src";

    public static final String NUTRIENTS_FILE = "nutrients";
    public static final String COMPETITORS_FILE = "competitors";
    public static final String CONFIG_FILE = "config";
    public static final String BATTLES_FILE = "battles.csv";

    public static final String BATTLES_BACKUP_FILE = "battles_backup.csv";


    /* Environment configuration */
    private int mode = ContestMode.PROGRAMMER_MODE;
    private int pause = 200;
    private String contestPath;
    private String contestName;


    private static Config instance = null;


    /**
     * private constructor. It allow us to define the singleton pattern.
     */
    private Config() {
    }

    /**
     * function to create and return a instance of a Config class.
     *
     * @return a instance of a Config class.
     *         return null if the config can not be loaded successfully.
     */
    public static Config getInstance() {
        if (instance == null) {
            reload();
        }

        return instance;
    }

    /**
     * Read and create a instance of the config file using default path.
     */
    public static void reload() {
        readConfig(getAbsoluteDefaultConfig());
    }

    /**
     * Read and create a instance of the config file.
     *
     * @param path is the path of the config file
     * @return true if was successful
     */
    public static boolean readConfig(String path) {
        Config config = new Config();


        try {
            Properties property = new Properties();
            InputStream is = new FileInputStream(path);

            property.load(is);

            for (Object key : property.keySet()) {
                config.setProperty(key.toString(), property.getProperty(key.toString()));
            }


            //ensure path
            if (config.contestPath == null) {
                config.contestPath = getAbsoluteDefaultPath();
            }

            //verify integrity.
            if (!config.validate()) {
                Log.printlnAndSave("Read Config [FAIL]");
                Log.printlnAndSave("Invalid config: " + config.toString());
                return false;
            }

            //if OK --> create instance.
            instance = config;
            return true;

        } catch (IOException ex) {
            Log.printlnAndSave("Read Config [FAIL]", ex);
            return false;
        }
    }

    /**
     * Create a config file with the default parameters.
     * The config will placed in the default config folder:
     *
     * @param contestName the name of the contest
     * @return true if it was successful. False in other case.
     * @see data.java.Config#getAbsoluteContestName()
     */
    public static void update(String contestName) throws CreateConfigException {
        update(contestName, 0, 200);
    }



    /**
     * Create a config file with the default parameters.
     *
     * @param contestName the name of the contest
     * @param mode        is the contest mode
     * @param pause       is the pause between battles
     * @return true if it was successful. False in other case.
     */
    public static void update(String contestName, int mode, int pause) throws CreateConfigException {
        try {
            Properties property = new Properties();

            property.setProperty("name", contestName);
            property.setProperty("mode", "" + mode);
            property.setProperty("pause between battle", "" + pause);

            property.store(new FileWriter(getAbsoluteDefaultPath() + File.separator + CONFIG_FILE),
                    "Configuration File\n Warning: do not modify this file");

            if (instance != null) {
                instance.contestName = contestName;
                instance.mode = mode;
                instance.pause = pause;
            }

        } catch (Exception ex) {
            throw new CreateConfigException("Create Config [FAIL]", ex);
        }
    }

    /**
     * Update a config file with the default parameters.
     *
     * @param path        the absolute folder
     * @param contestName the name of the contest
     * @param mode        is the contest mode
     * @param pause       is the pause between battles
     * @return true if it was successful. False in other case.
     */
    public static boolean update(String path, String contestName, int mode, int pause) {
        try {
            Properties property = new Properties();

            property.setProperty("path", path);
            property.setProperty("name", contestName);
            property.setProperty("mode", "" + mode);
            property.setProperty("pause between battle", "" + pause);

            property.store(new FileWriter(path + File.separator + CONFIG_FILE),
                    "Configuration File\n Warning: do not modify this file");

            if (instance != null) {
                instance.contestName = contestName;
                instance.mode = mode;
                instance.pause = pause;
                instance.contestPath = path;
            }

        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Verify if the config has a right configuration.
     *
     * @return true if the config is valid
     */
    private boolean validate() {
        return Validator.validateContestName(contestName) &&
                Validator.validateMode(mode) &&
                Validator.validatePath(contestPath) &&
                Validator.validatePause(pause);
    }

    /**
     * Read the current user directory (<b>user.dir</b> property) and create
     * the path of the configuration file as follows:
     * - "user.dir"/config
     *
     * @return the absolute path of configuration file.
     */
    public static String getAbsoluteDefaultConfig() {
        return getAbsoluteDefaultPath() + File.separator + CONFIG_FILE;
    }

    public static String getAbsoluteDefaultPath() {
        return new File(System.getProperty("user.dir")).getAbsolutePath();
    }

    /**
     * @return true if exist the default config. False in other case.
     */
    public static boolean existsDefaultConfig() {
        File f = new File(getAbsoluteDefaultConfig());
        return f.exists() && f.isFile() && f.canRead();
    }

    /**
     * eat the property <b>key</b> as <b>value</b>.
     * key can be: name, pause between battle and mode.
     *
     * @param key   is the key of the property
     * @param value is the value of the property
     */
    private void setProperty(String key, String value) {
        if (key.equals("name")) {
            setContestFolder(value);
        } else if (key.equals("pause between battle")) {
            setPause(value);
        } else if (key.equals("mode")) {
            setMode(value);
        } else if (key.equals("path")) {
            setPath(value);
        }
    }

    /**
     * @return return the absolute path of the MOs folder.
     */
    public String getAbsoluteMOsFolder() {
        return getAbsoluteContestName() + File.separator + MOS_FOLDER;
    }

    /**
     * @param colonyName name of a java colony
     * @return the absolute package
     */
    public String getAbsoluteMOsPackage(String colonyName) {
        return "MOs." + colonyName;
    }

    /**
     * @return the absolute path of the back up folder.
     */
    public String getAbsoluteBackUpFolder() {
        return getAbsoluteContestName() + File.separator + MOS_BACKUP_FOLDER;
    }

    /**
     * @return the absolute path of the nutrient file.
     */
    public String getAbsoluteNutrient() {
        return getAbsoluteContestName() + File.separator + NUTRIENTS_FILE;
    }

    /**
     * @return the absolute path of the competitors file.
     */
    public String getAbsoluteInfo() {
        return getAbsoluteContestName() + File.separator + COMPETITORS_FILE;
    }


    /**
     * @param tournament The name of the current tournament
     * @return return absolute path to battles file into the current tournament.
     */
    public String getAbsoluteBattleFile(String tournament) {
        return getAbsoluteContestName() + File.separator + tournament + File.separator + BATTLES_FILE;
    }

    /**
     * @param tournament The name of the current tournament
     * @return return absolute path to battles_backup file into the current tournament.
     */
    public String getAbsoluteBattleBackUp(String tournament) {
        return getAbsoluteContestName() + File.separator + tournament + File.separator + BATTLES_BACKUP_FILE;
    }

    /**
     * @param tName the name of the current tournament
     * @return the absolute path of the current tournament
     */
    public String getAbsoluteTournamentFolder(String tName) {
        return getAbsoluteContestName() + File.separator + tName;

    }

    /**
     * @return a string with the absolute path to the log folder.
     */
    /*public String getAbsoluteLogFolder() {
        return getAbsoluteDefaultPath() + File.separator + LOG_FOLDER;
    } */
    public String getAbsoluteMOsBackUpFile() {
        return getAbsoluteBackUpFolder() + File.separator + "backup-" + getFormattedDate() + ".zip";

    }

    public String getAbsoluteReportTxt() {
        return getAbsoluteReportFolder() + File.separator + "report-" + getFormattedDate() + ".txt";
    }

    public String getAbsoluteReportCsv() {
        return getAbsoluteReportFolder() + File.separator + "report-" + getFormattedDate() + ".csv";
    }

    /**
     * @return the absolute path of the report folder.
     */
    public String getAbsoluteReportFolder() {
        return getAbsoluteContestName() + File.separator + REPORT_FOLDER;
    }

    /**
     * @return the absolute path to the bin folder
     */
    public String getAbsoluteBinFolder() {
        return getAbsoluteDefaultPath() + File.separator + BIN_FOLDER;
    }

    public String getAbsoluteBinMOsFolder() {
        return getAbsoluteBinFolder() + File.separator + SRC_FOLDER;
    }

    public String getAbsoluteBinLibCppFolder() {
        return getAbsoluteBinMOsFolder() + File.separator + MOS_FOLDER;
    }

    /**
     * @return the absolute path of the contest folder.
     */
    public String getAbsoluteContestName() {
        return getAbsoluteContestName(contestName);
    }

    /**
     * @return the absolute path of the contest folder.
     */
    public String getAbsoluteContestName(String name) {
        return contestPath + File.separator + name;
    }

    /**
     * @return the name of the current contest.
     */
    public String getContestName() {
        return contestName;
    }

    /**
     * @return return the path of the contest without the contest name.
     */
    public String getAbsoluteContestPath() {
        return contestPath;
    }


    /**
     * @return the mode of the contest. Default value = 0.
     */
    public int getMode() {
        return mode;
    }

    /**
     * @return the pause between battle.
     *         This pause is between battle witch they are running.
     *         Default value = 200
     */
    public int getPauseBetweenBattles() {
        return pause;
    }

    @Override
    public String toString() {
        return "{path:" + contestPath + "; name:" + this.contestName + "; mode:" + mode + "; pause;" + pause + "}";
    }

    static String getFormattedDate() {
        return new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
    }

    private void setContestFolder(String value) {
        if (!Validator.validateContestName(value)) {
            System.out.println("Config: invalid line \"name=" + value + "\"");
            return;
        }
        this.contestName = value;
    }

    private void setPause(String value) {
        if (!Validator.validatePause(value)) {
            System.out.println("Config: invalid line \"pause between battles=" + value + "\"");
            return;
        }

        pause = Integer.parseInt(value);
    }


    private void setMode(String value) {
        if (!Validator.validateMode(value)) {
            System.out.println("Config: invalid line \"mode=" + value + "\"");
            return;
        }

        mode = Integer.parseInt(value);


    }

    private void setPath(String value) {
        if (!Validator.validatePath(value)) {
            System.out.println("Config: invalid line \"path=" + value + "\"");
            return;
        }

        try {
            contestPath = new File(value).getAbsoluteFile().getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}