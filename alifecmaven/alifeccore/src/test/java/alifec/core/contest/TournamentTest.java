package alifec.core.contest;

import alifec.ParentTest;
import alifec.core.compilation.CompilationResult;
import alifec.core.compilation.CompileHelper;
import alifec.core.exception.*;
import alifec.core.persistence.config.ContestConfig;
import alifec.core.simulation.Environment;
import alifec.core.simulation.nutrient.BallsNutrient;
import alifec.core.simulation.nutrient.function.FamineFunction;
import org.junit.Assert;
import org.junit.Test;

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
    @Test
    public void testDelete() throws URISyntaxException, ConfigFileException, CreateContestFolderException, IOException, CreateContestException, CreateTournamentException, CreateBattleException {
        ContestConfig config = createContest("Contest-01");
        //ensure the competition mode
        config.setMode(ContestConfig.COMPETITION_MODE);
        config.save();

        //compile MOs
        CompilationResult result = CompileHelper.compileMOs(config);
        Assert.assertFalse(result.haveErrors());

        Contest contest = new Contest(config);

        //create the environment
        Environment environment = new Environment(config);

        List<String> colonies = environment.getOpponentNames();
        contest.newTournament(colonies);
        Assert.assertTrue("Colonies list must have at least 3", colonies.size() > 2);

        List<Battle> battles = new ArrayList<>();
        FamineFunction famineFunction = new FamineFunction();
        BallsNutrient ballsNutrient = new BallsNutrient();

        Battle battle1 = new Battle(
                environment.getColonyIdByName(colonies.get(0)),
                environment.getColonyIdByName(colonies.get(1)),
                famineFunction.getId(),
                colonies.get(0),
                colonies.get(1),
                famineFunction.getName());
        battle1.setWinner(0, 100f);

        Battle battle2 = new Battle(
                environment.getColonyIdByName(colonies.get(0)),
                environment.getColonyIdByName(colonies.get(1)),
                ballsNutrient.getId(),
                colonies.get(0),
                colonies.get(1),
                ballsNutrient.toString());
        battle1.setWinner(0, 10f);

        Battle battle3 = new Battle(
                environment.getColonyIdByName(colonies.get(0)),
                environment.getColonyIdByName(colonies.get(2)),
                ballsNutrient.getId(),
                colonies.get(0),
                colonies.get(2),
                ballsNutrient.toString());
        battle1.setWinner(2, 150f);

        Battle battle4 = new Battle(
                environment.getColonyIdByName(colonies.get(0)),
                environment.getColonyIdByName(colonies.get(2)),
                famineFunction.getId(),
                colonies.get(0),
                colonies.get(2),
                famineFunction.getName());
        battle1.setWinner(0, 50f);

        contest.lastTournament().addResult(battle1);
        contest.lastTournament().addResult(battle2);
        contest.lastTournament().addResult(battle3);
        contest.lastTournament().addResult(battle4);


        contest.lastTournament().delete(colonies.get(2));

        Assert.assertTrue(contest.lastTournament().size() == 2);
        contest.lastTournament().load();

        List<String> names = contest.lastTournament().getNames();
        List<String> target = new ArrayList<>();
        target.add(colonies.get(0));
        target.add(colonies.get(1));

        Collections.sort(target);
        Collections.sort(names);

        Assert.assertEquals(2, names.size());

        Assert.assertEquals(names, target);
    }
}
