package alifec.core.validation;

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
        Assert.assertFalse(contestNameValidator.validate(null));
        Assert.assertFalse(contestNameValidator.validate(""));
        Assert.assertFalse(contestNameValidator.validate("asdñfjlaskdfjalñksdfjlakjsdfñlk"));
        Assert.assertFalse(contestNameValidator.validate("contest-"));
        Assert.assertFalse(contestNameValidator.validate("contest-*"));
        Assert.assertFalse(contestNameValidator.validate("contest-a*"));
        Assert.assertFalse(contestNameValidator.validate("contest-b+"));
        Assert.assertFalse(contestNameValidator.validate("contest-+"));
        Assert.assertFalse(contestNameValidator.validate("1contest-0"));
        Assert.assertFalse(contestNameValidator.validate("ccontest-01"));
        Assert.assertFalse(contestNameValidator.validate("_contest-01"));
        Assert.assertFalse(contestNameValidator.validate("contest-1111122222111112222211111a"));

        Assert.assertTrue(contestNameValidator.validate("CONTEST-01"));
        Assert.assertTrue(contestNameValidator.validate("Contest-01"));
        Assert.assertTrue(contestNameValidator.validate("cOntEsT-01"));
        Assert.assertTrue(contestNameValidator.validate("contest-1"));
        Assert.assertTrue(contestNameValidator.validate("contest-1111122222111112222211111"));
        Assert.assertTrue(contestNameValidator.validate("contest-abc1234DEF"));
    }
}
