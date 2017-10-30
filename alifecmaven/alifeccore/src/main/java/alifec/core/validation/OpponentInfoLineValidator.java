package alifec.core.validation;

import alifec.core.exception.ValidationException;

/**
 * Created by Sergio Del Castillo on 28/10/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class OpponentInfoLineValidator implements Validator<String> {

    //TODO: TEST
    @Override
    public void validate(String line) throws ValidationException {
        if (line == null || line.isEmpty())
            throw new ValidationException("The opponent info line is empty.");

        String[] info = line.trim().split(",");

        if (info.length != 3)
            throw new ValidationException("The opponent info line must have the pattern: <name>,<author>,<affiliation>");
    }
}
