package alifec.core.validation;

import alifec.core.contest.Battle;
import alifec.core.exception.ValidationException;

/**
 * Created by Sergio Del Castillo on 27/10/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class BattleRuntimeValidator implements Validator<Battle> {

    //todo create test
    @Override
    public void validate(Battle battle) throws ValidationException{
        if(battle.getFirstColonyId() < 0)
            throw new ValidationException("The first opponent id must be a positive value.");


        if(battle.getSecondColonyId() < 0)
            throw new ValidationException("The second opponent id must be a positive value.");


        if(battle.getFirstColonyId() == battle.getSecondColonyId())
            throw new ValidationException("First and second opponents must have different value.");

        if(battle.getNutrientId() < 0)
            throw new ValidationException("The nutrient distribution must be a positive value.");

    }
}
