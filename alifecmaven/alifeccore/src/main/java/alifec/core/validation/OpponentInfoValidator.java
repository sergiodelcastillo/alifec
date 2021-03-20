package alifec.core.validation;

import alifec.core.contest.oponentInfo.OpponentInfo;
import alifec.core.exception.ValidationException;

import java.util.Objects;

/**
 * Created by Sergio Del Castillo on 28/10/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class OpponentInfoValidator implements Validator<OpponentInfo> {

    //TODO: TEST

    @Override
    public OpponentInfo validate(OpponentInfo info) throws ValidationException {
        if (Objects.isNull(info.getName()) || info.getName().trim().isEmpty())
            throw new ValidationException("The opponent info line is invalid. The name of the colony is empty.");

        if (Objects.isNull(info.getAuthor()) || info.getAuthor().trim().isEmpty())
            throw new ValidationException("The opponent info line is invalid. The author of the colony is empty.");

        if (Objects.isNull(info.getAffiliation()) || info.getAffiliation().trim().isEmpty())
            throw new ValidationException("The opponent info line is invalid. The affiliation of the author is empty.");

        return info;
    }
}
