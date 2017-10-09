package alifec.core.persistence.filter;

import alifec.ParentTest;
import alifec.core.exception.ConfigFileException;
import alifec.core.exception.CreateContestFolderException;
import alifec.core.persistence.ContestConfig;
import alifec.core.persistence.filter.ContestFolderFilter;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;

/**
 * Created by Sergio Del Castillo on 07/08/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class ContestFolderFilterTest extends ParentTest {

    @Test
    public void testListEmpty() {
        String[] f = new File(TEST_ROOT_PATH).list(new ContestFolderFilter());

        Assert.assertArrayEquals(f, new String[0]);
    }

    @Test
    public void testAccept() {
        File folder = new File(TEST_ROOT_PATH);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("contest-");

        ContestFolderFilter contestFolderFilter = new ContestFolderFilter(false);
        Assert.assertFalse(contestFolderFilter.accept(folder, stringBuilder.toString()));
        Assert.assertFalse(contestFolderFilter.accept(folder, "contest-|"));
        Assert.assertFalse(contestFolderFilter.accept(folder, "contest-|"));
        Assert.assertFalse(contestFolderFilter.accept(folder, "contest-1|"));
        Assert.assertFalse(contestFolderFilter.accept(folder, "contest-|1"));
        Assert.assertFalse(contestFolderFilter.accept(folder, "contest-11111111111111111111111111"));

        for (int i = 0; i < 25; i++) {
            stringBuilder.append("c");
            Assert.assertTrue(contestFolderFilter.accept(folder, stringBuilder.toString()));
        }

        stringBuilder.append("c");
        Assert.assertFalse(contestFolderFilter.accept(folder, stringBuilder.toString()));

    }



    @Test
    public void testListOneContest() throws IOException, CreateContestFolderException, URISyntaxException, ConfigFileException {
        String contestName = ContestConfig.CONTEST_NAME_PREFIX + "01";
        createContest(contestName);

        String[] f = new File(TEST_ROOT_PATH).list(new ContestFolderFilter());

        Assert.assertTrue(1 == f.length);
        Assert.assertEquals(f[0], contestName);
    }

    @Test
    public void testListManyContests() throws IOException, CreateContestFolderException, URISyntaxException, ConfigFileException {
        //Create 100  contests
        for (int i = 0; i < 100; i++) {
            createContest(ContestConfig.CONTEST_NAME_PREFIX+ Integer.toString(i));
        }

        String[] f = new File(TEST_ROOT_PATH).list(new ContestFolderFilter());

        Assert.assertTrue(100 == f.length);

        String[] target = new String[100];
        for (int i = 99; i >= 0; i--) {
            target[i] = ContestConfig.CONTEST_NAME_PREFIX + Integer.toString(i);
        }

        //sort to compare
        Arrays.sort(f);
        Arrays.sort(target);

        Assert.assertArrayEquals(target, f);
    }

    @Test
    public void testListManyContestsWithSimilarName() throws IOException, CreateContestFolderException, URISyntaxException, ConfigFileException {
        String[] contestList = new String[]{
                "contest-01", //SI
                "Contest-02",//SI
                "cOntest-03",//SI
                "contesT-04",//SI
                "contesT_05",//NO
                "CONTEST-06",//SI
                "Contest07", //NO
                "Contes-08",//NO
                "Tcontest-09",//NO
                "contest-"//NO
        };
        String[] contestListTarget = new String[]{
                "contest-01", //SI
                "Contest-02",//SI
                "cOntest-03",//SI
                "contesT-04",//SI
                "CONTEST-06"//SI
        };

        for (String contestName : contestList) {
            createContest(contestName);
        }

        String[] list = new File(TEST_ROOT_PATH).list(new ContestFolderFilter());

        Assert.assertTrue(contestListTarget.length == list.length);

        //sort to compare
        Arrays.sort(list);
        Arrays.sort(contestListTarget);

        Assert.assertArrayEquals(contestListTarget, list);
    }

}
