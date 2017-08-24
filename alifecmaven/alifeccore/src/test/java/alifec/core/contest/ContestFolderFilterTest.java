package alifec.core.contest;

import alifec.ParentTest;
import alifec.core.exception.CreateContestFolderException;
import alifec.core.persistence.ContestConfig;
import alifec.core.persistence.filter.ContestFolderFilter;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
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
    public void testCheckPatter() {
        ContestFolderFilter filter = new ContestFolderFilter();

        //false contest names.
        Assert.assertFalse(filter.checkPattern(null));
        Assert.assertFalse(filter.checkPattern(""));
        Assert.assertFalse(filter.checkPattern("asdñfjlaskdfjalñksdfjlakjsdfñlk"));
        Assert.assertFalse(filter.checkPattern("contest-"));
        Assert.assertFalse(filter.checkPattern("contest-*"));
        Assert.assertFalse(filter.checkPattern("contest-a*"));
        Assert.assertFalse(filter.checkPattern("contest-b+"));
        Assert.assertFalse(filter.checkPattern("contest-+"));
        Assert.assertFalse(filter.checkPattern("1contest-0"));
        Assert.assertFalse(filter.checkPattern("ccontest-01"));
        Assert.assertFalse(filter.checkPattern("_contest-01"));
        Assert.assertFalse(filter.checkPattern("contest-1111122222111112222211111a"));

        Assert.assertTrue(filter.checkPattern("CONTEST-01"));
        Assert.assertTrue(filter.checkPattern("Contest-01"));
        Assert.assertTrue(filter.checkPattern("cOntEsT-01"));
        Assert.assertTrue(filter.checkPattern("contest-1"));
        Assert.assertTrue(filter.checkPattern("contest-1111122222111112222211111"));
        Assert.assertTrue(filter.checkPattern("contest-abc1234DEF"));
    }

    @Test
    public void testListOneContest() throws IOException, CreateContestFolderException {
        //TODO: usar format..
        String contestName = ContestConfig.CONTEST_NAME_PREFIX + "01";
        createContest(contestName);

        String[] f = new File(TEST_ROOT_PATH).list(new ContestFolderFilter());

        Assert.assertTrue(1 == f.length);
        Assert.assertEquals(f[0], contestName);
    }

    @Test
    public void testListManyContests() throws IOException, CreateContestFolderException {
        //Create 100  contests
        for (int i = 0; i < 100; i++) {
            //TODO: usar format..
            createContest(ContestConfig.CONTEST_NAME_PREFIX+ Integer.toString(i));
        }

        String[] f = new File(TEST_ROOT_PATH).list(new ContestFolderFilter());

        Assert.assertTrue(100 == f.length);

        String[] target = new String[100];
        for (int i = 99; i >= 0; i--) {
            //TODO: usar format..
            target[i] = ContestConfig.CONTEST_NAME_PREFIX + Integer.toString(i);
        }

        //sort to compare
        Arrays.sort(f);
        Arrays.sort(target);

        Assert.assertArrayEquals(target, f);
    }

    @Test
    public void testListManyContestsWithSimilarName() throws IOException, CreateContestFolderException {
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
