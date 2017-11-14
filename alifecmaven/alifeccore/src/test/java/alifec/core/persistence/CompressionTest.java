package alifec.core.persistence;

import alifec.ParentTest;
import alifec.core.compilation.CompilationResult;
import alifec.core.compilation.CompileHelper;
import alifec.core.contest.Contest;
import alifec.core.contest.Tournament;
import alifec.core.exception.*;
import alifec.core.persistence.config.ContestConfig;
import alifec.core.simulation.*;
import alifec.core.simulation.nutrient.BallsNutrient;
import alifec.core.simulation.nutrient.FunctionBasedNutrient;
import alifec.core.simulation.nutrient.Nutrient;
import alifec.core.simulation.nutrient.function.*;
import com.sun.org.apache.xalan.internal.xsltc.cmdline.Compile;
import org.junit.Assert;
import org.junit.Test;
import sun.management.resources.agent;
import sun.rmi.runtime.Log;
import sun.tools.jar.resources.jar;
import sun.util.logging.resources.logging;

import javax.annotation.Generated;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by Sergio Del Castillo on 05/11/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class CompressionTest extends ParentTest {

    class CompressResult {
        private int liveTime;
        int compressedSize;
        int originalSize;
        long compressTime;

        public CompressResult(int compressedSize, int originalSize) {
            this.compressedSize = compressedSize;
            this.originalSize = originalSize;
            this.compressTime = 0;
            this.liveTime = 0;
        }
    }

    public Nutrient[] getNutrients() {
        return new Nutrient[]{
                //todo: ver porque no anda con el balls
                new BallsNutrient(),
                new FunctionBasedNutrient(new InclinedPlaneFunction()),
                new FunctionBasedNutrient(new FamineFunction()),
                new FunctionBasedNutrient(new LatticeFunction()),
                new FunctionBasedNutrient(new RingsFunction()),
                new FunctionBasedNutrient(new TwoGaussiansFunction()),
                new FunctionBasedNutrient(new VerticalBarFunction())
        };
    }


    @Test
    public void test2() throws URISyntaxException, ConfigFileException, CreateContestFolderException, IOException, BattleException, MoveMicroorganismException, CreateContestException {
        //create the contest and the folder structure
        ContestConfig config = createContest("Contest-01");
        config.setMode(ContestConfig.COMPETITION_MODE);
        CompileHelper compileHelper = new CompileHelper(config);
        //compile MOs
        CompilationResult result = compileHelper.compileMOs();
        Assert.assertFalse(result.haveErrors());

        //create the environment
        Contest contest = new Contest(config);
        Environment environment = contest.getEnvironment();

        //create a battle: 0= first colony, 1= second colony, famine= uniform nutrient distribution
        List<Competitor> competitors = environment.getCompetitors();
        Nutrient[] nutrients = getNutrients();

        CompressResult finalStats = new CompressResult(0, 0);
        //TODO: check the limit...
        int numberOfBattles = 1000;

        for (int i = 0, nBattles = 0; i < competitors.size() && nBattles < numberOfBattles; i++) {
            for (int j = i + 1; j < competitors.size() && nBattles < numberOfBattles; j++) {
                for (int index = 0; index < nutrients.length && nBattles++ < numberOfBattles; index++) {
                    Nutrient n = nutrients[index];
                    if(!(n instanceof FunctionBasedNutrient) ) continue;
                    createBattle(environment, i, j, n.getId(), n.getName());

                    while (!environment.moveColonies()) {
/*                        Nutrient nutri = environment.getNutrient();
                        CompressResult tmp = testCompress1(nutri);
                        battleStats.originalSize += tmp.originalSize;
                        battleStats.compressedSize += tmp.compressedSize;
                        battleStats.compressTime += tmp.compressTime;*/
                    }

                    finalStats.liveTime += environment.getLiveTime();

                }
            }
        }
        long size = Files.size(Paths.get(config.getSimulationRunFile(contest.lastTournament().getName())));
        long size2 = Files.size(Paths.get(config.getSimulationRunFile(contest.lastTournament().getName()) + "2"));
        long size3 = Files.size(Paths.get(config.getSimulationRunFile(contest.lastTournament().getName()) + "3"));
        long size4 = Files.size(Paths.get(config.getSimulationRunFile(contest.lastTournament().getName()) + "4"));
        long size5 = Files.size(Paths.get(config.getSimulationRunFile(contest.lastTournament().getName()) + "5"));
        long size6 = Files.size(Paths.get(config.getSimulationRunFile(contest.lastTournament().getName()) + "6"));
        System.out.println("Final status:");
        System.out.println("size impl1: " + size / 1024 + "k");
        System.out.println("size impl2: " + size2 / 1024 + "k");
        System.out.println("size impl3: " + size3 / 1024 + "k");
        System.out.println("size impl4: " + size4 / 1024 + "k");
        System.out.println("size impl5: " + size5 / 1024 + "k");
        System.out.println("size impl6: " + size6 / 1024 + "k");

        System.out.println("Livetime = " + finalStats.liveTime);

    }

/*

    INFO  23:32:26 Tournament:314 - Starting battle: Tactica2_java vs Tactica1_java in InclinedPlane
    INFO  23:32:27 Tournament:326 - End of the battle. Winner Tactica2_java with energy 8082.642
    INFO  23:32:27 Tournament:314 - Starting battle: Tactica2_java vs Tactica1_java in Famine
    INFO  23:32:28 Tournament:326 - End of the battle. Winner Tactica2_java with energy 27106.373
    INFO  23:32:28 Tournament:314 - Starting battle: Tactica2_java vs Tactica1_java in Lattice
    INFO  23:32:31 Tournament:326 - End of the battle. Winner Tactica2_java with energy 15998.866
    INFO  23:32:31 Tournament:314 - Starting battle: Tactica2_java vs Tactica1_java in Rings
    INFO  23:32:33 Tournament:326 - End of the battle. Winner Tactica2_java with energy 37479.88
    INFO  23:32:33 Tournament:314 - Starting battle: Tactica2_java vs Tactica1_java in TwoGaussians
    INFO  23:32:34 Tournament:326 - End of the battle. Winner Tactica2_java with energy 16650.23
    INFO  23:32:34 Tournament:314 - Starting battle: Tactica2_java vs Tactica1_java in VerticalBar
    INFO  23:32:36 Tournament:326 - End of the battle. Winner Tactica2_java with energy 38345.53
    INFO  23:32:36 Tournament:314 - Starting battle: Tactica2_java vs Random_java in InclinedPlane
    INFO  23:32:36 Tournament:326 - End of the battle. Winner Tactica2_java with energy 13440.906
    INFO  23:32:36 Tournament:314 - Starting battle: Tactica2_java vs Random_java in Famine
    INFO  23:32:37 Tournament:326 - End of the battle. Winner Tactica2_java with energy 34762.2
    INFO  23:32:37 Tournament:314 - Starting battle: Tactica2_java vs Random_java in Lattice
    INFO  23:32:38 Tournament:326 - End of the battle. Winner Tactica2_java with energy 56356.97
    INFO  23:32:38 Tournament:314 - Starting battle: Tactica2_java vs Random_java in Rings
    INFO  23:32:39 Tournament:326 - End of the battle. Winner Tactica2_java with energy 49335.066
    INFO  23:32:39 Tournament:314 - Starting battle: Tactica2_java vs Random_java in TwoGaussians
    INFO  23:32:40 Tournament:326 - End of the battle. Winner Tactica2_java with energy 23027.105
    INFO  23:32:40 Tournament:314 - Starting battle: Tactica2_java vs Random_java in VerticalBar
    INFO  23:32:41 Tournament:326 - End of the battle. Winner Tactica2_java with energy 31196.01
    INFO  23:32:41 Tournament:314 - Starting battle: Tactica2_java vs MovX_java in InclinedPlane
    INFO  23:32:42 Tournament:326 - End of the battle. Winner Tactica2_java with energy 19898.738
    INFO  23:32:42 Tournament:314 - Starting battle: Tactica2_java vs MovX_java in Famine
    INFO  23:32:43 Tournament:326 - End of the battle. Winner Tactica2_java with energy 36351.58
    INFO  23:32:43 Tournament:314 - Starting battle: Tactica2_java vs MovX_java in Lattice
    INFO  23:32:45 Tournament:326 - End of the battle. Winner Tactica2_java with energy 42410.074
    INFO  23:32:45 Tournament:314 - Starting battle: Tactica2_java vs MovX_java in Rings
    INFO  23:32:46 Tournament:326 - End of the battle. Winner Tactica2_java with energy 71970.13
    INFO  23:32:46 Tournament:314 - Starting battle: Tactica2_java vs MovX_java in TwoGaussians
    INFO  23:32:48 Tournament:326 - End of the battle. Winner Tactica2_java with energy 32574.086
    INFO  23:32:48 Tournament:314 - Starting battle: Tactica2_java vs MovX_java in VerticalBar
    INFO  23:32:49 Tournament:326 - End of the battle. Winner Tactica2_java with energy 25998.455
    INFO  23:32:49 Tournament:314 - Starting battle: Tactica2_java vs Movx_cpp in InclinedPlane
    INFO  23:32:50 Tournament:326 - End of the battle. Winner Tactica2_java with energy 8325.403
    INFO  23:32:50 Tournament:314 - Starting battle: Tactica2_java vs Movx_cpp in Famine
    INFO  23:32:51 Tournament:326 - End of the battle. Winner Tactica2_java with energy 34650.96
    INFO  23:32:51 Tournament:314 - Starting battle: Tactica2_java vs Movx_cpp in Lattice
    INFO  23:32:52 Tournament:326 - End of the battle. Winner Tactica2_java with energy 58570.176
    INFO  23:32:52 Tournament:314 - Starting battle: Tactica2_java vs Movx_cpp in Rings
    INFO  23:32:53 Tournament:326 - End of the battle. Winner Tactica2_java with energy 75383.19
    INFO  23:32:53 Tournament:314 - Starting battle: Tactica2_java vs Movx_cpp in TwoGaussians
    INFO  23:32:54 Tournament:326 - End of the battle. Winner Tactica2_java with energy 17327.652
    INFO  23:32:54 Tournament:314 - Starting battle: Tactica2_java vs Movx_cpp in VerticalBar
    INFO  23:32:56 Tournament:326 - End of the battle. Winner Tactica2_java with energy 63860.844
    INFO  23:32:56 Tournament:314 - Starting battle: Tactica2_java vs Advanced_cpp in InclinedPlane
    INFO  23:34:01 Tournament:326 - End of the battle. Winner Advanced_cpp with energy 20315.451
    INFO  23:34:01 Tournament:314 - Starting battle: Tactica2_java vs Advanced_cpp in Famine
    INFO  23:35:30 Tournament:326 - End of the battle. Winner Advanced_cpp with energy 273.93213
    INFO  23:35:30 Tournament:314 - Starting battle: Tactica2_java vs Advanced_cpp in Lattice
    INFO  23:35:54 Tournament:326 - End of the battle. Winner Advanced_cpp with energy 98562.67
    INFO  23:35:54 Tournament:314 - Starting battle: Tactica2_java vs Advanced_cpp in Rings
    INFO  23:36:22 Tournament:326 - End of the battle. Winner Advanced_cpp with energy 217096.62
    INFO  23:36:22 Tournament:314 - Starting battle: Tactica2_java vs Advanced_cpp in TwoGaussians
    INFO  23:36:36 Tournament:326 - End of the battle. Winner Advanced_cpp with energy 95712.34
    INFO  23:36:36 Tournament:314 - Starting battle: Tactica2_java vs Advanced_cpp in VerticalBar
    INFO  23:39:27 Tournament:326 - End of the battle. Winner Advanced_cpp with energy 36837.8
    INFO  23:39:27 Tournament:314 - Starting battle: Tactica1_java vs Random_java in InclinedPlane
    INFO  23:39:27 Tournament:326 - End of the battle. Winner Tactica1_java with energy 2563.106
    INFO  23:39:27 Tournament:314 - Starting battle: Tactica1_java vs Random_java in Famine
    INFO  23:39:28 Tournament:326 - End of the battle. Winner Tactica1_java with energy 2875.8826
    INFO  23:39:28 Tournament:314 - Starting battle: Tactica1_java vs Random_java in Lattice
    INFO  23:39:29 Tournament:326 - End of the battle. Winner Tactica1_java with energy 56963.86
    INFO  23:39:29 Tournament:314 - Starting battle: Tactica1_java vs Random_java in Rings
    INFO  23:39:30 Tournament:326 - End of the battle. Winner Tactica1_java with energy 46358.742
    INFO  23:39:30 Tournament:314 - Starting battle: Tactica1_java vs Random_java in TwoGaussians
    INFO  23:39:30 Tournament:326 - End of the battle. Winner Tactica1_java with energy 37415.168
    INFO  23:39:30 Tournament:314 - Starting battle: Tactica1_java vs Random_java in VerticalBar
    INFO  23:39:31 Tournament:326 - End of the battle. Winner Tactica1_java with energy 6573.6313
    INFO  23:39:31 Tournament:314 - Starting battle: Tactica1_java vs MovX_java in InclinedPlane
    INFO  23:39:32 Tournament:326 - End of the battle. Winner Tactica1_java with energy 108.03615
    INFO  23:39:32 Tournament:314 - Starting battle: Tactica1_java vs MovX_java in Famine
    INFO  23:39:33 Tournament:326 - End of the battle. Winner Tactica1_java with energy 22.889606
    INFO  23:39:33 Tournament:314 - Starting battle: Tactica1_java vs MovX_java in Lattice
    INFO  23:39:34 Tournament:326 - End of the battle. Winner Tactica1_java with energy 16470.904
    INFO  23:39:34 Tournament:314 - Starting battle: Tactica1_java vs MovX_java in Rings
    INFO  23:39:35 Tournament:326 - End of the battle. Winner Tactica1_java with energy 18502.049
    INFO  23:39:35 Tournament:314 - Starting battle: Tactica1_java vs MovX_java in TwoGaussians
    INFO  23:39:39 Tournament:326 - End of the battle. Winner Tactica1_java with energy 19975.469
    INFO  23:39:39 Tournament:314 - Starting battle: Tactica1_java vs MovX_java in VerticalBar
    INFO  23:39:40 Tournament:326 - End of the battle. Winner Tactica1_java with energy 8797.497
    INFO  23:39:40 Tournament:314 - Starting battle: Tactica1_java vs Movx_cpp in InclinedPlane
    INFO  23:39:41 Tournament:326 - End of the battle. Winner Tactica1_java with energy 1311.37
    INFO  23:39:41 Tournament:314 - Starting battle: Tactica1_java vs Movx_cpp in Famine
    INFO  23:39:42 Tournament:326 - End of the battle. Winner Tactica1_java with energy 239.72731
    INFO  23:39:42 Tournament:314 - Starting battle: Tactica1_java vs Movx_cpp in Lattice
    INFO  23:39:45 Tournament:326 - End of the battle. Winner Tactica1_java with energy 17967.215
    INFO  23:39:45 Tournament:314 - Starting battle: Tactica1_java vs Movx_cpp in Rings
    INFO  23:39:46 Tournament:326 - End of the battle. Winner Tactica1_java with energy 17010.117
    INFO  23:39:46 Tournament:314 - Starting battle: Tactica1_java vs Movx_cpp in TwoGaussians
    INFO  23:39:48 Tournament:326 - End of the battle. Winner Tactica1_java with energy 16919.686
    INFO  23:39:48 Tournament:314 - Starting battle: Tactica1_java vs Movx_cpp in VerticalBar
    INFO  23:39:49 Tournament:326 - End of the battle. Winner Tactica1_java with energy 1616.6693
    INFO  23:39:49 Tournament:314 - Starting battle: Tactica1_java vs Advanced_cpp in InclinedPlane
    INFO  23:40:09 Tournament:326 - End of the battle. Winner Advanced_cpp with energy 38718.28
    INFO  23:40:09 Tournament:314 - Starting battle: Tactica1_java vs Advanced_cpp in Famine
    INFO  23:40:22 Tournament:326 - End of the battle. Winner Advanced_cpp with energy 27886.12
    INFO  23:40:22 Tournament:314 - Starting battle: Tactica1_java vs Advanced_cpp in Lattice
    INFO  23:40:36 Tournament:326 - End of the battle. Winner Advanced_cpp with energy 93072.09
    INFO  23:40:36 Tournament:314 - Starting battle: Tactica1_java vs Advanced_cpp in Rings
    INFO  23:40:48 Tournament:326 - End of the battle. Winner Advanced_cpp with energy 128960.57
    INFO  23:40:48 Tournament:314 - Starting battle: Tactica1_java vs Advanced_cpp in TwoGaussians
    INFO  23:40:54 Tournament:326 - End of the battle. Winner Advanced_cpp with energy 55498.312
    INFO  23:40:54 Tournament:314 - Starting battle: Tactica1_java vs Advanced_cpp in VerticalBar
    INFO  23:41:11 Tournament:326 - End of the battle. Winner Advanced_cpp with energy 49936.99
    INFO  23:41:11 Tournament:314 - Starting battle: Random_java vs MovX_java in InclinedPlane
    INFO  23:41:12 Tournament:326 - End of the battle. Winner MovX_java with energy 2352.4736
    INFO  23:41:12 Tournament:314 - Starting battle: Random_java vs MovX_java in Famine
    INFO  23:41:13 Tournament:326 - End of the battle. Winner MovX_java with energy 4744.7505
    INFO  23:41:13 Tournament:314 - Starting battle: Random_java vs MovX_java in Lattice
    INFO  23:41:14 Tournament:326 - End of the battle. Winner MovX_java with energy 1661.9164
    INFO  23:41:14 Tournament:314 - Starting battle: Random_java vs MovX_java in Rings
    INFO  23:41:16 Tournament:326 - End of the battle. Winner MovX_java with energy 1431.0448
    INFO  23:41:16 Tournament:314 - Starting battle: Random_java vs MovX_java in TwoGaussians
    INFO  23:41:16 Tournament:326 - End of the battle. Winner MovX_java with energy 12846.63
    INFO  23:41:16 Tournament:314 - Starting battle: Random_java vs MovX_java in VerticalBar
    INFO  23:41:17 Tournament:326 - End of the battle. Winner MovX_java with energy 3836.0183
    INFO  23:41:17 Tournament:314 - Starting battle: Random_java vs Movx_cpp in InclinedPlane
    INFO  23:41:18 Tournament:326 - End of the battle. Winner Movx_cpp with energy 7326.7275
    INFO  23:41:18 Tournament:314 - Starting battle: Random_java vs Movx_cpp in Famine
    INFO  23:41:18 Tournament:326 - End of the battle. Winner Movx_cpp with energy 4737.748
    INFO  23:41:18 Tournament:314 - Starting battle: Random_java vs Movx_cpp in Lattice
    INFO  23:41:21 Tournament:326 - End of the battle. Winner Random_java with energy 730.6261
    INFO  23:41:21 Tournament:314 - Starting battle: Random_java vs Movx_cpp in Rings
    INFO  23:41:21 Tournament:326 - End of the battle. Winner Random_java with energy 2350.1729
    INFO  23:41:21 Tournament:314 - Starting battle: Random_java vs Movx_cpp in TwoGaussians
    INFO  23:41:23 Tournament:326 - End of the battle. Winner Movx_cpp with energy 5817.504
    INFO  23:41:23 Tournament:314 - Starting battle: Random_java vs Movx_cpp in VerticalBar
    INFO  23:41:24 Tournament:326 - End of the battle. Winner Movx_cpp with energy 3587.4912
    INFO  23:41:24 Tournament:314 - Starting battle: Random_java vs Advanced_cpp in InclinedPlane
    INFO  23:41:35 Tournament:326 - End of the battle. Winner Advanced_cpp with energy 35702.3
    INFO  23:41:35 Tournament:314 - Starting battle: Random_java vs Advanced_cpp in Famine
    INFO  23:41:48 Tournament:326 - End of the battle. Winner Advanced_cpp with energy 36099.504
    INFO  23:41:48 Tournament:314 - Starting battle: Random_java vs Advanced_cpp in Lattice
    INFO  23:41:55 Tournament:326 - End of the battle. Winner Advanced_cpp with energy 73247.8
    INFO  23:41:55 Tournament:314 - Starting battle: Random_java vs Advanced_cpp in Rings
    INFO  23:42:00 Tournament:326 - End of the battle. Winner Advanced_cpp with energy 74136.39
    INFO  23:42:00 Tournament:314 - Starting battle: Random_java vs Advanced_cpp in TwoGaussians
    INFO  23:42:06 Tournament:326 - End of the battle. Winner Advanced_cpp with energy 103464.53
    INFO  23:42:06 Tournament:314 - Starting battle: Random_java vs Advanced_cpp in VerticalBar
    INFO  23:42:13 Tournament:326 - End of the battle. Winner Advanced_cpp with energy 35194.21
    INFO  23:42:13 Tournament:314 - Starting battle: MovX_java vs Movx_cpp in InclinedPlane
    INFO  23:42:14 Tournament:326 - End of the battle. Winner MovX_java with energy 772.5632
    INFO  23:42:14 Tournament:314 - Starting battle: MovX_java vs Movx_cpp in Famine
    INFO  23:42:15 Tournament:326 - End of the battle. Winner Movx_cpp with energy 52.861515
    INFO  23:42:15 Tournament:314 - Starting battle: MovX_java vs Movx_cpp in Lattice
    INFO  23:42:16 Tournament:326 - End of the battle. Winner MovX_java with energy 9.044891
    INFO  23:42:16 Tournament:314 - Starting battle: MovX_java vs Movx_cpp in Rings
    INFO  23:42:18 Tournament:326 - End of the battle. Winner Movx_cpp with energy 824.41235
    INFO  23:42:18 Tournament:314 - Starting battle: MovX_java vs Movx_cpp in TwoGaussians
    INFO  23:42:20 Tournament:326 - End of the battle. Winner Movx_cpp with energy 2897.3472
    INFO  23:42:20 Tournament:314 - Starting battle: MovX_java vs Movx_cpp in VerticalBar
    INFO  23:42:21 Tournament:326 - End of the battle. Winner Movx_cpp with energy 56.709473
    INFO  23:42:21 Tournament:314 - Starting battle: MovX_java vs Advanced_cpp in InclinedPlane
    INFO  23:42:43 Tournament:326 - End of the battle. Winner Advanced_cpp with energy 36258.805
    INFO  23:42:43 Tournament:314 - Starting battle: MovX_java vs Advanced_cpp in Famine
    INFO  23:43:00 Tournament:326 - End of the battle. Winner Advanced_cpp with energy 37568.887
    INFO  23:43:00 Tournament:314 - Starting battle: MovX_java vs Advanced_cpp in Lattice
    INFO  23:43:10 Tournament:326 - End of the battle. Winner Advanced_cpp with energy 48729.605
    INFO  23:43:10 Tournament:314 - Starting battle: MovX_java vs Advanced_cpp in Rings
    INFO  23:43:16 Tournament:326 - End of the battle. Winner Advanced_cpp with energy 96370.625
    INFO  23:43:16 Tournament:314 - Starting battle: MovX_java vs Advanced_cpp in TwoGaussians
    INFO  23:43:27 Tournament:326 - End of the battle. Winner Advanced_cpp with energy 148106.06
    INFO  23:43:27 Tournament:314 - Starting battle: MovX_java vs Advanced_cpp in VerticalBar
    INFO  23:43:46 Tournament:326 - End of the battle. Winner Advanced_cpp with energy 53537.965
    INFO  23:43:46 Tournament:314 - Starting battle: Movx_cpp vs Advanced_cpp in InclinedPlane
    INFO  23:44:12 Tournament:326 - End of the battle. Winner Advanced_cpp with energy 39780.953
    INFO  23:44:12 Tournament:314 - Starting battle: Movx_cpp vs Advanced_cpp in Famine
    INFO  23:44:30 Tournament:326 - End of the battle. Winner Advanced_cpp with energy 36047.03
    INFO  23:44:30 Tournament:314 - Starting battle: Movx_cpp vs Advanced_cpp in Lattice
    INFO  23:44:37 Tournament:326 - End of the battle. Winner Advanced_cpp with energy 50430.555
    INFO  23:44:37 Tournament:314 - Starting battle: Movx_cpp vs Advanced_cpp in Rings
    INFO  23:44:43 Tournament:326 - End of the battle. Winner Advanced_cpp with energy 71529.56
    INFO  23:44:43 Tournament:314 - Starting battle: Movx_cpp vs Advanced_cpp in TwoGaussians
    INFO  23:44:49 Tournament:326 - End of the battle. Winner Advanced_cpp with energy 114334.695
    INFO  23:44:49 Tournament:314 - Starting battle: Movx_cpp vs Advanced_cpp in VerticalBar
    INFO  23:45:14 Tournament:326 - End of the battle. Winner Advanced_cpp with energy 71261.0
    Final status:
    size impl1: 96750k
    size impl2: 96366k
    size impl3: 12469k --> todo: esta es la implementación más sencilla y parece la más razonable
    size impl4: 14194k
    size impl5: 11748k
    size impl6: 15946k
    Livetime = 26405
*/
}
