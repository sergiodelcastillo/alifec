package alifec.core.persistence;

import alifec.ParentTest;
import alifec.core.contest.BattleResult;
import alifec.core.exception.*;
import alifec.core.persistence.config.ContestConfig;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by Sergio Del Castillo on 23/10/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class TournamentFileManagerTest extends ParentTest {

    @Test
    public void testAppend() throws URISyntaxException, ConfigFileException, CreateContestFolderException, IOException, CreateContestException, CreateTournamentException, CreateBattleException {
        ContestConfig config = createContest("Contest-01");
        //ensure the competition mode

        List<BattleResult> list = battleResultDataSet();

        TournamentFileManager manager = new TournamentFileManager();
        String path = TEST_ROOT_PATH + File.separator + "battles.csv";

        for (BattleResult b : list)
            manager.append(path, b.toBattle());

        //todo: load the file and check.
        //todo: add missing tests (delete, save, etc).
    }
}
