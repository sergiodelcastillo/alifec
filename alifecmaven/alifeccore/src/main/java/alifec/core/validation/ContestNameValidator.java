package alifec.core.validation;

import alifec.core.exception.ValidationException;
import alifec.core.persistence.config.ContestConfig;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validate if the Contest Name have the right pattern: contest-"<xx>".
 * Please not that contest- is not case sensitive and xx are numbers between 0 and 9999999999999999999999999.
 * <p>
 * Created by Sergio Del Castillo on 08/10/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class ContestNameValidator implements Validator<String> {
    private static String STRING_PATTERN = "^(" + ContestConfig.CONTEST_NAME_PREFIX + ")([a-zA-Z_0-9]{1,25})$";

    private Pattern pattern;

    public ContestNameValidator() {
        pattern = Pattern.compile(STRING_PATTERN, Pattern.CASE_INSENSITIVE);
    }

    @Override
    public void validate(String name) throws ValidationException {
        if (name == null || name.trim().isEmpty())
            throw new ValidationException("The contest name must not be null");

        if (!name.startsWith(ContestConfig.CONTEST_NAME_PREFIX)) {
            throw new ValidationException("The contest name does not start with " + ContestConfig.CONTEST_NAME_PREFIX);
        }

        Matcher matcher = pattern.matcher(name);

        if (!matcher.matches()) {
            throw new ValidationException("The contest name does not have the pattern: contest-<NN>.");
        }
    }
}
