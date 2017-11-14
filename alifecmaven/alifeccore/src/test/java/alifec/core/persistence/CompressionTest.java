package alifec.core.persistence;

import alifec.ParentTest;
import alifec.core.compilation.CompilationResult;
import alifec.core.compilation.CompileHelper;
import alifec.core.contest.Contest;
import alifec.core.exception.*;
import alifec.core.persistence.config.ContestConfig;
import alifec.core.simulation.Competitor;
import alifec.core.simulation.Environment;
import alifec.core.simulation.nutrient.BallsNutrient;
import alifec.core.simulation.nutrient.FunctionBasedNutrient;
import alifec.core.simulation.nutrient.Nutrient;
import alifec.core.simulation.nutrient.function.*;
import org.junit.Assert;
import org.junit.Test;

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
Tactica2_java vs Tactica1_java in InclinedPlane
Original: 2682
Compressed: 1192
CompressTime: 275
Livetime: 299
Tactica2_java vs Tactica1_java in Famine
Original: 18657
Compressed: 0
CompressTime: 348
Livetime: 2074
Tactica2_java vs Tactica1_java in Lattice
Original: 2394
Compressed: 532
CompressTime: 513
Livetime: 267
Tactica2_java vs Tactica1_java in Rings
Original: 4824
Compressed: 0
CompressTime: 198
Livetime: 537
Tactica2_java vs Tactica1_java in TwoGaussians
Original: 14337
Compressed: 6372
CompressTime: 550
Livetime: 1594
Tactica2_java vs Tactica1_java in VerticalBar
Original: 10251
Compressed: 0
CompressTime: 203
Livetime: 1140
Tactica2_java vs Random_java in InclinedPlane
Original: 1224
Compressed: 544
CompressTime: 111
Livetime: 137
Tactica2_java vs Random_java in Famine
Original: 972
Compressed: 0
CompressTime: 11
Livetime: 109
Tactica2_java vs Random_java in Lattice
Original: 2502
Compressed: 556
CompressTime: 530
Livetime: 279
Tactica2_java vs Random_java in Rings
Original: 774
Compressed: 0
CompressTime: 31
Livetime: 87
Tactica2_java vs Random_java in TwoGaussians
Original: 1494
Compressed: 664
CompressTime: 61
Livetime: 167
Tactica2_java vs Random_java in VerticalBar
Original: 1314
Compressed: 0
CompressTime: 21
Livetime: 147
Tactica2_java vs MovX_java in InclinedPlane
Original: 1782
Compressed: 792
CompressTime: 162
Livetime: 199
Tactica2_java vs MovX_java in Famine
Original: 1350
Compressed: 0
CompressTime: 19
Livetime: 151
Tactica2_java vs MovX_java in Lattice
Original: 1782
Compressed: 396
CompressTime: 348
Livetime: 199
Tactica2_java vs MovX_java in Rings
Original: 2088
Compressed: 0
CompressTime: 85
Livetime: 233
Tactica2_java vs MovX_java in TwoGaussians
Original: 9477
Compressed: 4212
CompressTime: 403
Livetime: 1054
Tactica2_java vs MovX_java in VerticalBar
Original: 1737
Compressed: 0
CompressTime: 30
Livetime: 194
Tactica2_java vs Movx_cpp in InclinedPlane
Original: 2907
Compressed: 1292
CompressTime: 278
Livetime: 324
Tactica2_java vs Movx_cpp in Famine
Original: 1449
Compressed: 0
CompressTime: 31
Livetime: 162
Tactica2_java vs Movx_cpp in Lattice
Original: 1755
Compressed: 390
CompressTime: 385
Livetime: 196
Tactica2_java vs Movx_cpp in Rings
Original: 3465
Compressed: 0
CompressTime: 194
Livetime: 386
Tactica2_java vs Movx_cpp in TwoGaussians
Original: 2421
Compressed: 1076
CompressTime: 90
Livetime: 270
Tactica2_java vs Movx_cpp in VerticalBar
Original: 4392
Compressed: 0
CompressTime: 72
Livetime: 489
Tactica2_java vs Advanced_cpp in InclinedPlane
Original: 19017
Compressed: 8452
CompressTime: 1669
Livetime: 2114
Tactica2_java vs Advanced_cpp in Famine
Original: 18657
Compressed: 0
CompressTime: 311
Livetime: 2074
Tactica2_java vs Advanced_cpp in Lattice
Original: 6534
Compressed: 1452
CompressTime: 1254
Livetime: 727
Tactica2_java vs Advanced_cpp in Rings
Original: 6930
Compressed: 0
CompressTime: 288
Livetime: 771
Tactica2_java vs Advanced_cpp in TwoGaussians
Original: 1827
Compressed: 812
CompressTime: 68
Livetime: 204
Tactica2_java vs Advanced_cpp in VerticalBar
Original: 34056
Compressed: 0
CompressTime: 553
Livetime: 3785
Tactica1_java vs Random_java in InclinedPlane
Original: 1548
Compressed: 688
CompressTime: 143
Livetime: 173
Tactica1_java vs Random_java in Famine
Original: 981
Compressed: 0
CompressTime: 17
Livetime: 110
Tactica1_java vs Random_java in Lattice
Original: 1935
Compressed: 430
CompressTime: 388
Livetime: 216
Tactica1_java vs Random_java in Rings
Original: 1449
Compressed: 0
CompressTime: 65
Livetime: 162
Tactica1_java vs Random_java in TwoGaussians
Original: 1386
Compressed: 616
CompressTime: 51
Livetime: 155
Tactica1_java vs Random_java in VerticalBar
Original: 954
Compressed: 0
CompressTime: 19
Livetime: 107
Tactica1_java vs MovX_java in InclinedPlane
Original: 2916
Compressed: 1296
CompressTime: 267
Livetime: 325
Tactica1_java vs MovX_java in Famine
Original: 1350
Compressed: 0
CompressTime: 22
Livetime: 151
Tactica1_java vs MovX_java in Lattice
Original: 1746
Compressed: 388
CompressTime: 352
Livetime: 195
Tactica1_java vs MovX_java in Rings
Original: 2475
Compressed: 0
CompressTime: 110
Livetime: 276
Tactica1_java vs MovX_java in TwoGaussians
Original: 2097
Compressed: 932
CompressTime: 85
Livetime: 234
Tactica1_java vs MovX_java in VerticalBar
Original: 3537
Compressed: 0
CompressTime: 69
Livetime: 394
Tactica1_java vs Movx_cpp in InclinedPlane
Original: 1593
Compressed: 708
CompressTime: 136
Livetime: 178
Tactica1_java vs Movx_cpp in Famine
Original: 1458
Compressed: 0
CompressTime: 30
Livetime: 163
Tactica1_java vs Movx_cpp in Lattice
Original: 5535
Compressed: 1230
CompressTime: 1097
Livetime: 616
Tactica1_java vs Movx_cpp in Rings
Original: 3852
Compressed: 0
CompressTime: 166
Livetime: 429
Tactica1_java vs Movx_cpp in TwoGaussians
Original: 4365
Compressed: 1940
CompressTime: 318
Livetime: 486
Tactica1_java vs Movx_cpp in VerticalBar
Original: 2511
Compressed: 0
CompressTime: 40
Livetime: 280
Tactica1_java vs Advanced_cpp in InclinedPlane
Original: 2466
Compressed: 1096
CompressTime: 225
Livetime: 275
Tactica1_java vs Advanced_cpp in Famine
Original: 18657
Compressed: 0
CompressTime: 277
Livetime: 2074
Tactica1_java vs Advanced_cpp in Lattice
Original: 8307
Compressed: 1846
CompressTime: 1631
Livetime: 924
Tactica1_java vs Advanced_cpp in Rings
Original: 2232
Compressed: 0
CompressTime: 80
Livetime: 249
Tactica1_java vs Advanced_cpp in TwoGaussians
Original: 972
Compressed: 432
CompressTime: 41
Livetime: 109
Tactica1_java vs Advanced_cpp in VerticalBar
Original: 9351
Compressed: 0
CompressTime: 154
Livetime: 1040
Random_java vs MovX_java in InclinedPlane
Original: 2313
Compressed: 1028
CompressTime: 203
Livetime: 258
Random_java vs MovX_java in Famine
Original: 990
Compressed: 0
CompressTime: 15
Livetime: 111
Random_java vs MovX_java in Lattice
Original: 2421
Compressed: 538
CompressTime: 486
Livetime: 270
Random_java vs MovX_java in Rings
Original: 2736
Compressed: 0
CompressTime: 118
Livetime: 305
Random_java vs MovX_java in TwoGaussians
Original: 3987
Compressed: 1772
CompressTime: 151
Livetime: 444
Random_java vs MovX_java in VerticalBar
Original: 2214
Compressed: 0
CompressTime: 36
Livetime: 247
Random_java vs Movx_cpp in InclinedPlane
Original: 1206
Compressed: 536
CompressTime: 103
Livetime: 135
Random_java vs Movx_cpp in Famine
Original: 954
Compressed: 0
CompressTime: 15
Livetime: 107
Random_java vs Movx_cpp in Lattice
Original: 1683
Compressed: 374
CompressTime: 330
Livetime: 188
Random_java vs Movx_cpp in Rings
Original: 3600
Compressed: 0
CompressTime: 156
Livetime: 401
Random_java vs Movx_cpp in TwoGaussians
Original: 1701
Compressed: 756
CompressTime: 68
Livetime: 190
Random_java vs Movx_cpp in VerticalBar
Original: 3186
Compressed: 0
CompressTime: 49
Livetime: 355
Random_java vs Advanced_cpp in InclinedPlane
Original: 1152
Compressed: 512
CompressTime: 100
Livetime: 129
Random_java vs Advanced_cpp in Famine
Original: 1053
Compressed: 0
CompressTime: 16
Livetime: 118
Random_java vs Advanced_cpp in Lattice
Original: 585
Compressed: 130
CompressTime: 117
Livetime: 66
Random_java vs Advanced_cpp in Rings
Original: 792
Compressed: 0
CompressTime: 42
Livetime: 89
Random_java vs Advanced_cpp in TwoGaussians
Original: 1233
Compressed: 548
CompressTime: 54
Livetime: 138
Random_java vs Advanced_cpp in VerticalBar
Original: 1089
Compressed: 0
CompressTime: 18
Livetime: 122
MovX_java vs Movx_cpp in InclinedPlane
Original: 2331
Compressed: 1036
CompressTime: 254
Livetime: 260
MovX_java vs Movx_cpp in Famine
Original: 1386
Compressed: 0
CompressTime: 19
Livetime: 155
MovX_java vs Movx_cpp in Lattice
Original: 4878
Compressed: 1084
CompressTime: 953
Livetime: 543
MovX_java vs Movx_cpp in Rings
Original: 3807
Compressed: 0
CompressTime: 151
Livetime: 424
MovX_java vs Movx_cpp in TwoGaussians
Original: 5436
Compressed: 2416
CompressTime: 200
Livetime: 605
MovX_java vs Movx_cpp in VerticalBar
Original: 4716
Compressed: 0
CompressTime: 75
Livetime: 525
MovX_java vs Advanced_cpp in InclinedPlane
Original: 1224
Compressed: 544
CompressTime: 103
Livetime: 137
MovX_java vs Advanced_cpp in Famine
Original: 1368
Compressed: 0
CompressTime: 31
Livetime: 153
MovX_java vs Advanced_cpp in Lattice
Original: 1026
Compressed: 228
CompressTime: 189
Livetime: 115
MovX_java vs Advanced_cpp in Rings
Original: 828
Compressed: 0
CompressTime: 41
Livetime: 93
MovX_java vs Advanced_cpp in TwoGaussians
Original: 1359
Compressed: 604
CompressTime: 54
Livetime: 152
MovX_java vs Advanced_cpp in VerticalBar
Original: 1341
Compressed: 0
CompressTime: 23
Livetime: 150
Movx_cpp vs Advanced_cpp in InclinedPlane
Original: 1755
Compressed: 780
CompressTime: 157
Livetime: 196
Movx_cpp vs Advanced_cpp in Famine
Original: 1368
Compressed: 0
CompressTime: 26
Livetime: 153
Movx_cpp vs Advanced_cpp in Lattice
Original: 2538
Compressed: 564
CompressTime: 501
Livetime: 283
Movx_cpp vs Advanced_cpp in Rings
Original: 810
Compressed: 0
CompressTime: 36
Livetime: 91
Movx_cpp vs Advanced_cpp in TwoGaussians
Original: 1116
Compressed: 496
CompressTime: 36
Livetime: 125
Movx_cpp vs Advanced_cpp in VerticalBar
Original: 1665
Compressed: 0
CompressTime: 24
Livetime: 186

Final status:
Original: 338571
Compressed: 54282
CompressTime: 19825
Livetime: 37709

Process finished with exit code 0
*/
}
