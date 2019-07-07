package alifec.core.persistence;

import alifec.core.exception.ConfigFileException;
import alifec.core.exception.CreateContestFolderException;
import alifec.core.persistence.config.ContestConfig;
import alifec.core.persistence.custom.ContestFolderPredicate;
import alifec.core.persistence.custom.FileNameFunction;
import alifec.core.persistence.custom.NotNullPredicate;
import alifec.core.persistence.custom.TournamentPredicate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by Sergio Del Castillo on 06/08/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class ContestFileManager {
    private static Logger logger = LogManager.getLogger(ContestFileManager.class);
    private final Path path;

    public ContestFileManager(String folder) {
        this.path = Paths.get(folder);
    }

    public static List<String> listContest() throws IOException, ConfigFileException {
        try (Stream<Path> list = Files.list(Paths.get(ContestConfig.getDefaultBaseDataFolder()))) {
            return list.filter(new ContestFolderPredicate())
                    .map(new FileNameFunction())
                    .filter(new NotNullPredicate())
                    .collect(Collectors.toList());
        }
    }

    public static void buildNewContest(ContestConfig config, boolean createExamples) throws CreateContestFolderException {
        String cppResources = "compiler/cpp/";
        String exampleResources = "examples/";

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

    public static void createExamples(String source, String MOsFolder) {
        if (JarFileManager.isLoadedFromJar())
            JarFileManager.createFolderFromJar(source, MOsFolder);
        else
            createExamplesFromFile(source, MOsFolder);
    }

    public static void createExamplesFromJar(String path, String MOsFolder) {
        System.out.println("todo: implementar");
        //
        try {
            CodeSource src = ContestFileManager.class.getProtectionDomain().getCodeSource();
            if (src != null) {
                URL jar = src.getLocation();
                ZipInputStream zip = new ZipInputStream(jar.openStream());
                ZipEntry e;

                while ((e = zip.getNextEntry()) != null) {
                    String name = e.getName();

                    if (name.startsWith(path)) {
                        System.out.println("to add: " + name);

                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createExamplesFromFile(String path, String MOsFolder) {
        logger.info("Generating examples in folder " + MOsFolder);
        Path source = getRootPathSourcePath().resolve(path);

        try (Stream<Path> list = Files.walk(source)) {
            list.forEach(p -> {
                try {
                    Path target = Paths.get(MOsFolder + File.separator + p.getFileName()).toAbsolutePath().normalize();

                    if (Files.isRegularFile(p))
                        Files.copy(p, target);
                    logger.info("Generated file: " + target.toString());
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            });
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    private static boolean createCppApi(String source, String targetFolder) {
        if (JarFileManager.isLoadedFromJar())
            return JarFileManager.createFolderFromJar(source, targetFolder);
        else
            return createCppApiFromFile(source, targetFolder);
    }


    private static Path getRootPathSourcePath() {
        String path = ContestFileManager.class.getResource("").toString().replace("file:", "");
        String packageString = ContestFileManager.class.getPackageName() + ".";
        String packagePath = packageString.replace(".", File.separator);

        return Paths.get(path.replace(packagePath, ""));
    }

    private static boolean createCppApiFromFile(String path, String targetFolder) {
        final boolean[] isOK = {true};

        Path source = getRootPathSourcePath().resolve(path);

        try (Stream<Path> list = Files.walk(source)) {
            list.forEach(p -> {
                try {
                    Path target = Paths.get(targetFolder + File.separator + p.getFileName()).toAbsolutePath().normalize();

                    if (Files.isRegularFile(p)) {
                        Files.copy(p, target);
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


    public List<String> listTournaments(String file) throws IOException {
        try (Stream<Path> list = Files.list(Paths.get(file))) {
            return list.filter(new TournamentPredicate())
                    .map(new FileNameFunction())
                    .filter(new NotNullPredicate())
                    .collect(Collectors.toList());
        }
    }

    public void delete(String file) throws IOException {
        Path path = Paths.get(file);

        //remove the file and its folder.
        Files.deleteIfExists(path);
        Files.deleteIfExists(path.getParent());
    }
}
