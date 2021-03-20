package alifec.core.validation;

import alifec.core.exception.ValidationException;
import alifec.core.persistence.config.ContestConfig;

import java.util.Objects;

/**
 * Created by Sergio Del Castillo on 28/10/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class BattleFromCsvValidator implements Validator<String> {

    @Override
    public String validate(String line) throws ValidationException {
        if (Objects.isNull(line) || line.isEmpty())
            throw new ValidationException("The battle line is empty.");

        String[] tmp = line.split(",");

        //5 elements: name1, name2, nutrient, energy1, energy2
        if (tmp.length != 5)
            throw new ValidationException("The battle line does not have the pattern: <name1>,<name2>,<nutrient>,<energy1>,<energy2>");

        checkColonyName(tmp[0]);
        checkColonyName(tmp[1]);
        checkNutrientName(tmp[2]);
        checkPositiveFloat(tmp[3]);
        checkPositiveFloat(tmp[4]);

        return line;
    }

    private void checkNutrientName(String nutri) throws ValidationException {
        if (Objects.isNull(nutri) || nutri.isEmpty())
            throw new ValidationException("The battle line is invalid. Nutrient is empty.");

        if (Objects.isNull(ContestConfig.getNutrientByName(nutri)))
            throw new ValidationException("The battle line is invalid. The nutrient distribution " + nutri + " is unknown.");
    }

    private void checkColonyName(String name) throws ValidationException {
        if (Objects.isNull(name) || name.trim().length() == 0)
            throw new ValidationException("The battle line is invalid. Colony name is empty");
    }

    private void checkPositiveFloat(String s) throws ValidationException {
        try {
            if (Float.parseFloat(s) < 0.0f)
                throw new ValidationException("The battle line is invalid. MO energy can not be negative.");

        } catch (Throwable t) {
            throw new ValidationException("The battle line is invalid. Energy must be a float value.");
        }
    }
}
