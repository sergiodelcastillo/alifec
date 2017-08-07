package alifec.core.contest;

import alifec.ParentTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by Sergio Del Castillo on 07/08/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class ContestFilterTest extends ParentTest {

    @Test
    public void testListEmpty() {
        String[] f = new File(ROOT_PATH).list(new ContestFilter());

        Assert.assertArrayEquals(f, new String[0]);
    }

    @Test
    public void testListOneContest() throws IOException {
        String contestName = ContestFilter.CONTEST_PREFIX + "01";
        createContest(contestName);

        String[] f = new File(ROOT_PATH).list(new ContestFilter());

        Assert.assertTrue(1 == f.length);
        Assert.assertEquals(f[0], contestName);

        deleteContest(contestName);
    }

    @Test
    public void testListManyContests() throws IOException {
        //Create 100  contests
        for (int i = 0; i < 100; i++) {
            createContest(ContestFilter.CONTEST_PREFIX + Integer.toString(i));
        }

        String[] f = new File(ROOT_PATH).list(new ContestFilter());

        Assert.assertTrue(100 == f.length);

        String[] target = new String[100];
        for (int i = 99; i >= 0; i--) {
            target[i] = ContestFilter.CONTEST_PREFIX + Integer.toString(i);
        }

        //sort to compare
        Arrays.sort(f);
        Arrays.sort(target);

        Assert.assertArrayEquals(target, f);

        //cleanup the test files
        for (int i = 0; i < 100; i++) {
            deleteContest(ContestFilter.CONTEST_PREFIX + Integer.toString(i));
        }
    }

    @Test
    public void testListManyContestsWithSimilarName() throws IOException {
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

        String[] list = new File(ROOT_PATH).list(new ContestFilter());

        Assert.assertTrue(contestListTarget.length == list.length);

                //sort to compare
        Arrays.sort(list);
        Arrays.sort(contestListTarget);

        Assert.assertArrayEquals(contestListTarget, list);

        for (String contestName : contestList) {
            deleteContest(contestName);
        }
    }

    private void deleteContest(String contestName) {
        File file = new File(ROOT_PATH + File.separator + contestName);

        Assert.assertTrue(file.delete());
    }


    private void createContest(String name) throws IOException {
        File file = new File(ROOT_PATH + File.separator + name);

        Assert.assertTrue(file.mkdir());
    }
}
