package alifec.core.persistence;

import alifec.core.contest.Battle;
import alifec.core.contest.ParentTest;
import alifec.core.exception.BattleException;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Sergio Del Castillo on 23/10/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class TournamentFileManagerTest extends ParentTest {

    @Test
    public void testAppend() throws BattleException, IOException {
        List<Battle> list = battleDataSet();

        String path = TEST_ROOT_PATH + File.separator + "battles.csv";
        TournamentFileManager manager = new TournamentFileManager(path, true);

        for (Battle b : list)
            manager.append(b);

        List<Battle> result = manager.readAll();

        Collections.sort(list);
        Collections.sort(result);

        Assert.assertEquals(list.size(), result.size());

        for (int i = 0; i < list.size(); i++) {
            Assert.assertEquals(list.get(i), result.get(i));
        }
    }

    @Test
    public void testSave() throws BattleException, IOException {
        List<Battle> list = battleDataSet();

        String path = TEST_ROOT_PATH + File.separator + "battles.csv";
        TournamentFileManager manager = new TournamentFileManager(path, true);

        manager.saveAll(path, list);

        List<Battle> result = manager.readAll(path);

        Collections.sort(list);
        Collections.sort(result);

        Assert.assertEquals(list.size(), result.size());

        for (int i = 0; i < list.size(); i++) {
            Assert.assertEquals(list.get(i), result.get(i));
        }
    }

    @Test
    public void testDelete() throws BattleException, IOException {
        List<Battle> list = battleDataSet();

        String path = TEST_ROOT_PATH + File.separator + "battles.csv";
        TournamentFileManager manager = new TournamentFileManager(path, true);

        //deleteFromBattlesFile all
        manager.saveAll(path, list);

        List<Battle> result = manager.readAll(path);
        manager.deleteFromBattlesFile(result);

        //add lines which are not allowed so should be ignored.
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(path))) {
            writer.write("\n");
            writer.write("          \n");
            writer.write("       asdfaSDF   \n");
            writer.write("\t\n");
        }
        result = manager.readAll(path);

        Assert.assertEquals(0, result.size());

        //deleteFromBattlesFile the first
        manager.saveAll(path, list.subList(0, 2));
        manager.deleteFromBattlesFile(Collections.singletonList(list.get(0)));
        result = manager.readAll(path);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(list.get(1), result.get(0));

        //deleteFromBattlesFile in the middle, the first and the last
        manager.saveAll(path, list.subList(0, 5));
        manager.deleteFromBattlesFile(Arrays.asList(list.get(0), list.get(2), list.get(4)));
        result = manager.readAll(path);
        Assert.assertEquals(2, result.size());
        Assert.assertEquals(list.get(1), result.get(0));
        Assert.assertEquals(list.get(3), result.get(1));

        //do not deleteFromBattlesFile
        manager.saveAll(path, list.subList(0, 5));
        manager.deleteFromBattlesFile(Arrays.asList(list.get(6), list.get(7), list.get(8)));
        result = manager.readAll(path);
        Assert.assertEquals(5, result.size());
        for (int i = 0; i < result.size(); i++)
            Assert.assertEquals(list.get(i), result.get(i));

    }
}
