package alifec.core.persistence.filter;

import alifec.ParentTest;
import alifec.core.exception.ConfigFileException;
import alifec.core.exception.CreateContestFolderException;
import alifec.core.persistence.ContestConfig;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sergio Del Castillo on 07/08/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class ContestFolderFilterTest extends ParentTest {

    @Test
    public void testListEmpty() throws IOException {

        Assert.assertEquals(Files.list(Paths.get(TEST_ROOT_PATH))
                .filter(new ContestFolderFilter()).count(), 0);
    }

    @Test
    public void testAccept() {
        Path folder = Paths.get(TEST_ROOT_PATH);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("contest-");

        ContestFolderFilter contestFolderFilter = new ContestFolderFilter(false);
        Assert.assertFalse(contestFolderFilter.test(folder.resolve(stringBuilder.toString())));
        Assert.assertFalse(contestFolderFilter.test(folder.resolve("contest-|")));
        Assert.assertFalse(contestFolderFilter.test(folder.resolve("contest-|")));
        Assert.assertFalse(contestFolderFilter.test(folder.resolve("contest-1|")));
        Assert.assertFalse(contestFolderFilter.test(folder.resolve("contest-|1")));
        Assert.assertFalse(contestFolderFilter.test(folder.resolve("contest-11111111111111111111111111")));

        for (int i = 0; i < 25; i++) {
            stringBuilder.append("c");
            Assert.assertTrue(contestFolderFilter.test(folder.resolve(stringBuilder.toString())));
        }

        stringBuilder.append("c");
        Assert.assertFalse(contestFolderFilter.test(folder.resolve(stringBuilder.toString())));
    }

    @Test
    public void testListOneContest() throws IOException, CreateContestFolderException, URISyntaxException, ConfigFileException {
        String contestName = ContestConfig.CONTEST_NAME_PREFIX + "01";
        createContest(contestName);


        Assert.assertTrue(Files.list(Paths.get(TEST_ROOT_PATH))
                .filter(new ContestFolderFilter())
                .allMatch(path -> path.getFileName().toString().equals(contestName)));
    }

    @Test
    public void testListManyContests() throws IOException, CreateContestFolderException, URISyntaxException, ConfigFileException {
        List<String> target = new ArrayList<>(100);
        for (int i = 99; i >= 0; i--) {
            target.add(ContestConfig.CONTEST_NAME_PREFIX + Integer.toString(i));
        }

        //Create 100  contests
        for (int i = 0; i < 100; i++) {
            createContest(ContestConfig.CONTEST_NAME_PREFIX + Integer.toString(i));
        }


        Assert.assertTrue(Files.list(Paths.get(TEST_ROOT_PATH)).filter(new ContestFolderFilter()).allMatch(path -> {
            String file = path.getFileName().toString();
            if (target.contains(file)) {
                target.remove(file);
                return true;
            }

            return false;
        }));

        Assert.assertTrue(target.isEmpty());
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

        List<String> contestListTarget = new ArrayList<>();
        contestListTarget.add("contest-01");
        contestListTarget.add("Contest-02");
        contestListTarget.add("cOntest-03");
        contestListTarget.add("contesT-04");
        contestListTarget.add("CONTEST-06");


        for (String contestName : contestList) {
            createContest(contestName);
        }


        Assert.assertTrue(Files.list(Paths.get(TEST_ROOT_PATH))
                .filter(new ContestFolderFilter())
                .allMatch(path -> {
                    String file = path.getFileName().toString();
                    if (contestListTarget.contains(file)) {
                        contestListTarget.remove(file);
                        return true;
                    }

                    return false;
                }));
        Assert.assertTrue(contestListTarget.isEmpty());
    }

}
