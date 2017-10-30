package alifec.core.persistence;

import alifec.core.contest.Battle;
import alifec.core.persistence.custom.BattlesFunction;
import alifec.core.persistence.custom.ExcludeBattlesPredicate;
import org.apache.logging.log4j.LogManager;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
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
        List<Battle> list = Files.lines(path).map(new BattlesFunction()).collect(Collectors.toList());

        //remove empty values. it means invalid lines in the file
        while (list.contains(null))
            list.remove(null);

        return list;
    }

    public List<Battle> readAll(String path) throws IOException {
        List<Battle> list = Files.lines(Paths.get(path)).map(new BattlesFunction()).collect(Collectors.toList());

        //remove empty values. it means invalid lines in the file
        while (list.contains(null))
            list.remove(null);

        return list;
    }

    public void deleteFile(String file) throws IOException {
        Files.deleteIfExists(Paths.get(file));
    }

    public boolean existsFile(String file) {
        return Files.exists(Paths.get(file));
    }


}
