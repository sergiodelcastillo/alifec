package alifec.core.simulation.rules;

import alifec.core.contest.ParentTest;
import alifec.core.compilation.CompilationResult;
import alifec.core.compilation.CompileHelper;
import alifec.core.persistence.config.ContestConfig;
import alifec.core.simulation.*;

import alifec.core.simulation.nutrient.function.FamineFunction;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by Sergio Del Castillo on 14/10/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class EatRuleTest extends ParentTest {

    /**
     * This test method will be invoked by reflection in other JVM run to avoid the issue of the method System.load
     * which does not release the library until the JVM is closed.
     */
    public void testApplyImpl() throws Exception {
        //create the contest and the folder structure
        ContestConfig config = createContest("Contest-01");
        CompileHelper compileHelper = new CompileHelper(config);
        //compile MOs
        CompilationResult result = compileHelper.compileMOs();
        Assert.assertFalse(result.haveErrors());

        //create the environment
        Environment environment = new Environment(config);

        //create a battle: 0= first colony, 1= second colony, famine= uniform nutrient distribution
        createBattle(environment, 0, 1, FamineFunction.ID, new FamineFunction().getName());

        Cell mo = environment.getFirstOpponent().getMO(0);
        Movement mov = new Movement(0, 0);

        EatRule rule = new EatRule();
        Assert.assertEquals(Defs.E_INITIAL, mo.ene, 0.00001);
        float nutriInitial = environment.getAgar().getNutrient(mo.x, mo.y);
        Assert.assertEquals(ColonyRule.Status.NONE, rule.apply(environment, mo, mov, false));

        //validate energy of the mo. It should be last energy + 0.01* nutrient
        Assert.assertEquals(mo.ene, Defs.E_INITIAL + nutriInitial * (Defs.EAT_PERCENT), 0.00001);

        //validate that the nutrient have decreased 1%
        Assert.assertEquals(nutriInitial * (1 - Defs.EAT_PERCENT), environment.getAgar().getNutrient(mo.x, mo.y), 0.00001);
    }

    @Test
    public void testApply() throws IOException, InterruptedException {
        executeInDifferentVMProcess(this.getClass().getName(), "testApplyImpl");
    }
}