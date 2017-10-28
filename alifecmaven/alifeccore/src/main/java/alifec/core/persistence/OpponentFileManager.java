package alifec.core.persistence;

import alifec.core.contest.Battle;
import alifec.core.contest.oponentInfo.OpponentInfo;
import alifec.core.persistence.custom.OpponentFunction;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Sergio Del Castillo on 28/10/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class OpponentFileManager {
    private static Logger logger = org.apache.logging.log4j.LogManager.getLogger(OpponentFileManager.class);

    private final Path path;

    public OpponentFileManager(String file, boolean createFile) throws IOException {
        this.path = Paths.get(file);

        if (createFile) {
            if (Files.notExists(path)) {
                Files.createFile(path);
            }
        }
    }


    public List<OpponentInfo> readAll() throws IOException {
        return Files.lines(path).map(new OpponentFunction()).collect(Collectors.toList());
    }

    public void saveAll(List<OpponentInfo> opponents) {
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            for (Object line : opponents) {
                writer.write(line.toString() + '\n');
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            //todo: lanzar una exception
        }
    }
}
