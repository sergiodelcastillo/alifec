package alifec;

import alifec.core.contest.BattleRun;
import alifec.core.exception.ConfigFileException;
import alifec.core.exception.CreateBattleException;
import alifec.core.exception.CreateContestFolderException;
import alifec.core.persistence.ContestConfig;
import alifec.core.persistence.ContestHelper;
import alifec.core.simulation.Colony;
import alifec.core.simulation.Environment;
import org.junit.After;
import org.junit.Before;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

/**
 * Created by Sergio Del Castillo on 07/08/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class ParentTest {

    public static String ROOT_PATH = "./target";

    public static String TEST_ROOT_PATH = ROOT_PATH + File.separator + "alifectests";

    @Before
    public void init() throws IOException {
        Path rootDir = Paths.get(TEST_ROOT_PATH);

        //ensure that the root dir exists
        if (Files.notExists(rootDir)) {
            Files.createDirectory(rootDir);
        }
    }

    @After
    public void cleanup() throws IOException {
        Path rootDir = Paths.get(TEST_ROOT_PATH);

        if (Files.exists(rootDir)) {
            Path dirPath = Paths.get(TEST_ROOT_PATH);

            Files.walk(dirPath, FileVisitOption.FOLLOW_LINKS)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    /*.peek(System.out::println)*/
                    .forEach(File::delete);


            //Files.walk - return all files/directories below rootPath including
            //.sorted - sort the list in reverse order, so the directory itself comes
            //          after the including subdirectories and files
            //.map - map the Path to File
            //.peek - is there only to show which entry is processed
            //.forEach - calls the .delete() method on every File object
        }
    }

    protected ContestConfig createContest(String name) throws IOException, CreateContestFolderException, URISyntaxException, ConfigFileException {
        ContestConfig config = ContestConfig.buildNewConfigFile(TEST_ROOT_PATH, name);

        createContest(config);

        Path basePath = Paths.get(config.getBaseAppFolder());

        if (Files.notExists(basePath))
            Files.createDirectory(basePath);

        config.save();
        return config;
    }

    protected void createContest(ContestConfig config) throws IOException, CreateContestFolderException, URISyntaxException {
        Path cppResources = Paths.get(ParentTest.class.getClass().getResource("/app/cpp/").toURI());
        Path examplesResources = Paths.get(ParentTest.class.getClass().getResource("/app/examples/").toURI());

        ContestHelper.buildNewContestFolder(config, true, cppResources, examplesResources);

        //the app folder can exists if this method was already called.
        // The app folder is not contest folder dependent so it have to be created only the first time.
        //create the app folder
        Path app = Paths.get(config.getBaseAppFolder());
        if (Files.notExists(app))
            Files.createDirectory(app);

        //create the file compiler.properties
        Path compilerProperties = Paths.get(config.getCompilerConfigFile());
        if (Files.notExists(compilerProperties)) {
            Path compilerConfigFile = Paths.get(ParentTest.class.getClass().getResource("/app/compiler.properties").toURI());
            Files.copy(compilerConfigFile, compilerProperties);
        }
    }

    protected BattleRun createBattle(Environment env, int colony1, int colony2, int nutrientId, String nutrientName) throws CreateBattleException {
        Colony c1 = env.getColonyById(colony1);
        Colony c2 = env.getColonyById(colony2);

        BattleRun battle = new BattleRun(c1.getId(), c2.getId(), nutrientId, c1.getName(), c2.getName(), nutrientName);
        env.createBattle(battle);

        return battle;
    }
}
