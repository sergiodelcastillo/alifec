package alifec;

import alifec.core.persistence.ContestConfig;
import alifec.core.persistence.ContestHelper;
import alifec.core.exception.CreateContestFolderException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import java.io.File;
import java.io.IOException;
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

    protected ContestConfig  createContest(String name) throws IOException, CreateContestFolderException {
        ContestConfig config = ContestConfig.buildNewConfigFile(TEST_ROOT_PATH, name);

        createContest(config);
        return config;

    }

    protected void createContest(ContestConfig config) throws IOException, CreateContestFolderException {
        ContestHelper.buildNewContestFolder(config, true);

    }
}
