package alifec.core.validation;

import alifec.core.contest.Battle;
import alifec.core.exception.BattleException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


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
            Assertions.fail("It should fail.");
        } catch (BattleException e) {
        }

        try {
            Battle b = new Battle(-1, 1, 1, "c1", "c2", "n");
            Assertions.fail("It should fail.");
        } catch (BattleException e) {
        }

        try {
            Battle b = new Battle(1, 2, -1, "c1", "c2", "n");
            Assertions.fail("It should fail.");
        } catch (BattleException e) {
        }

        try {
            Battle b = new Battle(1, 2, 1, null, "c2", "n");
            Assertions.fail("It should fail.");
        } catch (BattleException e) {
        }

        try {
            Battle b = new Battle(1, 2, 1, "c1", null, "n");
            Assertions.fail("It should fail.");
        } catch (BattleException e) {
        }

        try {
            Battle b = new Battle(3, 1, 8, "c1", "c2", null);
            Assertions.fail("It should fail.");
        } catch (BattleException e) {
        }

        try {
            Battle b = new Battle(1, 1, -1, "c1", "c2", "n");
            Assertions.fail("It should fail.");
        } catch (BattleException e) {
        }

        try {
            Battle b = new Battle(1, 2, 6, "c1", "c2", "Famine");
        } catch (BattleException e) {
            Assertions.fail("It should be OK.");
        }

    }
}
