package alifec.core.persistence;

import alifec.core.contest.Battle;
import alifec.core.contest.ParentTest;
import alifec.core.exception.BattleException;
import alifec.core.persistence.dto.FinishedBattle;
import alifec.core.persistence.dto.RunningBattle;
import alifec.core.persistence.dto.StartBattle;
import alifec.core.simulation.Cell;
import alifec.core.simulation.Microorganism;
import alifec.core.simulation.TestMicroorganism;
import alifec.core.simulation.nutrient.Nutrient;
import alifec.core.simulation.nutrient.TestNutrient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;


/**
 * Created by Sergio Del Castillo on 14/11/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class SimulationFileManagerImpl3Test extends ParentTest {

    private Map<Integer, Microorganism> mos;
    private List<Cell> cells;

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

    @Test
    public void testCompressStartBattle() throws BattleException, IOException {
        Nutrient nutrient = initializeNutrient();
        initializeMos();

        Battle battle = new Battle(1, 2, nutrient.getId(),
                mos.get(1).getName(), mos.get(2).getName(), nutrient.getName());

        SimulationFileManager fileManager = new SimulationFileManagerImpl3(TEST_ROOT_PATH + File.separator + "battle3.run", true);

        fileManager.appendInit(nutrient, cells, battle);

        fileManager.iterateAll(new SimulationFileManager.Consumer() {
            @Override
            public void consume(StartBattle line) {
                Assertions.assertEquals(1, line.getFirstColonyId());
                Assertions.assertEquals(mos.get(1).getName(), line.getFirstColony());
                Assertions.assertEquals(2, line.getSecondColonyId());
                Assertions.assertEquals(mos.get(2).getName(), line.getSecondColony());
            }

            @Override
            public void consume(RunningBattle line) {
                Assertions.fail("should not be called");
            }

            @Override
            public void consume(FinishedBattle line) {
                Assertions.fail("should not be called");
            }
        });
    }

    private Nutrient initializeNutrient() {
        Nutrient nutrient = new TestNutrient();
        nutrient.init();
        return nutrient;
    }

    private void initializeMos() {
        mos = new HashMap<>();
        cells = new ArrayList<>();
        mos.put(1, createMo(1));
        mos.put(2, createMo(2));
        cells.add(createCell(1));
        cells.add(createCell(2));
    }

    private Microorganism createMo(int id) {
        return new TestMicroorganism("mo_" + id);
    }

    private Cell createCell(int id) {
        Cell mo1 = new Cell(id);
        mo1.x = id;
        mo1.y = -id;
        mo1.ene = id * 100f;
        return mo1;
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
            byte[] uncompressed = Base64.getDecoder().decode(Arrays.copyOf(list.array(), list.position()));
            Inflater inflater = new Inflater();
            inflater.setInput(uncompressed);
            byte[] result = new byte[10000];
            int size = inflater.inflate(result, 0, result.length);
            inflater.end();
            byte[] rfinal = Arrays.copyOf(result, size);
            char firstCharacter = (char) rfinal[0];
            System.out.println(firstCharacter);

        } catch (DataFormatException e) {
            e.printStackTrace();
        }
    }
}
