package alifec;

import alifec.core.contest.Contest;
import alifec.core.contest.tournament.battles.BattleRun;
import alifec.core.exception.ConfigFileException;
import alifec.core.exception.CreateBattleException;
import alifec.core.persistence.ContestConfig;
import alifec.core.persistence.ContestHelper;
import alifec.core.exception.CreateContestFolderException;
import alifec.core.simulation.Colony;
import alifec.core.simulation.Environment;
import alifec.core.simulation.nutrient.Nutrient;
import org.junit.After;
import org.junit.Assert;
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
    public void init() {
        File rootDir = new File(TEST_ROOT_PATH);

        //ensure that the root dir exists
        if (!rootDir.exists()) {
            Assert.assertTrue(rootDir.mkdir());
        }
    }

    @After
    public void cleanup() throws IOException {
        File rootDir = new File(TEST_ROOT_PATH);

        if (rootDir.exists()) {
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

    protected ContestConfig  createContest(String name) throws IOException, CreateContestFolderException, URISyntaxException, ConfigFileException {
        ContestConfig config = ContestConfig.buildNewConfigFile(TEST_ROOT_PATH, name);

        createContest(config);

        File baseFolder = new File(config.getBaseFolder());

        if(!baseFolder.exists())
            Assert.assertTrue(baseFolder.mkdirs());

        config.save();
        return config;

    }

    protected void createContest(ContestConfig config) throws IOException, CreateContestFolderException, URISyntaxException {
        File cppResources = new File(Contest.class.getClass().getResource("/app/cpp/").toURI());
        File examplesResources = new File(Contest.class.getClass().getResource("/app/examples/").toURI());

        ContestHelper.buildNewContestFolder(config, true, cppResources, examplesResources);

    }

    protected BattleRun createBattle(Environment env, int colony1, int colony2, int nutrientId, String nutrientName) throws CreateBattleException {
        Colony c1 = env.getColonyById(colony1);
        Colony c2 = env.getColonyById(colony2);

        BattleRun battle = new BattleRun(c1.getId(), c2.getId(), nutrientId, c1.getName(), c2.getName(), nutrientName);
        env.createBattle(battle);

        return battle;
    }
}
