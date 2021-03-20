package alifec.core.validation;

import alifec.core.exception.ValidationException;

import java.util.Objects;

/**
 * Created by Sergio Del Castillo on 14/11/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class LiveInstanceValidator implements Validator<String> {
    @Override
    public String validate(String line) throws ValidationException {
        if (Objects.isNull(line) || line.trim().isEmpty())
            throw new ValidationException("The line is null");

        if (line.trim().length() < 3) {
            throw new ValidationException("The line is too short.");
        }

        char c = line.charAt(0);

        if (c != 'b' && c != 'n' && c != 'm' && c != 'e') {
            throw new ValidationException("The line must start with character: b, n, m or e.");
        }

        return line;
    }
}
