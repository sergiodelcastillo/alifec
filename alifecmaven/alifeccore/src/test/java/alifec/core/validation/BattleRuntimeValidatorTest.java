package alifec.core.validation;

import alifec.core.contest.Battle;
import alifec.core.exception.BattleException;
import alifec.core.exception.BattleException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Sergio Del Castillo on 30/10/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class BattleRuntimeValidatorTest {

    @Test
    public void testValidate() throws BattleException {

        try {
            Battle b = new Battle(1, 1, 1, "c1", "c2", "n");
            Assert.fail("It should fail.");
        } catch (BattleException e) {
        }

        try {
            Battle b= new Battle(-1, 1, 1, "c1", "c2", "n");
            Assert.fail("It should fail.");
        } catch (BattleException e) {
        }

        try {
            Battle b = new Battle(1, 2, -1, "c1", "c2", "n");
            Assert.fail("It should fail.");
        } catch (BattleException e) {
        }

        try {
            Battle b = new Battle(1, 2, 1, null, "c2", "n");
            Assert.fail("It should fail.");
        } catch (BattleException e) {
        }

        try {
            Battle b = new Battle(1, 2, 1, "c1", null, "n");
            Assert.fail("It should fail.");
        } catch (BattleException e) {
        }

        try {
            Battle b = new Battle(3, 1, 8, "c1", "c2", null);
            Assert.fail("It should fail.");
        } catch (BattleException e) {
        }

        try {
            Battle b = new Battle(1, 1, -1, "c1", "c2", "n");
            Assert.fail("It should fail.");
        } catch (BattleException e) {
        }

        try {
            Battle b = new Battle(1, 2, 6, "c1", "c2", "Famine");
        } catch (BattleException e) {
            Assert.fail("It should be OK.");
        }

    }
}
