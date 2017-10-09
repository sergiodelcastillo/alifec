package alifec.core.validation;

import alifec.core.contest.Contest;
import alifec.core.persistence.ContestConfig;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
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
    public boolean validate(String name) {
        if (name == null) return false;

        Matcher matcher = pattern.matcher(name);

        return matcher.matches();
    }

    public static boolean checkPrefix(String folder) {
        return folder != null && folder.startsWith(ContestConfig.CONTEST_NAME_PREFIX);
    }
}
