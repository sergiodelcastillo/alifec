package alifec.core.validation;

import alifec.core.exception.ValidationException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Sergio Del Castillo on 08/10/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class ContestNameValidatorTest {
    @Test
    public void testValidate() {
        ContestNameValidator contestNameValidator = new ContestNameValidator();

        //false contest names.
        try {
            contestNameValidator.validate(null);
            Assert.fail("It should be non valid.");
        } catch (ValidationException ex) {
        }
        try {
            contestNameValidator.validate("");
            Assert.fail("It should be non valid.");
        } catch (ValidationException ex) {
        }
        try {
            contestNameValidator.validate("asdñfjlaskdfjalñksdfjlakjsdfñlk");
            Assert.fail("It should be non valid.");
        } catch (ValidationException ex) {
        }
        try {
            contestNameValidator.validate("contest-");
            Assert.fail("It should be non valid.");
        } catch (ValidationException ex) {
        }
        try {
            contestNameValidator.validate("contest-*");
            Assert.fail("It should be non valid.");
        } catch (ValidationException ex) {
        }
        try {
            contestNameValidator.validate("contest-a*");
            Assert.fail("It should be non valid.");
        } catch (ValidationException ex) {
        }
        try {
            contestNameValidator.validate("contest-b+");
            Assert.fail("It should be non valid.");
        } catch (ValidationException ex) {
        }
        try {
            contestNameValidator.validate("contest-+");
            Assert.fail("It should be non valid.");
        } catch (ValidationException ex) {
        }

        try {
            contestNameValidator.validate("1contest-0");
            Assert.fail("It should be non valid.");
        } catch (ValidationException ex) {
        }
        try {
            contestNameValidator.validate("ccontest-01");
            Assert.fail("It should be non valid.");
        } catch (ValidationException ex) {
        }
        try {
            contestNameValidator.validate("_contest-01");
            Assert.fail("It should be non valid.");
        } catch (ValidationException ex) {
        }
        try {
            contestNameValidator.validate("contest-1111122222111112222211111a");
            Assert.fail("It should be non valid.");
        } catch (ValidationException ex) {
        }


        //valid
        try {
            contestNameValidator.validate("CONTEST-01");
            contestNameValidator.validate("Contest-01");
            contestNameValidator.validate("cOntEsT-01");
            contestNameValidator.validate("contest-1");
            contestNameValidator.validate("contest-1111122222111112222211111");
            contestNameValidator.validate("contest-abc1234DEF");
        } catch (ValidationException ex) {
            Assert.fail("It should be valid.");
        }

    }
}
