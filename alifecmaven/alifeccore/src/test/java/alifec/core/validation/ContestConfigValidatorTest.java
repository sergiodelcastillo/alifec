package alifec.core.validation;

import alifec.core.exception.ValidationException;
import alifec.core.persistence.config.ContestConfig;
import org.junit.Assert;
import org.junit.Test;

import java.util.Locale;
import java.util.ResourceBundle;

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
        ResourceBundle bundle = ResourceBundle.getBundle("i18n/messages", Locale.ENGLISH);

        ContestConfig contestConfig = new ContestConfig(bundle, ".", "");
        contestConfig.setPauseBetweenBattles(-1);
        try {
            validator.validate(contestConfig);
            Assert.fail("it should fail");
        } catch (ValidationException e) {

        }
    }
}
