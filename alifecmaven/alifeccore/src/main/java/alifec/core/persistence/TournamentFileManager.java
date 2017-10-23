package alifec.core.persistence;

import alifec.core.contest.UnsuccessfulColonies;
import alifec.core.exception.TournamentCorruptedException;
import alifec.core.persistence.config.ContestConfig;
import alifec.core.persistence.filter.TournamentFilter;
import org.apache.logging.log4j.LogManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Sergio Del Castillo on 23/10/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class TournamentFileManager {
    static org.apache.logging.log4j.Logger logger = LogManager.getLogger(TournamentFileManager.class);

    public static List<String> listTournaments(String path) throws IOException {
        List<String> result = new ArrayList<>();

        Files.list(Paths.get(path))
                .filter(new TournamentFilter())
                .forEach(path1 -> {
                    result.add(path1.getFileName().toString());
                });

        return result;
    }

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

        List<String> tournaments = null;
        try {
            tournaments = TournamentFileManager.listTournaments(config.getContestPath());
        } catch (IOException e) {
            throw  new TournamentCorruptedException("The tournament list can not be loaded.", e);
        }

        if (tournaments.isEmpty()) {
            return new UnsuccessfulColonies(null, null, null, false);
        }

        String lastTournament = Collections.max(tournaments);

        String backupFilePath = config.getBattlesBackupFile(lastTournament);
        String battlesFilePath = config.getBattlesFile(lastTournament);

        //Check the existence of the backup file (battles_backup.csv file)
        if (!Files.isRegularFile(Paths.get(backupFilePath))) {
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
        try {
            Files.deleteIfExists(Paths.get(backupFilePath));
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        return new UnsuccessfulColonies(null, null, null, false);

    }
    private static boolean compareBattleLine(String[] a, String b[]) {
        //todo improve it
        if (a.length != b.length) return false;

        for (int i = 0; i < a.length; i++) {
            if (!a[i].toLowerCase().equalsIgnoreCase(b[i].toLowerCase()))
                return false;
        }
        return true;
    }
    private static void loadBattleFile(String n, List<String[]> b) {
        //todo improve it
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

    public static void deleteBattleBackupFile(ContestConfig config, UnsuccessfulColonies unsuccessful) {
        Path backupFile = Paths.get(config.getBattlesBackupFile(unsuccessful.getTournament()));

        if (Files.exists(backupFile)) {
            try {
                Files.delete(backupFile);
                logger.info("Delete back up file [OK]");
            } catch (IOException e) {
                logger.info("Delete back up file [FAIL]");
                logger.error(e.getMessage(), e);
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

}
