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
    static Logger logger = org.apache.logging.log4j.LogManager.getLogger(OpponentFileManager.class);


    public List<OpponentInfo> readAll(String file) throws IOException {
        Path path = Paths.get(file);

        return Files.lines(path).map(new OpponentFunction()).collect(Collectors.toList());
    }

    public void saveAll(String competitorsFile, List<OpponentInfo> opponents) {
        Path path = Paths.get(competitorsFile);

        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            for (Object line : opponents) {
                    writer.write(line.toString() + '\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
