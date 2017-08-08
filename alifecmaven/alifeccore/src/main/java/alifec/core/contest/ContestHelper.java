package alifec.core.contest;

import alifec.core.contest.tournament.TournamentFilter;
import alifec.core.exception.TournamentCorruptedException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

/**
 * Created by Sergio Del Castillo on 06/08/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class ContestHelper {

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
        Vector<String[]> battles = new Vector<>();
        Vector<String[]> backup = new Vector<>();

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
                System.out.println("Delete back up file [OK]");
            } else {
                System.out.println("Delete back up file [FAIL]");
            }
        }
        /*TODO: ver porque esto estaba acá, mepa que está al dope
        try {
            tournaments.newTournament(environment.getNames());
            System.out.println("Creating new Tournament [OK]");
        } catch (CreateTournamentException ex) {
            System.out.println("Creating new Tournament [FAIL]");
        }*/
    }

    public static boolean existConfigFile(String path) {
        if (path == null || path.equals("")) {
            return false;
        }

        String[] list = new File(path).list();

        if (list != null) {
            for (String names : list) {
                if (names.equals(ContestConfig.CONFIG_FILE)) {
                    return true;
                }
            }
        }
        return false;
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
        String NutrientFile = ContestConfig.getNutrientsFilePath(path, name);

        return new File(contestName).exists() &&
                new File(MOsFolder).exists() &&
                new File(ReportFolder).exists() &&
                new File(NutrientFile).exists();
    }

    private static boolean compareBattleLine(String[] a, String b[]) {
        if (a.length != b.length) return false;

        for (int i = 0; i < a.length; i++) {
            if (!a[i].toLowerCase().equalsIgnoreCase(b[i].toLowerCase()))
                return false;
        }
        return true;
    }

    private static void loadBattleFile(String n, Vector<String[]> b) {
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

                    b.addElement(tmp2);
                }
            }

        } catch (IOException ex) {
            System.out.println("Cant find battles.csv or battles_backup.csv");
            System.out.println("path: " + n);
        } finally {
            try {
                if (f != null) {
                    f.close();
                }
            } catch (IOException ignored) {
            }
        }
    }
}
