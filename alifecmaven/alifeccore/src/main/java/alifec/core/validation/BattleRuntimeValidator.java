package alifec.core.validation;

import alifec.core.contest.Battle;
import alifec.core.exception.CreateBattleException;

/**
 * Created by Sergio Del Castillo on 27/10/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class BattleRuntimeValidator implements Validator<Battle> {

    //todo create test
    @Override
    public boolean validate(Battle battle) {
        if(battle.getFirstColonyId() < 0)
            return false;

        if(battle.getSecondColonyId() < 0)
            return false;

        if(battle.getFirstColonyId() == battle.getSecondColonyId())
            return false;

        if(battle.getNutrientId() < 0) return false;

        return true;
    }
}
