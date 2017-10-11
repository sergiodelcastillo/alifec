package alifec.core.validation;

import alifec.ParentTest;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

/**
 * Created by Sergio Del Castillo on 09/10/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class NewContestFolderValidatorTest extends ParentTest {

    @Test
    public void testInvalidContestFolder(){
        NewContestFolderValidator validator = new NewContestFolderValidator();

        //empty string
        Assert.assertFalse(validator.validate(null));
        Assert.assertFalse(validator.validate(""));
        Assert.assertFalse(validator.validate(" "));
        Assert.assertFalse(validator.validate("     "));
        Assert.assertFalse(validator.validate('\t'+""));

        //Non valid names
        Assert.assertFalse(validator.validate(TEST_ROOT_PATH+ File.separator+""));
        Assert.assertFalse(validator.validate(TEST_ROOT_PATH+ File.separator+"Contes-"));
        Assert.assertFalse(validator.validate(TEST_ROOT_PATH+ File.separator+"Context-asdf"));

        //name too long
        Assert.assertFalse(validator.validate(TEST_ROOT_PATH+ File.separator+"contest-00000000001111111111111111"));

        //existing folder
        File file = new File(TEST_ROOT_PATH + File.separator +  "contest-01");
        Assert.assertTrue(file.mkdir());
        Assert.assertFalse(validator.validate(TEST_ROOT_PATH+ File.separator+"contest-01"));
    }

    @Test
    public void testValidContestFolder(){
        NewContestFolderValidator validator = new NewContestFolderValidator();

        Assert.assertTrue(validator.validate(TEST_ROOT_PATH+ File.separator+"contest-01"));
        Assert.assertTrue(validator.validate(TEST_ROOT_PATH+ File.separator+"contest-1"));
        Assert.assertTrue(validator.validate(TEST_ROOT_PATH+ File.separator+"Contest-1"));
        Assert.assertTrue(validator.validate(TEST_ROOT_PATH+ File.separator+"Contest-0000000000111111111111111"));
    }
}
