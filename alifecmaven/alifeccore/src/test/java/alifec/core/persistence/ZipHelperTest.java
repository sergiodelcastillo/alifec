package alifec.core.persistence;

import alifec.ParentTest;
import alifec.core.exception.CreateContestFolderException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Sergio Del Castillo on 22/08/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class ZipHelperTest extends ParentTest {

    @Test
    public void testCreateZip() throws IOException, CreateContestFolderException, URISyntaxException {
        String contestName = "contest-01";

        ContestConfig config = createContest(contestName);

        String zipFile = ZipHelper.zipContest(config);

        Assert.assertEquals(1, new File(config.getBackupFolder()).list((dir, name) -> {
            return name.equals(zipFile);
        }).length);

    }

    @Test
    public void testVerifyTheZipContent() throws CreateContestFolderException, IOException, URISyntaxException {
        String[] target = new String[]{
                "contest-01/MOs/Movx.java",
                "contest-01/MOs/Tactica1.java",
                "contest-01/MOs/RandomColony.java",
                "contest-01/MOs/Tactica2.java",
                "contest-01/MOs/movx.h",
                "contest-01/cpp/CppColony.h",
                "contest-01/cpp/Data.h",
                "contest-01/cpp/Defs.h",
                "contest-01/cpp/Environment.cpp",
                "contest-01/cpp/Environment.h",
                "contest-01/cpp/Microorganism.h",
                "contest-01/cpp/Petri.h",
                "contest-01/cpp/includemos.h",
                "contest-01/cpp/lib_CppColony.cpp",
                "contest-01/cpp/lib_CppColony.h",
                "contest-01/nutrients"};
        String contestName = "contest-01";

        ContestConfig config = createContest(contestName);

        String zipfile = ZipHelper.zipContest(config);

        List<String> entries = ZipHelper.listEntries(zipfile);

        Assert.assertThat(entries, Matchers.containsInAnyOrder(target));
    }

    @Test
    public void testUnzip() throws CreateContestFolderException, IOException, URISyntaxException {
        String[] target = new String[]{
                "contest-01/MOs/Movx.java",
                "contest-01/MOs/Tactica1.java",
                "contest-01/MOs/RandomColony.java",
                "contest-01/MOs/Tactica2.java",
                "contest-01/MOs/movx.h",
                "contest-01/cpp/CppColony.h",
                "contest-01/cpp/Data.h",
                "contest-01/cpp/Defs.h",
                "contest-01/cpp/Environment.cpp",
                "contest-01/cpp/Environment.h",
                "contest-01/cpp/Microorganism.h",
                "contest-01/cpp/Petri.h",
                "contest-01/cpp/includemos.h",
                "contest-01/cpp/lib_CppColony.cpp",
                "contest-01/cpp/lib_CppColony.h",
                "contest-01/nutrients"};
        String contestName = "contest-01";

        ContestConfig config = createContest(contestName);

        String zipFile = ZipHelper.zipContest(config);

        String outputFolder = TEST_ROOT_PATH + File.separator + "restore";

        Assert.assertTrue(new File(outputFolder).mkdir());

        ZipHelper.unzip(config.getBackupFolder() + File.separator + zipFile, outputFolder);

        for (String file : target) {
            Assert.assertTrue(new File(outputFolder + File.separator + file).exists());
        }
    }
}
