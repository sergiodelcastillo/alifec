package alifec.core.simulation.rules;

import alifec.ParentTest;
import alifec.core.compilation.CompilationResult;
import alifec.core.compilation.CompileHelper;
import alifec.core.contest.tournament.battles.BattleRun;
import alifec.core.exception.CreateBattleException;
import alifec.core.persistence.ContestConfig;
import alifec.core.simulation.*;
import alifec.core.simulation.nutrients.Famine;
import alifec.core.simulation.nutrients.InclinedPlane;
import alifec.core.simulation.nutrients.Nutrient;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Sergio Del Castillo on 14/10/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class EatRuleTest extends ParentTest {
    @Test
    public void testApply() throws Exception {
        //create the contest and the folder structure
        ContestConfig config = createContest("Contest-01");

        //compile MOs
        CompilationResult result = CompileHelper.compileMOs(config);
        Assert.assertFalse(result.haveErrors());

        //create the environment
        Environment environment = new Environment(config);

        //create a battle: 0= first colony, 1= second colony, famine= uniform nutrient distribution
        Nutrient dist = new Famine();
        createBattle(environment, 0, 1, dist);

        Cell mo = environment.getFirstOpponent().getMO(0);
        Movement mov = new Movement(0, 0);
        boolean mitosis = false;

        EatRule rule = new EatRule();
        Assert.assertEquals(Defs.E_INITIAL, mo.ene, 0.00001);
        float nutriInitial = environment.getAgar().getNutrient(mo.x, mo.y);
        Assert.assertEquals(ColonyRule.Status.NONE, rule.apply(environment, mo, mov, mitosis));

        //validate energy of the mo. It should be last energy + 0.01* nutrient
        Assert.assertEquals(mo.ene, Defs.E_INITIAL + nutriInitial * (Defs.EAT_PERCENT), 0.00001);

        //validate that the nutrient have decreased 1%
        Assert.assertEquals(dist.getNutrient(mo.x, mo.y) * (1 - Defs.EAT_PERCENT), environment.getAgar().getNutrient(mo.x, mo.y), 0.00001);
    }


}