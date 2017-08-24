package alifec.core.contest;

import alifec.core.persistence.filter.TournamentFilter;
import alifec.core.exception.CreateContestFolderException;
import alifec.core.exception.TournamentCorruptedException;
import alifec.core.persistence.filter.ContestFolderFilter;
import alifec.core.simulation.Agar;
import alifec.core.simulation.nutrients.Nutrient;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Sergio Del Castillo on 06/08/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class ContestHelper {

    static Logger logger = org.apache.log4j.Logger.getLogger(ContestHelper.class);

    /**
     * Find the mode is competition and the Alifec software was closed unsuccessful then
     * it could be inconsistency in saved battles. This method helps to find unsaved run and
     * give the possibility to take an action.
     *
     * @param config
     * @return
     * @throws TournamentCorruptedException
     */
    public static UnsuccessfulColonies findFinishedUnsuccessful(ContestConfig config) throws TournamentCorruptedException {
        if (config.getMode() == ContestConfig.PROGRAMMER_MODE) {
            return new UnsuccessfulColonies(null, null, null, false);
        }
        String[] tournamentsName = new File(config.getContestName()).list(new TournamentFilter());
        if (tournamentsName == null) {
            return new UnsuccessfulColonies(null, null, null, false);
        }

        List<String> tournamentList = new ArrayList<>();

        Collections.addAll(tournamentList, tournamentsName);

        String lastTournament = Collections.max(tournamentList);

        String backupFilePath = config.getBattlesBackupFile(lastTournament);
        String battlesFilePath = config.getBattlesFile(lastTournament);

        //Check the existence of the backup file (battles_backup.csv file)
        if (!new File(backupFilePath).exists()) {
            return new UnsuccessfulColonies(null, null, null, false);
        }

        //find unsaved battles.
        List<String[]> battles = new ArrayList<>();
        List<String[]> backup = new ArrayList<>();

        loadBattleFile(battlesFilePath, battles);
        loadBattleFile(backupFilePath, backup);

        int index = 0;
        for (; index < battles.size(); index++) {
            if (!compareBattleLine(battles.get(index), backup.get(index))) {
                throw new TournamentCorruptedException("Tournament " + lastTournament + " is corrupt. Please verify battles.csv and battles_backup.csv files.");
            }
        }

        if (backup.size() > index) {
            return new UnsuccessfulColonies(lastTournament, backup.get(index)[0], backup.get(index)[1], true);
        }

        //try to remove battles_backup.csv file because it is equals to battles.csv file.
        new File(backupFilePath).delete();

        return new UnsuccessfulColonies(null, null, null, false);

    }

    public static void deleteBattleBackupFile(ContestConfig config, UnsuccessfulColonies unsuccessful) {
        File backupFile = new File(config.getBattlesBackupFile(unsuccessful.getTournament()));

        if (backupFile.exists()) {
            if (backupFile.delete()) {
                logger.info("Delete back up file [OK]");
            } else {
                logger.info("Delete back up file [FAIL]");
            }
        }
        /*TODO: ver porque esto estaba acá, mepa que está al dope
        try {
            tournaments.newTournament(environment.getNames());
            logger.info("Creating new Tournament [OK]");
        } catch (CreateTournamentException ex) {
            logger.info("Creating new Tournament [FAIL]");
        }*/
    }

    public static List<String> listContest(String path) {
        String list[] = new File(path).list(new ContestFolderFilter());
        List<String> results = new ArrayList<>();

        if (list != null) {
            for (String name : list) {
                if (checkContestFolder(path, name)) {
                    results.add(name);
                }

            }
        }
        return results;
    }

    private static boolean checkContestFolder(String path, String name) {
        if (path == null || path.equals("")) {
            return false;
        }
        if (name == null || name.equals("")) {
            return false;
        }

        String contestName = ContestConfig.getContestPath(path, name);
        String MOsFolder = ContestConfig.getMOsPath(path, name);
        String ReportFolder = ContestConfig.getReportPath(path, name);
        String CppFolder = ContestConfig.getCppApiFolder(path, name);
        String NutrientFile = ContestConfig.getNutrientsFilePath(path, name);
        String BackupFolder = ContestConfig.getBackupFolder(path, name);

        return new File(contestName).exists() &&
                new File(MOsFolder).exists() &&
                new File(ReportFolder).exists() &&
                new File(CppFolder).exists() &&
                new File(NutrientFile).exists() &&
                new File(BackupFolder).exists();
    }

    private static boolean compareBattleLine(String[] a, String b[]) {
        if (a.length != b.length) return false;

        for (int i = 0; i < a.length; i++) {
            if (!a[i].toLowerCase().equalsIgnoreCase(b[i].toLowerCase()))
                return false;
        }
        return true;
    }

    private static void loadBattleFile(String n, List<String[]> b) {
        BufferedReader f = null;
        String line;

        try {
            f = new BufferedReader(new FileReader(n));

            while ((line = f.readLine()) != null) {
                String[] tmp = line.split(",");
                if (tmp.length == 3) {
                    String tmp2[] = new String[3];

                    tmp2[0] = tmp[0];
                    tmp2[1] = tmp[1];
                    tmp2[2] = tmp[2];

                    b.add(tmp2);
                }
            }

        } catch (IOException ex) {
            logger.info("Cant find battles.csv or battles_backup.csv", ex);
        } finally {
            try {
                if (f != null) {
                    f.close();
                }
            } catch (IOException ignored) {
            }
        }
    }

    public static void buildNewContestFolder(ContestConfig config, boolean createExamples) throws CreateContestFolderException {
        createFolder(config.getContestPath());
        createFolder(config.getMOsPath());
        createFolder(config.getReportPath());
        createFolder(config.getCppApiFolder());
        createFolder(config.getLogFolder());
        createFolder(config.getBackupFolder());

        File nutrientsFile = new File(config.getNutrientsFilePath());
        PrintWriter writter = null;

        try {
            writter = new PrintWriter(nutrientsFile);

            for (Nutrient n : Agar.nutrient)
                writter.println(n.getID());
        } catch (IOException ex) {
            logger.error("Creating file: " + nutrientsFile + " [FAIL]");
            throw new CreateContestFolderException("Cant not create the file: " + config.getNutrientsFilePath());
        } finally {
            if (writter != null) writter.close();
        }

        logger.info("Creating file: " + nutrientsFile + " [OK]");

        //copy the cpp api
        if (!createCppApi(config.getCppApiFolder())) {
            logger.error("Creating cpp api to: " + config.getCppApiFolder() + " [FAIL]");
            throw new CreateContestFolderException("Cant not create the cpp api files in dir: " + config.getCppApiFolder());
        }
        logger.info("Creating cpp api to: " + config.getCppApiFolder() + " [OK]");

        //Create examples
        logger.info("Create examples: " + (createExamples ? "YES" : "NO"));
        if (createExamples) {
            createExamples(config.getMOsPath());
        }
    }

    private static void createFolder(String folder) throws CreateContestFolderException {
        if (!new File(folder).mkdir()) {
            logger.error("Creating folder: " + folder + " [FAIL]");
            throw new CreateContestFolderException("Can not create the folder: " + folder);
        }

        logger.info("Creating folder: " + folder + " [OK]");
    }

    public static void createExamples(String MOsFolder) {
        try {
            logger.info("Generating examples in folder " + MOsFolder);
            File source = new File(Contest.class.getClass().getResource("/examples/").toURI());

            Files.walk(source.toPath()).forEach(path -> {
                try {
                    File target = new File(MOsFolder + File.separator + path.getFileName());

                    if (new File(path.toUri()).isFile())
                        Files.copy(path, target.toPath());
                    logger.info("Generated file: " + target.getAbsolutePath());
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            });
        } catch (URISyntaxException | IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    private static boolean createCppApi(String targetFolder) {
        try {
            File source = new File(Contest.class.getClass().getResource("/cpp/").toURI());
            final boolean[] isOK = {true};

            Files.walk(source.toPath()).forEach(path -> {
                try {
                    File target = new File(targetFolder + File.separator + path.getFileName());

                    if (new File(path.toUri()).isFile()) {
                        Files.copy(path, target.toPath());
                        logger.info("Copying file: " + target.getAbsolutePath());
                    }
                } catch (IOException e) {
                    isOK[0] = false;
                    logger.error(e.getMessage(), e);

                }
            });
            return isOK[0];
        } catch (URISyntaxException | IOException ex) {
            logger.error(ex.getMessage(), ex);
            return false;
        }
    }

    public static void updateNutrient(ContestConfig config, int[] nutrients) throws IOException {
        //todo: improve it
        String url = config.getNutrientsFilePath();

        new File(url).renameTo(new File(url + "_backup"));

        File newNutrient = new File(url);
        PrintWriter pw = new PrintWriter(newNutrient);

        for (int nutriID : nutrients)
            pw.println(nutriID);

        pw.close();

        new File(url + "_backup").delete();
    }
}
