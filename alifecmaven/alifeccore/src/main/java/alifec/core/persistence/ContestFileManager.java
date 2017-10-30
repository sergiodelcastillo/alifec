package alifec.core.persistence;

import alifec.core.exception.CreateContestFolderException;
import alifec.core.persistence.config.ContestConfig;
import alifec.core.persistence.custom.FileNameFunction;
import alifec.core.persistence.filter.ContestFolderFilter;
import alifec.core.persistence.filter.TournamentFilter;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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

    public ContestFileManager(String file) {
        this.path = Paths.get(file);
    }

    public static List<String> listContest(String path) {
        try {
            List<String> list = Files.list(Paths.get(path))
                    .filter(new ContestFolderFilter())
                    .map(new FileNameFunction()).collect(Collectors.toList());

            while (list.contains(null))
                list.remove(null);

            return list;
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        return new ArrayList<>();
    }


    public List<String> listTournaments(String file) throws IOException {
       Path path = Paths.get(file);
        List<String> list = Files.list(path)
                .filter(new TournamentFilter())
                .map(new FileNameFunction()).collect(Collectors.toList());

        while (list.contains(null))
            list.remove(null);

        return list;
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
            Files.createDirectory(Paths.get(folder));
        } catch (IOException e) {
            logger.error("Creating folder: " + folder + " [FAIL]");
            throw new CreateContestFolderException("Can not create the folder: " + folder, e);
        }

        logger.info("Creating folder: " + folder + " [OK]");
    }

    public static void createExamples(Path source, String MOsFolder) {
        try {
            logger.info("Generating examples in folder " + MOsFolder);

            Files.walk(source).forEach(path -> {
                try {
                    Path target = Paths.get(MOsFolder + File.separator + path.getFileName());

                    if (Files.isRegularFile(path))
                        Files.copy(path, target);
                    logger.info("Generated file: " + target.toFile().getAbsolutePath());
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
                    Path target = Paths.get(targetFolder + File.separator + path.getFileName());

                    if (Files.isRegularFile(path)) {
                        Files.copy(path, target);
                        logger.info("Copying file: " + target.toFile().getAbsolutePath());
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
}
