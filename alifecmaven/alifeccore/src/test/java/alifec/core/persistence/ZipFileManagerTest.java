package alifec.core.persistence;

import alifec.core.contest.ParentTest;
import alifec.core.exception.ConfigFileException;
import alifec.core.exception.CreateContestFolderException;
import alifec.core.persistence.config.ContestConfig;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by Sergio Del Castillo on 22/08/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class ZipFileManagerTest extends ParentTest {

    @Test
    public void testCreateZip() throws IOException, CreateContestFolderException, URISyntaxException, ConfigFileException {
        String contestName = "contest-01";

        ContestConfig config = createContest(contestName);
        ZipFileManager zipFileManager = new ZipFileManager(config);
        String zipFile = zipFileManager.zipContest();

        Assert.assertEquals(1, new File(config.getBackupFolder()).list((dir, name) -> {
            return name.equals(zipFile);
        }).length);

    }

    @Test
    public void testVerifyTheZipContent() throws CreateContestFolderException, IOException, URISyntaxException, ConfigFileException {
        String[] target = new String[]{
                "contest-01/MOs/Movx.java",
                "contest-01/MOs/Tactica1.java",
                "contest-01/MOs/RandomColony.java",
                "contest-01/MOs/Tactica2.java",
                "contest-01/MOs/advancedMO.h",
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
                "contest-01/cpp/lib_CppColony.h"
        };
        String contestName = "contest-01";

        ContestConfig config = createContest(contestName);
        ZipFileManager zipFileManager = new ZipFileManager(config);
        String zipfile = zipFileManager.zipContest();

        List<String> entries = zipFileManager.listEntries(config.getBackupFolder()+ File.separator+zipfile);

        Assert.assertThat(entries, Matchers.containsInAnyOrder(target));
    }

    @Test
    public void testUnzip() throws CreateContestFolderException, IOException, URISyntaxException, ConfigFileException {
        String[] target = new String[]{
                "contest-01/MOs/Movx.java",
                "contest-01/MOs/Tactica1.java",
                "contest-01/MOs/RandomColony.java",
                "contest-01/MOs/Tactica2.java",
                "contest-01/MOs/advancedMO.h",
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
                "contest-01/cpp/lib_CppColony.h"
        };
        String contestName = "contest-01";

        ContestConfig config = createContest(contestName);
        ZipFileManager zipFileManager = new ZipFileManager(config);
        String zipFile = zipFileManager.zipContest();

        String outputFolder = TEST_ROOT_PATH + File.separator + "restore";

        Assert.assertTrue(new File(outputFolder).mkdir());

        zipFileManager.unzip(config.getBackupFolder() + File.separator + zipFile, outputFolder);

        for (String file : target) {
            Assert.assertTrue(new File(outputFolder + File.separator + file).exists());
        }
    }
}
