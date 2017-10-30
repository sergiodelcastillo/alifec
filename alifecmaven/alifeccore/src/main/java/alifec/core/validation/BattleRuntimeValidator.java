package alifec.core.validation;

import alifec.core.contest.Battle;
import alifec.core.exception.ValidationException;

/**
 * Created by Sergio Del Castillo on 27/10/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class BattleRuntimeValidator implements Validator<Battle> {

    @Override
    public void validate(Battle battle) throws ValidationException {
        if (battle.getFirstColonyId() < 0)
            throw new ValidationException("The first opponent id must be a positive value.");

        if (battle.getSecondColonyId() < 0)
            throw new ValidationException("The second opponent id must be a positive value.");

        if (battle.getNutrientId() < 0)
            throw new ValidationException("The nutrient distribution must be a positive value.");

        if (battle.getFirstColony() == null || battle.getFirstColony().trim().isEmpty())
            throw new ValidationException("The first opponent name is null.");

        if (battle.getSecondColony() == null || battle.getSecondColony().trim().isEmpty())
            throw new ValidationException("The second opponent name is null.");

        if (battle.getNutrient() == null || battle.getNutrient().trim().isEmpty())
            throw new ValidationException("The name of the nutrient distribution is null.");

        if (battle.getFirstColonyId() == battle.getSecondColonyId() ||
                battle.getFirstColony().equals(battle.getSecondColony()))
            throw new ValidationException("First and second opponents must have different value.");
    }
}
