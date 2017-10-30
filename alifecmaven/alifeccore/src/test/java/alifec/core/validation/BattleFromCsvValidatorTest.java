package alifec.core.validation;

import alifec.core.exception.ValidationException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Sergio Del Castillo on 30/10/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class BattleFromCsvValidatorTest {

    @Test
    public void testValidate() {
        BattleFromCsvValidator validator = new BattleFromCsvValidator();
        try {
            validator.validate(null);
            Assert.fail("It should fail.");
        } catch (ValidationException e) {
        }
        try {
            validator.validate("");
            Assert.fail("It should fail.");
        } catch (ValidationException e) {
        }
        try {
            validator.validate("\t");
            Assert.fail("It should fail.");
        } catch (ValidationException e) {
        }
        try {
            validator.validate("        ");
            Assert.fail("It should fail.");
        } catch (ValidationException e) {
        }
        try {
            validator.validate("        ");
            Assert.fail("It should fail.");
        } catch (ValidationException e) {
        }

        try {
            validator.validate("asdf,asdf, asdfa, asdf,");
            Assert.fail("It should fail.");
        } catch (ValidationException e) {
        }
        try {
            validator.validate("asdf");
            Assert.fail("It should fail.");
        } catch (ValidationException e) {
        }
        try {
            validator.validate(",,,,");
            Assert.fail("It should fail.");
        } catch (ValidationException e) {
        }
        try {
            validator.validate("a,b,1,,");
            Assert.fail("It should fail.");
        } catch (ValidationException e) {
        }
        try {
            validator.validate("a,b,1,a,a");
            Assert.fail("It should fail.");
        } catch (ValidationException e) {
        }
        try {
            validator.validate("a,b,a,1,2");
            Assert.fail("It should fail.");
        } catch (ValidationException e) {
        }
        try {
            validator.validate("a,b,a,1,2");
            Assert.fail("It should fail.");
        } catch (ValidationException e) {
        }
        try {
            validator.validate("a,b,1,b,2");
            Assert.fail("It should fail.");
        } catch (ValidationException e) {
        }
        try {
            validator.validate("a,b,1,b,2");
            Assert.fail("It should fail.");
        } catch (ValidationException e) {
        }
        try {
            validator.validate("a,b,Balls,b,2");
            Assert.fail("It should fail.");
        } catch (ValidationException e) {
        }
        try {
            validator.validate("a,,Balls,1,2");
            Assert.fail("It should fail.");
        } catch (ValidationException e) {
        }
        try {
            validator.validate("a,b,Balls,1,2.0,0");
            Assert.fail("It should fail.");
        } catch (ValidationException e) {
        }
        try {
            validator.validate("a,b,Balls,1,-2.0");
            Assert.fail("It should fail.");
        } catch (ValidationException e) {
        }
        try {
            validator.validate("a,b,Balls,-1,0.0");
            Assert.fail("It should fail.");
        } catch (ValidationException e) {
        }
        try {
            validator.validate("a,b,Balls,-0.0,0");
        } catch (ValidationException e) {
            Assert.fail("It should not fail.");
        }

        try {
            validator.validate("a,b,Balls,1,2.0");
        } catch (ValidationException e) {
            Assert.fail("It should be ok.");
        }
        try {
            validator.validate("abbb,b11,Famine,1.0,2.0");
        } catch (ValidationException e) {
            Assert.fail("It should be ok.");
        }
        try {
            validator.validate("a,b,Balls,1,2.0");
        } catch (ValidationException e) {
            Assert.fail("It should be ok.");
        }
        try {
            validator.validate("a,b,Balls,1,2.0");
        } catch (ValidationException e) {
            Assert.fail("It should be ok.");
        }
        try {
            validator.validate("a,b,Balls,1,2.0");
        } catch (ValidationException e) {
            Assert.fail("It should be ok.");
        }
        try {
            validator.validate("\"a,b\",Balls,1,2.0");
        } catch (ValidationException e) {
            Assert.fail("It should be ok.");
        }
    }
}
