package alifec.core.validation;

import alifec.core.exception.ValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
            Assertions.fail("It should fail.");
        } catch (ValidationException e) {
        }
        try {
            validator.validate("");
            Assertions.fail("It should fail.");
        } catch (ValidationException e) {
        }
        try {
            validator.validate("\t");
            Assertions.fail("It should fail.");
        } catch (ValidationException e) {
        }
        try {
            validator.validate("        ");
            Assertions.fail("It should fail.");
        } catch (ValidationException e) {
        }
        try {
            validator.validate("        ");
            Assertions.fail("It should fail.");
        } catch (ValidationException e) {
        }

        try {
            validator.validate("asdf,asdf, asdfa, asdf,");
            Assertions.fail("It should fail.");
        } catch (ValidationException e) {
        }
        try {
            validator.validate("asdf");
            Assertions.fail("It should fail.");
        } catch (ValidationException e) {
        }
        try {
            validator.validate(",,,,");
            Assertions.fail("It should fail.");
        } catch (ValidationException e) {
        }
        try {
            validator.validate("a,b,1,,");
            Assertions.fail("It should fail.");
        } catch (ValidationException e) {
        }
        try {
            validator.validate("a,b,1,a,a");
            Assertions.fail("It should fail.");
        } catch (ValidationException e) {
        }
        try {
            validator.validate("a,b,a,1,2");
            Assertions.fail("It should fail.");
        } catch (ValidationException e) {
        }
        try {
            validator.validate("a,b,a,1,2");
            Assertions.fail("It should fail.");
        } catch (ValidationException e) {
        }
        try {
            validator.validate("a,b,1,b,2");
            Assertions.fail("It should fail.");
        } catch (ValidationException e) {
        }
        try {
            validator.validate("a,b,1,b,2");
            Assertions.fail("It should fail.");
        } catch (ValidationException e) {
        }
        try {
            validator.validate("a,b,Balls,b,2");
            Assertions.fail("It should fail.");
        } catch (ValidationException e) {
        }
        try {
            validator.validate("a,,Balls,1,2");
            Assertions.fail("It should fail.");
        } catch (ValidationException e) {
        }
        try {
            validator.validate("a,b,Balls,1,2.0,0");
            Assertions.fail("It should fail.");
        } catch (ValidationException e) {
        }
        try {
            validator.validate("a,b,Balls,1,-2.0");
            Assertions.fail("It should fail.");
        } catch (ValidationException e) {
        }
        try {
            validator.validate("a,b,Balls,-1,0.0");
            Assertions.fail("It should fail.");
        } catch (ValidationException e) {
        }
        try {
            validator.validate("a,b,Balls,-0.0,0");
        } catch (ValidationException e) {
            Assertions.fail("It should not fail.");
        }

        try {
            validator.validate("a,b,Balls,1,2.0");
        } catch (ValidationException e) {
            Assertions.fail("It should be ok.");
        }
        try {
            validator.validate("abbb,b11,Famine,1.0,2.0");
        } catch (ValidationException e) {
            Assertions.fail("It should be ok.");
        }
        try {
            validator.validate("a,b,Balls,1,2.0");
        } catch (ValidationException e) {
            Assertions.fail("It should be ok.");
        }
        try {
            validator.validate("a,b,Balls,1,2.0");
        } catch (ValidationException e) {
            Assertions.fail("It should be ok.");
        }
        try {
            validator.validate("a,b,Balls,1,2.0");
        } catch (ValidationException e) {
            Assertions.fail("It should be ok.");
        }
        try {
            validator.validate("\"a,b\",Balls,1,2.0");
        } catch (ValidationException e) {
            Assertions.fail("It should be ok.");
        }
    }
}
