package alifec.core.persistence;

import alifec.core.contest.Battle;
import alifec.core.contest.UnsuccessfulColonies;
import alifec.core.exception.TournamentCorruptedException;
import alifec.core.persistence.custom.BattlesCollector;
import alifec.core.persistence.config.ContestConfig;
import alifec.core.persistence.filter.TournamentFilter;
import alifec.core.persistence.custom.ExcludeBattlesPredicate;
import org.apache.logging.log4j.LogManager;

import java.io.*;
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


    private final Path path;
    private final String file;

    public TournamentFileManager(String battleFIle, boolean createFiles) throws IOException {
        this.file = battleFIle;
        this.path = Paths.get(battleFIle);

        if (createFiles) {
            //create the tournament folder if it does not exists.
            if (Files.notExists(path.getParent())) {
                Files.createDirectory(path.getParent());
            }

            //create the battle file if it does not exists.
            if (Files.notExists(path)) {
                Files.createFile(path);
            }
        }
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
            //todo: improve it
            tournaments = ContestFileManager.listTournaments(config.getContestPath());
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

        //todo : improve it... the best way is to use the Battle class.
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

    public void append(Battle battle) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path,
                StandardOpenOption.APPEND,
                StandardOpenOption.CREATE)) {
            writer.write(battle.toCsv() + '\n');
        }
    }

    /**
     * Delete the battles list from battles.csv file
     *
     * @throws IOException
     */
    public void deleteFromBattlesFile(List<Battle> battles) throws IOException {
        Path tempFile = Paths.get(file + TEMP_SUFFIX);

        //rename the file
        Files.move(path, tempFile);
        List<String> filteredList;

        try (Stream<String> stream = Files.lines(tempFile)) {
            filteredList = stream
                    .filter(new ExcludeBattlesPredicate(battles))
                    .collect(Collectors.toList());

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw e;
        }

        saveAll(path, filteredList);

        //clean temporal file
        Files.delete(tempFile);
    }

    public void saveAll(String path, List<?> battles) throws IOException {
        saveAll(Paths.get(path), battles);
    }

    public void saveAll(List<?> battles) throws IOException {
        saveAll(path, battles);
    }

    public void saveAll(Path path, List<?> battles) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            for (Object line : battles) {
                if (line instanceof Battle)
                    writer.write(((Battle) line).toCsv() + '\n');
                else
                    writer.write(line.toString() + '\n');
            }
        }
    }

    public List<Battle> readAll() throws IOException {
        return Files.lines(path).collect(new BattlesCollector());
    }

    public List<Battle> readAll(String path) throws IOException {
        return Files.lines(Paths.get(path)).collect(new BattlesCollector());
    }

    public void deleteFile(String file) throws IOException {
        Files.deleteIfExists(Paths.get(file));
    }

    public boolean existsFile(String file) {
        return Files.exists(Paths.get(file));
    }


}
