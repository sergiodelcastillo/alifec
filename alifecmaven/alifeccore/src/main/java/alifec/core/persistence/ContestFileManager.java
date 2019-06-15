package alifec.core.persistence;

import alifec.core.contest.Contest;
import alifec.core.exception.ConfigFileException;
import alifec.core.exception.CreateContestFolderException;
import alifec.core.persistence.config.ContestConfig;
import alifec.core.persistence.custom.FileNameFunction;
import alifec.core.persistence.custom.NotNullPredicate;
import alifec.core.persistence.custom.ContestFolderPredicate;
import alifec.core.persistence.custom.TournamentPredicate;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Month;
import java.time.Year;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Sergio Del Castillo on 06/08/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class ContestFileManager {
    private static Logger logger = org.apache.logging.log4j.LogManager.getLogger(ContestFileManager.class);
    private final Path path;

    public ContestFileManager(String folder) {
        this.path = Paths.get(folder);
    }

    public static List<String> listContest() {
        try {
            return Files.list(Paths.get(ContestConfig.getDefaultBaseDataFolder()))
                    .filter(new ContestFolderPredicate())
                    .map(new FileNameFunction())
                    .filter(new NotNullPredicate())
                    .collect(Collectors.toList());
        } catch (ConfigFileException | IOException e) {
            logger.info("Could not retrieve the contest list: {}", e.getMessage());
        }

        return new ArrayList<>();
    }


    public List<String> listTournaments(String file) throws IOException {
        Path path = Paths.get(file);

        return Files.list(path)
                .filter(new TournamentPredicate())
                .map(new FileNameFunction())
                .filter(new NotNullPredicate())
                .collect(Collectors.toList());
    }

    public static void buildNewContestFolder(ContestConfig config, boolean createExamples) throws CreateContestFolderException {
        Path cppResources = Paths.get("app/cpp/");
        Path examplesResources = Paths.get("app/examples/");
        buildNewContestFolder(config, createExamples, cppResources, examplesResources);
    }

    public static void buildNewContestFolder(ContestConfig config,
                                             boolean createExamples,
                                             Path cppResources,
                                             Path exampleResources) throws CreateContestFolderException {
        createFolder(config.getContestPath());
        createFolder(config.getMOsPath());
        createFolder(config.getReportPath());
        createFolder(config.getCppApiFolder());
        createFolder(config.getLogFolder());
        createFolder(config.getBackupFolder());

        //copy the cpp api
        if (!createCppApi(cppResources, config.getCppApiFolder())) {
            logger.error("Creating cpp api to: " + config.getCppApiFolder() + " [FAIL]");
            throw new CreateContestFolderException("Cant not create the cpp api files in dir: " + config.getCppApiFolder());
        }
        logger.info("Creating cpp api to: " + config.getCppApiFolder() + " [OK]");

        //Create examples
        logger.info("Create examples: " + (createExamples ? "YES" : "NO"));
        if (createExamples) {
            createExamples(exampleResources, config.getMOsPath());
        }
    }

    private static void createFolder(String folder) throws CreateContestFolderException {
        try {
            Files.createDirectories(Paths.get(folder).toAbsolutePath().normalize());
        } catch (IOException e) {
            logger.error("Creating folder: " + Paths.get(folder).toAbsolutePath().normalize() + " [FAIL]");
            throw new CreateContestFolderException("Can not create the folder: " + Paths.get(folder).toAbsolutePath().normalize(), e);
        }

        logger.info("Creating folder: " + folder + " [OK]");
    }

    public static void createExamples(Path source, String MOsFolder) {
        try {
            logger.info("Generating examples in folder " + MOsFolder);

            Files.walk(source).forEach(path -> {
                try {
                    Path target = Paths.get(MOsFolder + File.separator + path.getFileName()).toAbsolutePath().normalize();

                    if (Files.isRegularFile(path))
                        Files.copy(path, target);
                    logger.info("Generated file: " + target.toString());
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            });
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    private static boolean createCppApi(Path source, String targetFolder) {
        try {
            final boolean[] isOK = {true};

            Files.walk(source).forEach(path -> {
                try {
                    Path target = Paths.get(targetFolder + File.separator + path.getFileName()).toAbsolutePath().normalize();

                    if (Files.isRegularFile(path)) {
                        Files.copy(path, target);
                        logger.info("Copying file: " + target.toAbsolutePath());
                    }
                } catch (IOException e) {
                    isOK[0] = false;
                    logger.error(e.getMessage(), e);

                }
            });
            return isOK[0];
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
            return false;
        }
    }

    public void delete(String file) throws IOException {
        Path path = Paths.get(file);

        //remove the file and its folder.
        Files.deleteIfExists(path);
        Files.deleteIfExists(path.getParent());
    }

    public static String getNextAvailableName(String p) {
        String dataFolder = ContestConfig.getBaseDataFolder(p);
        Calendar calendar = Calendar.getInstance();
        Integer year = calendar.get(Calendar.YEAR);
        Integer count = 0;
        String contestName = String.format(ContestConfig.CONTEST_NAME_PREFIX + "%d", year);
        String nextFolder = String.format("%s%s%s", dataFolder, File.separator, contestName);

        while (count <= 100 && Files.exists(Paths.get(nextFolder))) {
            contestName = String.format(ContestConfig.CONTEST_NAME_PREFIX + "%d-%d", year, ++count);
            nextFolder = String.format("%s%s%s", dataFolder, File.separator, contestName);
        }

        if (count > 100) {
            Integer month = calendar.get(Calendar.MONTH);
            Integer day = calendar.get(Calendar.DAY_OF_MONTH);
            Long time = calendar.getTimeInMillis();

            contestName = String.format(ContestConfig.CONTEST_NAME_PREFIX + "%d-%d-%d-%d", year, month, day, time);
        }

        return contestName;
    }

    public static String getNextAvailableNameWithoutPrefix(String path) {
        return getNextAvailableName(path).replace(ContestConfig.CONTEST_NAME_PREFIX, "");
    }
}
