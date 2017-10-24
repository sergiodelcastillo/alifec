package alifec.core.persistence;

import alifec.ParentTest;
import alifec.core.contest.Battle;
import alifec.core.contest.BattleResult;
import alifec.core.exception.*;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
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
    public void testAppend() throws CreateBattleException, IOException {
        List<BattleResult> list = battleResultDataSet();

        TournamentFileManager manager = new TournamentFileManager();
        String path = TEST_ROOT_PATH + File.separator + "battles.csv";

        for (BattleResult b : list)
            manager.append(path, b.toBattle());

        List<Battle> result = manager.readAll(path);

        Collections.sort(list);
        Collections.sort(result);

        Assert.assertEquals(list.size(), result.size());

        for (int i = 0; i < list.size(); i++) {
            Assert.assertEquals(list.get(i).toBattle(), result.get(i));
        }
    }

    @Test
    public void testSave() throws CreateBattleException, IOException {
        List<BattleResult> list = battleResultDataSet();

        TournamentFileManager manager = new TournamentFileManager();
        String path = TEST_ROOT_PATH + File.separator + "battles.csv";

        manager.saveAll(path, list);

        List<Battle> result = manager.readAll(path);

        Collections.sort(list);
        Collections.sort(result);

        Assert.assertEquals(list.size(), result.size());

        for (int i = 0; i < list.size(); i++) {
            Assert.assertEquals(list.get(i).toBattle(), result.get(i));
        }
    }

    @Test
    public void testDelete() throws CreateBattleException, IOException {
        List<BattleResult> list = battleResultDataSet();

        TournamentFileManager manager = new TournamentFileManager();
        String path = TEST_ROOT_PATH + File.separator + "battles.csv";

        //delete all
        manager.saveAll(path, list);
        List<Battle> result = manager.readAll(path);
        manager.delete(path, result);
        result = manager.readAll(path);
        Assert.assertEquals(0, result.size());

        //delete the first
        manager.saveAll(path, list.subList(0, 2));
        manager.delete(path, Arrays.asList(list.get(0).toBattle()));
        result = manager.readAll(path);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(list.get(1).toBattle(), result.get(0));

        //delete in the middle, the first and the last
        manager.saveAll(path, list.subList(0, 5));
        manager.delete(path, Arrays.asList(list.get(0).toBattle(), list.get(2).toBattle(), list.get(4).toBattle()));
        result = manager.readAll(path);
        Assert.assertEquals(2, result.size());
        Assert.assertEquals(list.get(1).toBattle(), result.get(0));
        Assert.assertEquals(list.get(3).toBattle(), result.get(1));

        //do not delete
        manager.saveAll(path, list.subList(0, 5));
        manager.delete(path, Arrays.asList(list.get(6).toBattle(), list.get(7).toBattle(), list.get(8).toBattle()));
        result = manager.readAll(path);
        Assert.assertEquals(5, result.size());
        for (int i = 0; i < result.size(); i++)
            Assert.assertEquals(list.get(i).toBattle(), result.get(i));

    }
}
