package alifec.core.contest;

import alifec.core.exception.SaveContestConfigException;
import org.apache.log4j.Logger;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * Created by Sergio Del Castillo on 05/08/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class ContestConfig {

    static Logger logger = org.apache.log4j.Logger.getLogger(CompileHelper.class);

    /**
     * List of nutrients that are used in contest.
     * generic form: "nutrient_id"
     * name : is the nutrient name used in each tournament
     */
    public static final String NUTRIENTS_FILE = "nutrients";

    public static final String BATTLES_BACKUP_FILENAME = "battles_backup.csv";
    public static final String BATTLES_FILENAME = "battles.csv";
    /**
     * Folder of source colonies.
     */
    public static final String MOS_FOLDER = "MOs";

    /**
     * Folder of reports.
     */
    public static final String REPORT_FOLDER = "Report";

    /**
     * Log Folder.
     */
    public static final String LOG_FOLDER = "Log";

    /**
     * Configuration file.
     */
    public static final String CONFIG_FILE = "config";

    /**
     * back up file
     */
    public static final String BACKUP_FOLDER = "Backup";

    public static final int PROGRAMMER_MODE = 0;

    public static final int COMPETITION_MODE = 1;

    public static final int DEFAULT_PAUSE_BETWEEN_BATTLES = 100;

    /**
     * Absolute path of Contest.
     */
    private String path = "";
    /**
     * name of contest
     */
    private String name = "";

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

    private boolean needRestart;

    private ContestConfig() {
        needRestart = false;
    }

    /**
     * Read the config File in the project.
     *
     * @param path path to read the config
     * @return true if is successfully
     * @throws IOException if can not find the config file
     */
    public static ContestConfig buildFromFile(String path) throws IOException {
        Properties property = new Properties();
        InputStream is = new FileInputStream(getConfigFilePath(path));

        property.load(is);
        ContestConfig config = new ContestConfig();
        config.setPath(path);
        for (Object object : property.keySet()) {

            if (!config.setProperty(object.toString(), property.getProperty(object.toString()))) {
                logger.warn("Can not set the property: " + object.toString() + "=" + property.getProperty(object.toString()));
            }
        }

        config.isValid();

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

    public boolean save() throws SaveContestConfigException {
        Properties property = new Properties();
        property.setProperty("url", path);
        property.setProperty("name", name);
        property.setProperty("mode", "" + mode);
        property.setProperty("pause between battles", "" + pauseBetweenBattles);

        try {
            property.store(new FileWriter(this.getConfigFilePath()),
                    "Configuration File\n Warning: do not modify this file");
        } catch (IOException e) {
            throw new SaveContestConfigException("Can not update the config file: " + getConfigFilePath(), this);
        }
        //the property was saved so the system should be restarted.
        needRestart = true;
        return true;
    }

    public boolean isValid() {
        if (pauseBetweenBattles < 0) {
            return false;
        }

        if (mode < 0 || mode > 1) {
            return false;
        }

        if (name.equalsIgnoreCase("")) {
            return false;
        }

        if (path.equalsIgnoreCase("")) {
            return false;
        }

        File f = new File(path);
        if (!f.exists())
            return false;

        f = new File(path + File.separator + name);

        return f.exists();
    }

    private boolean setProperty(String type, String option) {
        if (type == null || option == null) return false;

        type = type.trim().toLowerCase();
        option = option.trim();

        if (type.equals("") || option.equals(""))
            return false;

        if (type.equals("url")) {
            try {
                path = new File(option).getCanonicalPath();
            } catch (IOException ex) {
                return false;
            }
        } else if (type.equals("mos")) {
//			MOS_FOLDER = option;
            return false;
        } else if (type.equals("name")) {
            name = option;
        } else if (type.equals("mode")) {
            try {
                mode = Integer.parseInt(option);
            } catch (NumberFormatException ex) {
                return false;
            }
        } else if (type.equals("pause between battles")) {
            try {
                pauseBetweenBattles = Integer.parseInt(option);
            } catch (NumberFormatException ex) {
                return false;
            }
        }
        return true;
    }


    public void setPath(String path) {
        this.path = path;
    }

    public void setContestName(String name) {
        this.name = name;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public void setPauseBetweenBattles(int pauseBetweenBattles) {
        this.pauseBetweenBattles = pauseBetweenBattles;
    }

    public String getContestPath() {
        return getContestPath(this.path, this.name);
    }

    public static String getContestPath(String absolutePath, String contestName) {
        return absolutePath + File.separator + contestName;
    }

    public String getTournamentPath(String tournamentName) {
        return getContestPath() + File.separator + tournamentName;
    }

    public String getBattlesBackupFile(String tournamentName) {
        return getTournamentPath(tournamentName) + File.separator + BATTLES_BACKUP_FILENAME;
    }

    public String getBattlesFile(String tournamentName) {
        return getTournamentPath(tournamentName) + File.separator + BATTLES_FILENAME;
    }

    //public static String getBattlesFile()
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
        if (path == null || path.isEmpty()) path = ".";

        return path + File.separator + CONFIG_FILE;
    }

    public static boolean existsConfigFile(String path) {
        return new File(getConfigFilePath(path)).exists();
    }

    public String getNutrientsFilePath() {
        return getNutrientsFilePath(path, name);
    }

    public static String getNutrientsFilePath(String absolutePath, String contestName) {
        return getContestPath(absolutePath, contestName) + File.separator + NUTRIENTS_FILE;
    }

    public String getMOsPath() {
        return getMOsPath(path, name);
    }

    public static String getMOsPath(String absolutePath, String contestName) {
        return getContestPath(absolutePath, contestName) + File.separator + MOS_FOLDER;
    }

    public String getReportPath() {
        return getReportPath(path, name);
    }

    public static String getReportPath(String absolutePath, String contestName) {
        return getContestPath(absolutePath, contestName) + File.separator + REPORT_FOLDER;
    }

    public String getContestName() {
        return this.name;
    }

    public int getPauseBetweenBattles() {
        return this.pauseBetweenBattles;
    }

    public String getBackupPath() {
        return this.name + File.separator + BACKUP_FOLDER;
    }

    public String getPath() {
        return path;
    }



    public File getCompilationFilePath(String javaFile) {
        String logFolderPath = getLogFolder();
        File logFolder = new File(logFolderPath);
        if (!logFolder.exists()) {
            logFolder.mkdir();
        }

        String compilationFile = logFolderPath + File.separator;
        compilationFile += "log-" + javaFile.replace(".java", "") + "-";
        compilationFile += new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());

        return new File(compilationFile);
    }


    public  String getLogFolder(){
        return getLogFolder(this.path, this.name);
    }
    public static String getLogFolder(String path, String contestName){
        return getContestPath(path, contestName) + File.separator + ContestConfig.LOG_FOLDER;
    }
    @Override
    public String toString() {
        return "ContestConfig{" +
                "path='" + path + '\'' +
                ", name='" + name + '\'' +
                ", mode=" + mode +
                ", needRestart=" + needRestart+
                ", pauseBetweenBattles=" + pauseBetweenBattles +
                '}';
    }

    public boolean isNeedRestart() {
        return needRestart;
    }
}
