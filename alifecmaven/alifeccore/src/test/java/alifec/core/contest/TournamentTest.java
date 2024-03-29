package alifec.core.contest;

import alifec.core.compilation.CompilationResult;
import alifec.core.compilation.CompileHelper;
import alifec.core.exception.BattleException;
import alifec.core.exception.ConfigFileException;
import alifec.core.exception.ContestException;
import alifec.core.exception.CreateContestFolderException;
import alifec.core.exception.TournamentException;
import alifec.core.persistence.config.ContestConfig;
import alifec.core.simulation.Environment;
import alifec.core.simulation.nutrient.BallsNutrient;
import alifec.core.simulation.nutrient.function.FamineFunction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Sergio Del Castillo on 23/10/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class TournamentTest extends ParentTest {

    /**
     * This test method will be invoked by reflection in other JVM run to avoid the issue of the method System.load
     * which does not release the library until the JVM is closed.
     */
    public void testDeleteImpl() throws URISyntaxException, ConfigFileException, CreateContestFolderException, IOException, ContestException, TournamentException, BattleException {
        ContestConfig config = createContest("Contest-01");
        //ensure the competition mode
        config.setMode(ContestConfig.COMPETITION_MODE);
        config.save();

        CompileHelper compiler = new CompileHelper(config);
        //compile MOs
        CompilationResult result = compiler.compileMOs();
        Assertions.assertFalse(result.haveErrors());


        Contest contest = new Contest(config);

        Environment environment = contest.getEnvironment();

        List<String> colonies = environment.getOpponentNames();
        contest.newTournament();
        Assertions.assertEquals(6, colonies.size(), "Colonies list must have 6");

        List<Battle> battles = new ArrayList<>();
        FamineFunction famineFunction = new FamineFunction();
        BallsNutrient ballsNutrient = new BallsNutrient();

        Battle battle1 = new Battle(
                0, 1,
                famineFunction.getId(),
                colonies.get(0),
                colonies.get(1),
                famineFunction.getName());
        battle1.setWinner(0, 100f);

        Battle battle2 = new Battle(
                0, 1,
                ballsNutrient.getId(),
                colonies.get(0),
                colonies.get(1),
                ballsNutrient.toString());
        battle1.setWinner(0, 10f);

        Battle battle3 = new Battle(
                0, 2,
                ballsNutrient.getId(),
                colonies.get(0),
                colonies.get(2),
                ballsNutrient.toString());
        battle1.setWinner(2, 150f);

        Battle battle4 = new Battle(
                0, 2,
                famineFunction.getId(),
                colonies.get(0),
                colonies.get(2),
                famineFunction.getName());
        battle1.setWinner(0, 50f);

        contest.lastTournament().addBattle(battle1);
        contest.lastTournament().addBattle(battle2);
        contest.lastTournament().addBattle(battle3);
        contest.lastTournament().addBattle(battle4);


        contest.lastTournament().delete(colonies.get(2));

        Assertions.assertEquals(contest.lastTournament().getBattles().size(), 2);
        Assertions.assertEquals(contest.lastTournament().getColonyNames().size(), 5);

        //   Assert.assertTrue(contest.lastTournament().size() == 2);

        List<String> names = contest.lastTournament().getColonyNames();
        List<String> target = new ArrayList<>();
        target.add(colonies.get(0));
        target.add(colonies.get(1));
        target.add(colonies.get(3));
        target.add(colonies.get(4));
        target.add(colonies.get(5));


        Collections.sort(target);
        Collections.sort(names);

        Assertions.assertEquals(5, names.size());

        Assertions.assertEquals(names, target);
    }

    @Test
    public void testDelete() throws InterruptedException, IOException {
        executeInDifferentVMProcess(this.getClass().getName(), "testDeleteImpl");
    }

}
