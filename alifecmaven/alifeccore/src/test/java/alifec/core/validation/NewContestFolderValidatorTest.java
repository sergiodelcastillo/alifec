package alifec.core.validation;

import alifec.core.contest.ParentTest;
import alifec.core.exception.ValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
            Assertions.fail("It should be non valid.");
        } catch (ValidationException ex) {
        }
        try {
            validator.validate("");
            Assertions.fail("It should be non valid.");
        } catch (ValidationException ex) {
        }
        try {
            validator.validate(" ");
            Assertions.fail("It should be non valid.");
        } catch (ValidationException ex) {
        }
        try {
            validator.validate("     ");
            Assertions.fail("It should be non valid.");
        } catch (ValidationException ex) {
        }
        try {
            validator.validate('\t' + "");
            Assertions.fail("It should be non valid.");
        } catch (ValidationException ex) {
        }


        //Non valid names
        try {
            validator.validate(TEST_ROOT_PATH + File.separator + "");
            Assertions.fail("It should be non valid.");
        } catch (ValidationException ex) {
        }

        try {
            validator.validate(TEST_ROOT_PATH + File.separator + "Contes-");
            Assertions.fail("It should be non valid.");
        } catch (ValidationException ex) {
        }

        try {
            validator.validate(TEST_ROOT_PATH + File.separator + "Context-asdf");
            Assertions.fail("It should be non valid.");
        } catch (ValidationException ex) {
        }

        //name too long
        try {
            validator.validate(TEST_ROOT_PATH + File.separator + "contest-00000000001111111111111111");
            Assertions.fail("It should be non valid.");
        } catch (ValidationException ex) {
        }

        //existing folder
        try {

            Path file = Paths.get(TEST_ROOT_PATH + File.separator + "contest-01");
            Files.createDirectories(file.toAbsolutePath().normalize());
            validator.validate(TEST_ROOT_PATH + File.separator + "contest-01");
            Assertions.fail("It should be non valid.");
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
            Assertions.fail("It should be valid.");
        }
    }
}
