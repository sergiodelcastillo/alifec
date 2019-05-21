package alifec.core.persistence;

import alifec.core.contest.ParentTest;
import alifec.core.exception.ConfigFileException;
import alifec.core.exception.CreateContestFolderException;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Calendar;

/**
 * Created by Sergio Del Castillo on 15/06/18.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class ContestFileManagerTest extends ParentTest {

    @Test
    public void testGetNextAvailableName() throws URISyntaxException, ConfigFileException, CreateContestFolderException, IOException {
        String contestName = "Contest-" + String.valueOf(Calendar.getInstance().get(Calendar.YEAR));

        Assert.assertEquals(contestName, ContestFileManager.getNextAvailableName(TEST_ROOT_PATH));

        createContest(contestName);
        Assert.assertEquals(contestName + "-1", ContestFileManager.getNextAvailableName(TEST_ROOT_PATH));

        for (int i = 0; i < 100; i++) {
            createContest(contestName + "-" + i);
            Assert.assertEquals(contestName + "-" + (i + 1), ContestFileManager.getNextAvailableName(TEST_ROOT_PATH));
        }

        createContest(contestName + "-100");

        String name = ContestFileManager.getNextAvailableName(TEST_ROOT_PATH);

        Calendar calendar = Calendar.getInstance();
        Integer year = calendar.get(Calendar.YEAR);
        Integer month = Calendar.getInstance().get(Calendar.MONTH);
        Integer day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        String shouldBe = String.format("Contest-%d-%d-%d", year, month, day);

        Assert.assertTrue(name.startsWith(shouldBe));
    }
}
