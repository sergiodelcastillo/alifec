package alifec.core.persistence;

import alifec.ParentTest;
import alifec.core.contest.Battle;
import alifec.core.exception.BattleException;
import alifec.core.simulation.Cell;
import alifec.core.simulation.nutrient.Nutrient;
import alifec.core.simulation.nutrient.TestNutrient;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Sergio Del Castillo on 14/11/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class SimulationFileManagerImpl3Test extends ParentTest {

    public void testCompressStartBattle() throws BattleException {
        Nutrient nutrient = new TestNutrient();
        nutrient.init();
        List<Cell> mos = initializeMos();
        Battle battle = new Battle(1, 2, nutrient.getId(), "mo1", "mo2", nutrient.getName());

    }

    private List<Cell> initializeMos() {
        Cell mo1 = new Cell(1);
        mo1.x = 10;
        mo1.y = -10;
        mo1.ene = 1000f;
        Cell mo2 = new Cell(2);
        mo2.x = 20;
        mo2.y = -20;
        mo2.ene = 500f;
        Cell mo3 = new Cell(3);
        mo3.x = 30;
        mo3.y = -30;
        mo3.ene = 900f;
        return Arrays.asList(mo1, mo2, mo3);
    }
}
