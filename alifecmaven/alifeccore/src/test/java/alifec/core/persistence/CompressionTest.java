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
INFO  23:45:58 Tournament:314 - Starting battle: Tactica2_java vs Tactica1_java in InclinedPlane
INFO  23:46:00 Tournament:326 - End of the battle. Winner Tactica2_java with energy 5292.766
INFO  23:46:00 Tournament:314 - Starting battle: Tactica2_java vs Tactica1_java in Famine
INFO  23:46:00 Tournament:326 - End of the battle. Winner Tactica2_java with energy 24959.33
INFO  23:46:00 Tournament:314 - Starting battle: Tactica2_java vs Tactica1_java in Lattice
INFO  23:46:02 Tournament:326 - End of the battle. Winner Tactica2_java with energy 20557.912
INFO  23:46:02 Tournament:314 - Starting battle: Tactica2_java vs Tactica1_java in Rings
INFO  23:46:05 Tournament:326 - End of the battle. Winner Tactica2_java with energy 31431.322
INFO  23:46:05 Tournament:314 - Starting battle: Tactica2_java vs Tactica1_java in TwoGaussians
INFO  23:46:06 Tournament:326 - End of the battle. Winner Tactica2_java with energy 19068.428
INFO  23:46:06 Tournament:314 - Starting battle: Tactica2_java vs Tactica1_java in VerticalBar
INFO  23:46:07 Tournament:326 - End of the battle. Winner Tactica2_java with energy 15882.818
INFO  23:46:07 Tournament:314 - Starting battle: Tactica2_java vs Random_java in InclinedPlane
INFO  23:46:07 Tournament:326 - End of the battle. Winner Tactica2_java with energy 20085.043
INFO  23:46:07 Tournament:314 - Starting battle: Tactica2_java vs Random_java in Famine
INFO  23:46:08 Tournament:326 - End of the battle. Winner Tactica2_java with energy 32310.229
INFO  23:46:08 Tournament:314 - Starting battle: Tactica2_java vs Random_java in Lattice
INFO  23:46:09 Tournament:326 - End of the battle. Winner Tactica2_java with energy 35454.914
INFO  23:46:09 Tournament:314 - Starting battle: Tactica2_java vs Random_java in Rings
INFO  23:46:10 Tournament:326 - End of the battle. Winner Tactica2_java with energy 49658.598
INFO  23:46:10 Tournament:314 - Starting battle: Tactica2_java vs Random_java in TwoGaussians
INFO  23:46:11 Tournament:326 - End of the battle. Winner Tactica2_java with energy 56291.08
INFO  23:46:11 Tournament:314 - Starting battle: Tactica2_java vs Random_java in VerticalBar
INFO  23:46:12 Tournament:326 - End of the battle. Winner Tactica2_java with energy 32264.574
INFO  23:46:12 Tournament:314 - Starting battle: Tactica2_java vs MovX_java in InclinedPlane
INFO  23:46:13 Tournament:326 - End of the battle. Winner Tactica2_java with energy 25593.516
INFO  23:46:13 Tournament:314 - Starting battle: Tactica2_java vs MovX_java in Famine
INFO  23:46:14 Tournament:326 - End of the battle. Winner Tactica2_java with energy 37233.902
INFO  23:46:14 Tournament:314 - Starting battle: Tactica2_java vs MovX_java in Lattice
INFO  23:46:19 Tournament:326 - End of the battle. Winner Tactica2_java with energy 33027.387
INFO  23:46:19 Tournament:314 - Starting battle: Tactica2_java vs MovX_java in Rings
INFO  23:46:20 Tournament:326 - End of the battle. Winner Tactica2_java with energy 73990.23
INFO  23:46:20 Tournament:314 - Starting battle: Tactica2_java vs MovX_java in TwoGaussians
INFO  23:46:22 Tournament:326 - End of the battle. Winner Tactica2_java with energy 39531.133
INFO  23:46:22 Tournament:314 - Starting battle: Tactica2_java vs MovX_java in VerticalBar
INFO  23:46:22 Tournament:326 - End of the battle. Winner Tactica2_java with energy 37447.16
INFO  23:46:22 Tournament:314 - Starting battle: Tactica2_java vs Movx_cpp in InclinedPlane
INFO  23:46:24 Tournament:326 - End of the battle. Winner Tactica2_java with energy 18555.94
INFO  23:46:24 Tournament:314 - Starting battle: Tactica2_java vs Movx_cpp in Famine
INFO  23:46:25 Tournament:326 - End of the battle. Winner Tactica2_java with energy 34131.582
INFO  23:46:25 Tournament:314 - Starting battle: Tactica2_java vs Movx_cpp in Lattice
INFO  23:46:27 Tournament:326 - End of the battle. Winner Tactica2_java with energy 64123.7
INFO  23:46:27 Tournament:314 - Starting battle: Tactica2_java vs Movx_cpp in Rings
INFO  23:46:29 Tournament:326 - End of the battle. Winner Tactica2_java with energy 62776.746
INFO  23:46:29 Tournament:314 - Starting battle: Tactica2_java vs Movx_cpp in TwoGaussians
INFO  23:46:30 Tournament:326 - End of the battle. Winner Tactica2_java with energy 8666.966
INFO  23:46:30 Tournament:314 - Starting battle: Tactica2_java vs Movx_cpp in VerticalBar
INFO  23:46:31 Tournament:326 - End of the battle. Winner Tactica2_java with energy 50103.484
INFO  23:46:31 Tournament:314 - Starting battle: Tactica2_java vs Advanced_cpp in InclinedPlane
INFO  23:47:25 Tournament:326 - End of the battle. Winner Advanced_cpp with energy 41898.395
INFO  23:47:25 Tournament:314 - Starting battle: Tactica2_java vs Advanced_cpp in Famine
INFO  23:48:47 Tournament:326 - End of the battle. Winner Tactica2_java with energy 2.360858
INFO  23:48:47 Tournament:314 - Starting battle: Tactica2_java vs Advanced_cpp in Lattice
INFO  23:49:20 Tournament:326 - End of the battle. Winner Advanced_cpp with energy 186994.45
INFO  23:49:20 Tournament:314 - Starting battle: Tactica2_java vs Advanced_cpp in Rings
INFO  23:49:34 Tournament:326 - End of the battle. Winner Advanced_cpp with energy 104806.57
INFO  23:49:34 Tournament:314 - Starting battle: Tactica2_java vs Advanced_cpp in TwoGaussians
INFO  23:49:49 Tournament:326 - End of the battle. Winner Advanced_cpp with energy 38469.78
INFO  23:49:49 Tournament:314 - Starting battle: Tactica2_java vs Advanced_cpp in VerticalBar
INFO  23:52:01 Tournament:326 - End of the battle. Winner Advanced_cpp with energy 60782.48
INFO  23:52:01 Tournament:314 - Starting battle: Tactica1_java vs Random_java in InclinedPlane
INFO  23:52:02 Tournament:326 - End of the battle. Winner Tactica1_java with energy 4136.691
INFO  23:52:02 Tournament:314 - Starting battle: Tactica1_java vs Random_java in Famine
INFO  23:52:03 Tournament:326 - End of the battle. Winner Tactica1_java with energy 3606.0347
INFO  23:52:03 Tournament:314 - Starting battle: Tactica1_java vs Random_java in Lattice
INFO  23:52:04 Tournament:326 - End of the battle. Winner Tactica1_java with energy 28254.867
INFO  23:52:04 Tournament:314 - Starting battle: Tactica1_java vs Random_java in Rings
INFO  23:52:05 Tournament:326 - End of the battle. Winner Tactica1_java with energy 32970.86
INFO  23:52:05 Tournament:314 - Starting battle: Tactica1_java vs Random_java in TwoGaussians
INFO  23:52:05 Tournament:326 - End of the battle. Winner Tactica1_java with energy 49616.15
INFO  23:52:05 Tournament:314 - Starting battle: Tactica1_java vs Random_java in VerticalBar
INFO  23:52:06 Tournament:326 - End of the battle. Winner Tactica1_java with energy 9128.402
INFO  23:52:06 Tournament:314 - Starting battle: Tactica1_java vs MovX_java in InclinedPlane
INFO  23:52:07 Tournament:326 - End of the battle. Winner Tactica1_java with energy 1465.7659
INFO  23:52:07 Tournament:314 - Starting battle: Tactica1_java vs MovX_java in Famine
INFO  23:52:08 Tournament:326 - End of the battle. Winner MovX_java with energy 31.401585
INFO  23:52:08 Tournament:314 - Starting battle: Tactica1_java vs MovX_java in Lattice
INFO  23:52:10 Tournament:326 - End of the battle. Winner Tactica1_java with energy 16013.973
INFO  23:52:10 Tournament:314 - Starting battle: Tactica1_java vs MovX_java in Rings
INFO  23:52:11 Tournament:326 - End of the battle. Winner Tactica1_java with energy 20339.174
INFO  23:52:11 Tournament:314 - Starting battle: Tactica1_java vs MovX_java in TwoGaussians
INFO  23:52:12 Tournament:326 - End of the battle. Winner Tactica1_java with energy 24120.744
INFO  23:52:12 Tournament:314 - Starting battle: Tactica1_java vs MovX_java in VerticalBar
INFO  23:52:13 Tournament:326 - End of the battle. Winner Tactica1_java with energy 16023.719
INFO  23:52:13 Tournament:314 - Starting battle: Tactica1_java vs Movx_cpp in InclinedPlane
INFO  23:52:14 Tournament:326 - End of the battle. Winner Tactica1_java with energy 847.0089
INFO  23:52:14 Tournament:314 - Starting battle: Tactica1_java vs Movx_cpp in Famine
INFO  23:52:15 Tournament:326 - End of the battle. Winner Tactica1_java with energy 75.67633
INFO  23:52:15 Tournament:314 - Starting battle: Tactica1_java vs Movx_cpp in Lattice
INFO  23:52:17 Tournament:326 - End of the battle. Winner Tactica1_java with energy 27366.047
INFO  23:52:17 Tournament:314 - Starting battle: Tactica1_java vs Movx_cpp in Rings
INFO  23:52:18 Tournament:326 - End of the battle. Winner Tactica1_java with energy 3362.3901
INFO  23:52:18 Tournament:314 - Starting battle: Tactica1_java vs Movx_cpp in TwoGaussians
INFO  23:52:20 Tournament:326 - End of the battle. Winner Tactica1_java with energy 11042.055
INFO  23:52:20 Tournament:314 - Starting battle: Tactica1_java vs Movx_cpp in VerticalBar
INFO  23:52:21 Tournament:326 - End of the battle. Winner Tactica1_java with energy 10265.368
INFO  23:52:21 Tournament:314 - Starting battle: Tactica1_java vs Advanced_cpp in InclinedPlane
INFO  23:52:58 Tournament:326 - End of the battle. Winner Advanced_cpp with energy 24028.234
INFO  23:52:58 Tournament:314 - Starting battle: Tactica1_java vs Advanced_cpp in Famine
INFO  23:53:08 Tournament:326 - End of the battle. Winner Advanced_cpp with energy 28261.557
INFO  23:53:08 Tournament:314 - Starting battle: Tactica1_java vs Advanced_cpp in Lattice
INFO  23:53:22 Tournament:326 - End of the battle. Winner Advanced_cpp with energy 53001.273
INFO  23:53:22 Tournament:314 - Starting battle: Tactica1_java vs Advanced_cpp in Rings
INFO  23:53:35 Tournament:326 - End of the battle. Winner Advanced_cpp with energy 122962.81
INFO  23:53:35 Tournament:314 - Starting battle: Tactica1_java vs Advanced_cpp in TwoGaussians
INFO  23:53:46 Tournament:326 - End of the battle. Winner Advanced_cpp with energy 41742.6
INFO  23:53:46 Tournament:314 - Starting battle: Tactica1_java vs Advanced_cpp in VerticalBar
INFO  23:54:02 Tournament:326 - End of the battle. Winner Advanced_cpp with energy 51620.332
INFO  23:54:02 Tournament:314 - Starting battle: Random_java vs MovX_java in InclinedPlane
INFO  23:54:03 Tournament:326 - End of the battle. Winner MovX_java with energy 2611.8755
INFO  23:54:03 Tournament:314 - Starting battle: Random_java vs MovX_java in Famine
INFO  23:54:04 Tournament:326 - End of the battle. Winner MovX_java with energy 4503.881
INFO  23:54:04 Tournament:314 - Starting battle: Random_java vs MovX_java in Lattice
INFO  23:54:05 Tournament:326 - End of the battle. Winner MovX_java with energy 2228.0933
INFO  23:54:05 Tournament:314 - Starting battle: Random_java vs MovX_java in Rings
INFO  23:54:06 Tournament:326 - End of the battle. Winner MovX_java with energy 3425.4524
INFO  23:54:06 Tournament:314 - Starting battle: Random_java vs MovX_java in TwoGaussians
INFO  23:54:09 Tournament:326 - End of the battle. Winner MovX_java with energy 1154.0668
INFO  23:54:09 Tournament:314 - Starting battle: Random_java vs MovX_java in VerticalBar
INFO  23:54:10 Tournament:326 - End of the battle. Winner MovX_java with energy 1838.4396
INFO  23:54:10 Tournament:314 - Starting battle: Random_java vs Movx_cpp in InclinedPlane
INFO  23:54:11 Tournament:326 - End of the battle. Winner Movx_cpp with energy 1077.1862
INFO  23:54:11 Tournament:314 - Starting battle: Random_java vs Movx_cpp in Famine
INFO  23:54:11 Tournament:326 - End of the battle. Winner Movx_cpp with energy 4355.7285
INFO  23:54:11 Tournament:314 - Starting battle: Random_java vs Movx_cpp in Lattice
INFO  23:54:13 Tournament:326 - End of the battle. Winner Movx_cpp with energy 6759.886
INFO  23:54:13 Tournament:314 - Starting battle: Random_java vs Movx_cpp in Rings
INFO  23:54:14 Tournament:326 - End of the battle. Winner Random_java with energy 979.5536
INFO  23:54:14 Tournament:314 - Starting battle: Random_java vs Movx_cpp in TwoGaussians
INFO  23:54:15 Tournament:326 - End of the battle. Winner Random_java with energy 2005.2328
INFO  23:54:15 Tournament:314 - Starting battle: Random_java vs Movx_cpp in VerticalBar
INFO  23:54:16 Tournament:326 - End of the battle. Winner Movx_cpp with energy 251.29095
INFO  23:54:16 Tournament:314 - Starting battle: Random_java vs Advanced_cpp in InclinedPlane
INFO  23:54:28 Tournament:326 - End of the battle. Winner Advanced_cpp with energy 50401.797
INFO  23:54:28 Tournament:314 - Starting battle: Random_java vs Advanced_cpp in Famine
INFO  23:54:40 Tournament:326 - End of the battle. Winner Advanced_cpp with energy 33862.695
INFO  23:54:40 Tournament:314 - Starting battle: Random_java vs Advanced_cpp in Lattice
INFO  23:54:46 Tournament:326 - End of the battle. Winner Advanced_cpp with energy 78133.53
INFO  23:54:46 Tournament:314 - Starting battle: Random_java vs Advanced_cpp in Rings
INFO  23:54:51 Tournament:326 - End of the battle. Winner Advanced_cpp with energy 64929.93
INFO  23:54:51 Tournament:314 - Starting battle: Random_java vs Advanced_cpp in TwoGaussians
INFO  23:54:57 Tournament:326 - End of the battle. Winner Advanced_cpp with energy 132841.3
INFO  23:54:57 Tournament:314 - Starting battle: Random_java vs Advanced_cpp in VerticalBar
INFO  23:55:11 Tournament:326 - End of the battle. Winner Advanced_cpp with energy 56911.76
INFO  23:55:11 Tournament:314 - Starting battle: MovX_java vs Movx_cpp in InclinedPlane
INFO  23:55:12 Tournament:326 - End of the battle. Winner Movx_cpp with energy 570.92694
INFO  23:55:12 Tournament:314 - Starting battle: MovX_java vs Movx_cpp in Famine
INFO  23:55:13 Tournament:326 - End of the battle. Winner Movx_cpp with energy 42.521606
INFO  23:55:13 Tournament:314 - Starting battle: MovX_java vs Movx_cpp in Lattice
INFO  23:55:15 Tournament:326 - End of the battle. Winner Movx_cpp with energy 1261.1187
INFO  23:55:15 Tournament:314 - Starting battle: MovX_java vs Movx_cpp in Rings
INFO  23:55:16 Tournament:326 - End of the battle. Winner MovX_java with energy 695.0035
INFO  23:55:16 Tournament:314 - Starting battle: MovX_java vs Movx_cpp in TwoGaussians
INFO  23:55:18 Tournament:326 - End of the battle. Winner MovX_java with energy 1245.5217
INFO  23:55:18 Tournament:314 - Starting battle: MovX_java vs Movx_cpp in VerticalBar
INFO  23:55:19 Tournament:326 - End of the battle. Winner MovX_java with energy 98.14563
INFO  23:55:19 Tournament:314 - Starting battle: MovX_java vs Advanced_cpp in InclinedPlane
INFO  23:55:42 Tournament:326 - End of the battle. Winner Advanced_cpp with energy 43541.258
INFO  23:55:42 Tournament:314 - Starting battle: MovX_java vs Advanced_cpp in Famine
INFO  23:55:59 Tournament:326 - End of the battle. Winner Advanced_cpp with energy 36247.676
INFO  23:55:59 Tournament:314 - Starting battle: MovX_java vs Advanced_cpp in Lattice
INFO  23:56:08 Tournament:326 - End of the battle. Winner Advanced_cpp with energy 80417.99
INFO  23:56:08 Tournament:314 - Starting battle: MovX_java vs Advanced_cpp in Rings
INFO  23:56:21 Tournament:326 - End of the battle. Winner Advanced_cpp with energy 112758.78
INFO  23:56:21 Tournament:314 - Starting battle: MovX_java vs Advanced_cpp in TwoGaussians
INFO  23:56:28 Tournament:326 - End of the battle. Winner Advanced_cpp with energy 101336.15
INFO  23:56:28 Tournament:314 - Starting battle: MovX_java vs Advanced_cpp in VerticalBar
INFO  23:56:45 Tournament:326 - End of the battle. Winner Advanced_cpp with energy 49396.887
INFO  23:56:45 Tournament:314 - Starting battle: Movx_cpp vs Advanced_cpp in InclinedPlane
INFO  23:57:04 Tournament:326 - End of the battle. Winner Advanced_cpp with energy 36073.02
INFO  23:57:04 Tournament:314 - Starting battle: Movx_cpp vs Advanced_cpp in Famine
INFO  23:57:20 Tournament:326 - End of the battle. Winner Advanced_cpp with energy 34824.99
INFO  23:57:20 Tournament:314 - Starting battle: Movx_cpp vs Advanced_cpp in Lattice
INFO  23:57:26 Tournament:326 - End of the battle. Winner Advanced_cpp with energy 44947.3
INFO  23:57:26 Tournament:314 - Starting battle: Movx_cpp vs Advanced_cpp in Rings
INFO  23:57:33 Tournament:326 - End of the battle. Winner Advanced_cpp with energy 81105.26
INFO  23:57:33 Tournament:314 - Starting battle: Movx_cpp vs Advanced_cpp in TwoGaussians
INFO  23:57:41 Tournament:326 - End of the battle. Winner Advanced_cpp with energy 144089.72
INFO  23:57:41 Tournament:314 - Starting battle: Movx_cpp vs Advanced_cpp in VerticalBar
INFO  23:57:56 Tournament:326 - End of the battle. Winner Advanced_cpp with energy 48299.633
Final status:
size impl1: 101501k
size impl2: 101103k
size impl3: 20653k --> todo: run using base64(deflater(BestCompress)) .. con base64 usa un 60% más
size impl4: 14849k
size impl5: 11805k
size impl6: 15967k
Livetime = 26445

* */
}
