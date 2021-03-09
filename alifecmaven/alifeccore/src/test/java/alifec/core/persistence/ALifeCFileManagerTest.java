package alifec.core.persistence;

import alifec.core.contest.ParentTest;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Sergio Del Castillo on 06/07/19.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */

public class ALifeCFileManagerTest extends ParentTest {

    @Test
    public void testCreateFiles() throws IOException, URISyntaxException {
        //Files do not exists
        Assert.assertFalse(Files.exists(Paths.get(TEST_ROOT_PATH + File.separator + "app")));
        Assert.assertFalse(Files.exists(Paths.get(TEST_ROOT_PATH + File.separator + "data")));

        //create files
        Path basePath = Paths.get(TEST_ROOT_PATH);

        ALifeCFileManager.build(basePath);

        //Files should exists
        Assert.assertTrue(Files.exists(Paths.get(TEST_ROOT_PATH + File.separator + "app")));
        Assert.assertTrue(Files.exists(Paths.get(TEST_ROOT_PATH + File.separator + "app" + File.separator + "compiler.properties")));
        Assert.assertTrue(Files.exists(Paths.get(TEST_ROOT_PATH + File.separator + "app" + File.separator + "log4j2.xml")));
        Assert.assertTrue(Files.exists(Paths.get(TEST_ROOT_PATH + File.separator + "data")));

        ALifeCFileManager.build(basePath);
        //should be the same
        Assert.assertTrue(Files.exists(Paths.get(TEST_ROOT_PATH + File.separator + "app")));
        Assert.assertTrue(Files.exists(Paths.get(TEST_ROOT_PATH + File.separator + "app" + File.separator + "compiler.properties")));
        Assert.assertTrue(Files.exists(Paths.get(TEST_ROOT_PATH + File.separator + "app" + File.separator + "log4j2.xml")));
        Assert.assertTrue(Files.exists(Paths.get(TEST_ROOT_PATH + File.separator + "data")));

        Files.delete(Paths.get(TEST_ROOT_PATH + File.separator + "app" + File.separator + "compiler.properties"));

        //only one file is missing
        Assert.assertTrue(Files.exists(Paths.get(TEST_ROOT_PATH + File.separator + "app")));
        Assert.assertFalse(Files.exists(Paths.get(TEST_ROOT_PATH + File.separator + "app" + File.separator + "compiler.properties")));
        Assert.assertTrue(Files.exists(Paths.get(TEST_ROOT_PATH + File.separator + "app" + File.separator + "log4j2.xml")));
        Assert.assertTrue(Files.exists(Paths.get(TEST_ROOT_PATH + File.separator + "data")));

        ALifeCFileManager.build(basePath);

        //everything should exists again
        //should be the same
        Assert.assertTrue(Files.exists(Paths.get(TEST_ROOT_PATH + File.separator + "app")));
        Assert.assertTrue(Files.exists(Paths.get(TEST_ROOT_PATH + File.separator + "app" + File.separator + "compiler.properties")));
        Assert.assertTrue(Files.exists(Paths.get(TEST_ROOT_PATH + File.separator + "app" + File.separator + "log4j2.xml")));
        Assert.assertTrue(Files.exists(Paths.get(TEST_ROOT_PATH + File.separator + "data")));
    }
}
