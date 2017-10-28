package alifec.core.persistence;

import alifec.core.contest.Battle;
import alifec.core.contest.UnsuccessfulColonies;
import alifec.core.exception.TournamentCorruptedException;
import alifec.core.persistence.collector.BattlesCollector;
import alifec.core.persistence.config.ContestConfig;
import alifec.core.persistence.filter.TournamentFilter;
import alifec.core.persistence.predicate.ExcludeBattlesPredicate;
import org.apache.logging.log4j.LogManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Sergio Del Castillo on 23/10/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class TournamentFileManager {
    static org.apache.logging.log4j.Logger logger = LogManager.getLogger(TournamentFileManager.class);
    private static final String TEMP_SUFFIX = ".tmp";

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
            throw new TournamentCorruptedException("The tournament list can not be loaded.", e);
        }

        if (tournaments.isEmpty()) {
            return new UnsuccessfulColonies(null, null, null, false);
        }

        String lastTournament = Collections.max(tournaments);

        String backupFilePath = config.getBattlesTargetRunFile(lastTournament);
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
        Path backupFile = Paths.get(config.getBattlesTargetRunFile(unsuccessful.getTournament()));

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

    public void append(String path, Battle battle) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(path), StandardOpenOption.APPEND, StandardOpenOption.CREATE)) {
            writer.write(battle.toCsv() + '\n');
        }
    }

    /**
     * Delete the battles list from battles.csv file
     *
     * @param path of battle.csv file
     * @throws IOException
     */
    public void delete(String path, List<Battle> battles) throws IOException {
        Path tempFile = Paths.get(path + TEMP_SUFFIX);
        Path originalFile = Paths.get(path);

        //rename the file
        Files.move(originalFile, tempFile);
        List<String> filteredList;

        try (Stream<String> stream = Files.lines(tempFile)) {
            filteredList = stream
                    .filter(new ExcludeBattlesPredicate(battles))
                    .collect(Collectors.toList());

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw e;
        }

        saveAll(originalFile, filteredList);

        //clean temporal file
        Files.delete(tempFile);
    }

    public void saveAll(String pathToFile, List<Battle> battles) throws IOException {
        Path path = Paths.get(pathToFile);

        saveAll(path, battles);
    }

    private void saveAll(Path path, List<?> battles) throws IOException {
        if (Files.notExists(path.getParent())) {
            Files.createDirectory(path.getParent());
        }

        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            for (Object line : battles) {
                if (line instanceof Battle)
                    writer.write(((Battle)line).toCsv() + '\n');
                else
                    writer.write(line.toString() + '\n');
            }
        }
    }

    public List<Battle> readAll(String file) throws IOException {
        Path path = Paths.get(file);

        return Files.lines(path).collect(new BattlesCollector());
    }

    public void deleteFile(String file) throws IOException {
        Files.deleteIfExists(Paths.get(file));
    }

    public boolean existsFile(String file) {
        return Files.exists(Paths.get(file));
    }
}
