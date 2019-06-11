package alifec.core.persistence;

import alifec.core.contest.Battle;
import alifec.core.persistence.custom.BattlesFunction;
import alifec.core.persistence.custom.ExcludeBattlesPredicate;
import alifec.core.persistence.custom.NotNullPredicate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Sergio Del Castillo on 23/10/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class TournamentFileManager {
    private static final String TEMP_SUFFIX = ".tmp";
    private static final String BATTLE_CSV_FORMAT = "%s,%s,%s,%f,%f" + System.lineSeparator();
    private final Path path;
    private final String file;
    Logger logger = LogManager.getLogger(getClass());

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


    public void append(Battle battle) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path,
                StandardOpenOption.APPEND,
                StandardOpenOption.CREATE)) {
            writeBattleLine(battle, writer);
        }
    }

    private void writeBattleLine(Battle battle, BufferedWriter writer) throws IOException {
        writer.write(String.format(Locale.ROOT, BATTLE_CSV_FORMAT,
                battle.getFirstColony(),
                battle.getSecondColony(),
                battle.getNutrient(),
                battle.getFirstEnergy(),
                battle.getSecondEnergy()));
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

    public void saveAll(List<Battle> battles) throws IOException {
        saveAll(path, battles);
    }

    public void saveAll(Path path, List<?> battles) throws IOException {
        BufferedWriter writer = null;

        try {
            writer = Files.newBufferedWriter(path);

            for (Object line : battles) {
                if (line instanceof Battle)
                    writeBattleLine((Battle) line, writer);
                else
                    writer.write(line.toString() + System.lineSeparator());
            }
        } finally {
            if (writer != null) writer.close();
        }
    }

    public List<Battle> readAll() throws IOException {
        return readAll(path);
    }

    public List<Battle> readAll(String path) throws IOException {
        return readAll(Paths.get(path));
    }

    private List<Battle> readAll(Path path1) throws IOException {
        Stream<String> battleStream = null;
        try {
            battleStream = Files.lines(path1);

            return battleStream.map(new BattlesFunction())
                    .filter(new NotNullPredicate())
                    .collect(Collectors.toList());
        } finally {
            if (battleStream != null) battleStream.close();
        }
    }

    public void deleteFile(String file) throws IOException {
        Files.deleteIfExists(Paths.get(file));
    }

    public boolean existsFile(String file) {
        return Files.exists(Paths.get(file));
    }


}
