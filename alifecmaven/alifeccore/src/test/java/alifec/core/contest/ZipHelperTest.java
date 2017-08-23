package alifec.core.contest;

import alifec.ParentTest;
import alifec.core.exception.CreateContestFolderException;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

/**
 * Created by Sergio Del Castillo on 22/08/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class ZipHelperTest extends ParentTest {

    @Test
    public void testCreateZip() throws IOException, CreateContestFolderException {
        String contestName = "contest-01";

        ContestConfig config = createContest(contestName);

        ZipHelper.createZip(config);

        Assert.assertEquals(1,new File(config.getBackupFolder()).list((dir, name) -> name.contains(".zip")).length);

    }

    @Test
    public void testCreateZip2(){
        //TODO: improve test
    }
}
