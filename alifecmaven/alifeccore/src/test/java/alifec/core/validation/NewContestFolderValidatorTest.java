package alifec.core.validation;

import alifec.core.contest.ParentTest;
import alifec.core.exception.ValidationException;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Sergio Del Castillo on 09/10/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class NewContestFolderValidatorTest extends ParentTest {

    @Test
    public void testInvalidContestFolder() throws IOException {
        NewContestFolderValidator validator = new NewContestFolderValidator();

        //empty string
        try {
            validator.validate(null);
            Assert.fail("It should be non valid.");
        } catch (ValidationException ex) {
        }
        try {
            validator.validate("");
            Assert.fail("It should be non valid.");
        } catch (ValidationException ex) {
        }
        try {
            validator.validate(" ");
            Assert.fail("It should be non valid.");
        } catch (ValidationException ex) {
        }
        try {
            validator.validate("     ");
            Assert.fail("It should be non valid.");
        } catch (ValidationException ex) {
        }
        try {
            validator.validate('\t' + "");
            Assert.fail("It should be non valid.");
        } catch (ValidationException ex) {
        }


        //Non valid names
        try {
            validator.validate(TEST_ROOT_PATH + File.separator + "");
            Assert.fail("It should be non valid.");
        } catch (ValidationException ex) {
        }

        try {
            validator.validate(TEST_ROOT_PATH + File.separator + "Contes-");
            Assert.fail("It should be non valid.");
        } catch (ValidationException ex) {
        }

        try {
            validator.validate(TEST_ROOT_PATH + File.separator + "Context-asdf");
            Assert.fail("It should be non valid.");
        } catch (ValidationException ex) {
        }

        //name too long
        try {
            validator.validate(TEST_ROOT_PATH + File.separator + "contest-00000000001111111111111111");
            Assert.fail("It should be non valid.");
        } catch (ValidationException ex) {
        }

        //existing folder
        try {

            Path file = Paths.get(TEST_ROOT_PATH + File.separator + "contest-01");
            Files.createDirectories(file.toAbsolutePath().normalize());
            validator.validate(TEST_ROOT_PATH + File.separator + "contest-01");
            Assert.fail("It should be non valid.");
        } catch (ValidationException ex) {
        }
    }

    @Test
    public void testValidContestFolder() {
        NewContestFolderValidator validator = new NewContestFolderValidator();

        try {
            validator.validate(TEST_ROOT_PATH + File.separator + "contest-01");
            validator.validate(TEST_ROOT_PATH + File.separator + "contest-1");
            validator.validate(TEST_ROOT_PATH + File.separator + "Contest-1");
            validator.validate(TEST_ROOT_PATH + File.separator + "Contest-0000000000111111111111111");
        } catch (ValidationException ex) {
            Assert.fail("It should be valid.");
        }
    }
}
