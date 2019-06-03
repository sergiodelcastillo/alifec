package alifec.simulation.sumulation.test;

import alifec.core.contest.Battle;
import alifec.core.exception.BattleException;
import alifec.core.exception.ValidationException;
import alifec.simulation.simulation.ALifeContestSimulationView;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sergio Del Castillo on 09/07/18.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class Test extends Application {
    public static void main(String[] args) throws ValidationException, BattleException {

        Application.launch(Test.class, args);

        /*List<String> source = new ArrayList<>();
        source.add("bicho vs random in inclinedPlane");
        source.add("bicho vs random in lattice");
        source.add("bicho vs random in rings");
        source.add("Advanced vs random in rings");
        source.add("bicho vs advanced in TwoGaussians");

        Queue<String> queue = new LinkedList<>(source);
        while (!queue.isEmpty()){
            System.out.println(queue.poll());
        }*/

    }

    @Override
    public void start(Stage stage) throws Exception {
        List<Battle> list = new ArrayList<>();

        list.add(new Battle(1,  2, 1, "uno","dos", "nut"));

        new ALifeContestSimulationView(null, null).simulate(list);
    }
}
