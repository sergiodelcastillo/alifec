package alifec.core.persistence;

import alifec.ParentTest;
import alifec.core.contest.Battle;
import alifec.core.exception.BattleException;
import alifec.core.persistence.dto.FinishedBattle;
import alifec.core.persistence.dto.RunningBattle;
import alifec.core.persistence.dto.StartBattle;
import alifec.core.simulation.Cell;
import alifec.core.simulation.nutrient.Nutrient;
import alifec.core.simulation.nutrient.TestNutrient;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * Created by Sergio Del Castillo on 14/11/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class SimulationFileManagerImpl3Test extends ParentTest {

    @Test
    public void testCompressStartBattle() throws BattleException, IOException {
        Nutrient nutrient = initializeNutrient();
        List<Cell> mos = initializeMos();

        Battle battle = new Battle(1, 2, nutrient.getId(), "mo1", "mo2", nutrient.getName());

        SimulationFileManager fileManager = new SimulationFileManagerImpl3(TEST_ROOT_PATH + File.separator + "battle3.run", true);

        fileManager.appendInit(nutrient, mos, battle);

        fileManager.iterateAll(new SimulationFileManager.Consumer() {
            @Override
            public void consume(StartBattle line) {
                System.out.println(line);
            }

            @Override
            public void consume(RunningBattle line) {
                Assert.fail("should not be called");
            }

            @Override
            public void consume(FinishedBattle line) {
                Assert.fail("should not be called");
            }
        });
    }

    private Nutrient initializeNutrient() {
        Nutrient nutrient = new TestNutrient();
        nutrient.init();
        return nutrient;
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
        Cell mo3 = new Cell(2);
        mo3.x = 30;
        mo3.y = -30;
        mo3.ene = 900f;
        return Arrays.asList(mo1, mo2, mo3);
    }

    @Test
    public void testFiles() throws IOException {
        Path file = Paths.get(TEST_ROOT_PATH + File.separator + "battle3.run");
        try (OutputStream os = Files.newOutputStream(file)) {
            String battle1 = "b,1,name1,2,name2,200,TestNutri";
            Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);

            byte[] sbytes = battle1.getBytes(StandardCharsets.UTF_8);
            deflater.setInput(sbytes);
            deflater.finish();
            byte[] buffer = new byte[sbytes.length];
            int n = deflater.deflate(buffer);
            byte[] bufferFinal = Arrays.copyOf(buffer, n);

            os.write(Base64.getEncoder().encode(bufferFinal));
            os.write('\n');
        }


        try (InputStream is = Files.newInputStream(file)) {
            int buffer;
            ByteBuffer list = ByteBuffer.allocate(20000);

            while ((buffer = is.read()) != -1 && buffer != 10) {
                list.put((byte) buffer);
            }
            byte [] uncompressed = Base64.getDecoder().decode(Arrays.copyOf(list.array(), list.position()));
            Inflater inflater = new Inflater();
            inflater.setInput(uncompressed);
            byte [] result = new byte[10000];
            int size = inflater.inflate(result, 0, result.length);
            inflater.end();
            byte [] rfinal = Arrays.copyOf(result, size);
            char firstCharacter  = (char) rfinal[0];
            System.out.println(firstCharacter);

        } catch (DataFormatException e) {
            e.printStackTrace();
        }


    }

    public static String compress(String s) {
        Deflater def = new Deflater(Deflater.BEST_COMPRESSION);
        byte[] sbytes = s.getBytes(StandardCharsets.UTF_8);
        def.setInput(sbytes);
        def.finish();
        byte[] buffer = new byte[sbytes.length];
        int n = def.deflate(buffer);
        return new String(buffer, 0, n, StandardCharsets.ISO_8859_1)
                + "*" + sbytes.length;
    }

    public static String decompress(String s) {
        int pos = s.lastIndexOf('*');
        int len = Integer.parseInt(s.substring(pos + 1));
        s = s.substring(0, pos);

        Inflater inf = new Inflater();
        byte[] buffer = s.getBytes(StandardCharsets.ISO_8859_1);
        byte[] decomp = new byte[len];
        inf.setInput(buffer);
        try {
            inf.inflate(decomp, 0, len);
            inf.end();
        } catch (DataFormatException e) {
            throw new IllegalArgumentException(e);
        }
        return new String(decomp, StandardCharsets.UTF_8);
    }
}