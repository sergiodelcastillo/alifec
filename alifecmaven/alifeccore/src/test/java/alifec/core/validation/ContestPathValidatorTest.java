package alifec.core.validation;

import alifec.ParentTest;
import alifec.core.exception.ConfigFileException;
import alifec.core.exception.CreateContestFolderException;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by Sergio Del Castillo on 08/10/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class ContestPathValidatorTest extends ParentTest{

    @Test
    public void testValidate() throws CreateContestFolderException, IOException, URISyntaxException, ConfigFileException {
        ContestPathValidator validator = new ContestPathValidator();

        Assert.assertFalse(validator.validate(TEST_ROOT_PATH));

        createContest("contest-01");


        Assert.assertTrue(validator.validate(TEST_ROOT_PATH));

    }
}
