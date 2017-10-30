package alifec.core.validation;

import alifec.core.exception.ValidationException;
import alifec.core.persistence.config.ContestConfig;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Sergio Del Castillo on 30/10/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class ContestConfigValidatorTest {

    @Test
    public void testValidate() {
        //todo: COMPLETE!!
        ContestConfigValidator validator = new ContestConfigValidator();
        ContestConfig contestConfig = new ContestConfig(".", "");
        contestConfig.setPauseBetweenBattles(-1);
        try {
            validator.validate(contestConfig);
            Assert.fail("it should fail");
        } catch (ValidationException e) {

        }
    }
}
