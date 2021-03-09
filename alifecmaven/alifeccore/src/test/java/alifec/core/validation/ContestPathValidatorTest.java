package alifec.core.validation;

import alifec.core.contest.ParentTest;
import alifec.core.exception.ConfigFileException;
import alifec.core.exception.CreateContestFolderException;
import alifec.core.exception.ValidationException;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by Sergio Del Castillo on 08/10/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class ContestPathValidatorTest extends ParentTest {


    public void testValidateImpl() throws CreateContestFolderException, IOException, URISyntaxException, ConfigFileException {
        ContestPathValidator validator = new ContestPathValidator();

        try {
            validator.validate(TEST_ROOT_PATH);
            Assert.fail("It should be non valid.");
        } catch (ValidationException ex) {
        }

        createContest("contest-01");
        try {
            validator.validate(TEST_ROOT_PATH);
        } catch (ValidationException ex) {
            Assert.fail("It should be valid.");
        }
    }

    @Test
    public void testValidate() throws IOException, InterruptedException {
        executeInDifferentVMProcess(this.getClass().getName(), "testValidateImpl");
    }
}
