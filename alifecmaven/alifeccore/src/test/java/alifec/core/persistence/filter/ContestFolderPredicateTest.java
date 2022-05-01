package alifec.core.persistence.filter;

import alifec.core.contest.ParentTest;
import alifec.core.exception.ConfigFileException;
import alifec.core.exception.CreateContestFolderException;
import alifec.core.persistence.config.ContestConfig;
import alifec.core.persistence.custom.ContestFolderPredicate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by Sergio Del Castillo on 07/08/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class ContestFolderPredicateTest extends ParentTest {

    @Test
    public void testListEmpty() throws IOException {
        try (Stream<Path> list = Files.list(Paths.get(TEST_ROOT_PATH))) {
            long count = list.filter(new ContestFolderPredicate()).count();
            Assertions.assertEquals(count, 0);
        }
    }

    @Test
    public void testAccept() {
        Path folder = Paths.get(TEST_ROOT_PATH);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("contest-");

        ContestFolderPredicate contestFolderPredicate = new ContestFolderPredicate(false);
        Assertions.assertFalse(contestFolderPredicate.test(folder.resolve(stringBuilder.toString())));
        Assertions.assertFalse(contestFolderPredicate.test(folder.resolve("contest-q a")));
        Assertions.assertFalse(contestFolderPredicate.test(folder.resolve("contest- baa")));
        Assertions.assertFalse(contestFolderPredicate.test(folder.resolve("contest-dd__--")));
        Assertions.assertFalse(contestFolderPredicate.test(folder.resolve("contest-ll!")));
        Assertions.assertFalse(contestFolderPredicate.test(folder.resolve("contest-11111111111111111111111111")));

        for (int i = 0; i < 25; i++) {
            stringBuilder.append("c");
            Assertions.assertTrue(contestFolderPredicate.test(folder.resolve(stringBuilder.toString())));
        }

        stringBuilder.append("c");
        Assertions.assertFalse(contestFolderPredicate.test(folder.resolve(stringBuilder.toString())));
    }

    @Test
    public void testListOneContest() throws IOException, CreateContestFolderException, URISyntaxException, ConfigFileException {
        String contestName = ContestConfig.CONTEST_NAME_PREFIX + "01";
        createContest(contestName);

        try (Stream<Path> list = Files.list(Paths.get(TEST_ROOT_PATH))) {
            boolean allmatch = list.filter(new ContestFolderPredicate())
                    .allMatch(path -> path.getFileName().toString().equals(contestName));

            Assertions.assertTrue(allmatch);
        }
    }

    @Test
    public void testListManyContests() throws IOException, CreateContestFolderException, URISyntaxException, ConfigFileException {
        List<String> target = new ArrayList<>(100);
        for (int i = 99; i >= 0; i--) {
            target.add(ContestConfig.CONTEST_NAME_PREFIX + i);
        }

        //Create 100  contests
        for (int i = 0; i < 100; i++) {
            createContest(ContestConfig.CONTEST_NAME_PREFIX + i);
        }

        try (Stream<Path> list = Files.list(Paths.get(ContestConfig.getBaseDataFolder(TEST_ROOT_PATH)))) {
            boolean allMatch = list.filter(new ContestFolderPredicate()).allMatch(path -> {
                String file = path.getFileName().toString();
                if (target.contains(file)) {
                    target.remove(file);
                    return true;
                }

                return false;
            });
            Assertions.assertTrue(allMatch);
        }

        Assertions.assertTrue(target.isEmpty());
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

        try (Stream<Path> list = Files.list(Paths.get(ContestConfig.getBaseDataFolder(TEST_ROOT_PATH)))) {
            boolean allMatch = list.filter(new ContestFolderPredicate())
                    .allMatch(path -> {
                        String file = path.getFileName().toString();
                        if (contestListTarget.contains(file)) {
                            contestListTarget.remove(file);
                            return true;
                        }

                        return false;
                    });
            Assertions.assertTrue(allMatch);
        }
        Assertions.assertTrue(contestListTarget.isEmpty());
    }

}
