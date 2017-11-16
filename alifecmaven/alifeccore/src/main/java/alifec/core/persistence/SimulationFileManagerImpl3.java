package alifec.core.persistence;

import alifec.core.contest.Battle;
import alifec.core.exception.ValidationException;
import alifec.core.persistence.dto.FinishedBattle;
import alifec.core.persistence.dto.RunningBattle;
import alifec.core.persistence.dto.StartBattle;
import alifec.core.simulation.Cell;
import alifec.core.simulation.Defs;
import alifec.core.simulation.nutrient.Nutrient;
import alifec.core.validation.LiveInstanceValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.Deflater;

/**
 * Created by Sergio Del Castillo on 01/11/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class SimulationFileManagerImpl3 implements SimulationFileManager {
    Logger logger = LogManager.getLogger(getClass());
    private final int BUFFER_SIZE = 1024;
    private Path file;
    private StringBuilder builder;
    private ByteBuffer byteBuffer;
    private float[][] nutrients;
    private byte[] buffer;
    private Deflater deflater;
    private final LiveInstanceValidator validator;

    public SimulationFileManagerImpl3(String path, boolean createFile) throws IOException {
        file = Paths.get(path);
        builder = new StringBuilder();
        byteBuffer = ByteBuffer.allocate(4 * Defs.DIAMETER * Defs.DIAMETER);
        nutrients = new float[Defs.DIAMETER][Defs.DIAMETER];

        buffer = new byte[BUFFER_SIZE];
        deflater = new Deflater(Deflater.BEST_COMPRESSION);

        if (createFile) {
            if (Files.notExists(file)) {
                Files.createFile(file);
            }
        }
        validator = new LiveInstanceValidator();
    }

    @Override
    public void appendInit(Nutrient nutri, List<Cell> mos, Battle battle) throws IOException {
        builder.delete(0, builder.length());

        builder.append("b,")
                .append(battle.getFirstColonyId()).append(",")
                .append(battle.getFirstColony()).append(",")
                .append(battle.getSecondColonyId()).append(",")
                .append(battle.getSecondColony()).append(",")
                .append(battle.getNutrientId()).append(",")
                .append(battle.getNutrient()).append(System.lineSeparator());

        Files.write(file, builder.toString().getBytes(), StandardOpenOption.APPEND);

        append(nutri, mos);
    }

    @Override
    public void append(Nutrient nutri, List<Cell> mos) throws IOException {
        saveNutrients(nutri);

        builder.delete(0, builder.length());

        for (Cell mo : mos) {
            builder.append(mo.x).append(',').append(mo.y).append(',').append(mo.colonyId).append(',').append(mo.ene).append(',');
        }

        saveMOs(builder.toString().getBytes());

        Files.write(file, System.lineSeparator().getBytes(), StandardOpenOption.APPEND);
    }

    private void saveNutrients(Nutrient nutri) throws IOException {

        float[][] newNutri = nutri.getNutrients();
        //calculate difference
        byteBuffer.clear();

        for (int x = 0; x < Defs.DIAMETER; x++) {
            for (int y = 0; y < Defs.DIAMETER; y++) {
                float diff = nutrients[x][y] - newNutri[x][y];

                if (diff < 0.01f && diff > -0.01f) {
                    diff = 0f;
                }

                byteBuffer.putFloat(diff);

                nutrients[x][y] = newNutri[x][y];
            }
        }

        Files.write(file, ("n," + nutri.getDx() + "," + nutri.getDy() + ",").getBytes(), StandardOpenOption.APPEND);

        saveCompressedData(byteBuffer.array());
    }

    private void saveMOs(byte[] data) throws IOException {
        Files.write(file, ("m,").getBytes(), StandardOpenOption.APPEND);

        saveCompressedData(data);
    }

    private void saveCompressedData(byte[] data) throws IOException {
        deflater.reset();
        deflater.setInput(data);
        deflater.finish();

        int characters;
        while (!deflater.finished()) {
            characters = deflater.deflate(buffer);
            if (characters < buffer.length) {
                Files.write(file, Arrays.copyOf(buffer, characters), StandardOpenOption.APPEND);
            } else {
                Files.write(file, buffer, StandardOpenOption.APPEND);
            }
        }

        Files.write(file, System.lineSeparator().getBytes(), StandardOpenOption.APPEND);
    }

    @Override
    public void appendFinish(Nutrient nutri, List<Cell> mos, Battle battle) throws IOException {
        builder.delete(0, builder.length());
        builder.append("e,")
                .append(battle.getWinnerId()).append(",")
                .append(battle.getWinnerName()).append(",")
                .append(battle.getWinnerEnergy()).append(System.lineSeparator());

        Files.write(file, builder.toString().getBytes(), StandardOpenOption.APPEND);
    }

    @Override
    public void iterateAll(Consumer consumer) throws IOException {

        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String line;

            while ((line = reader.readLine()) != null) {
                try {
                    validator.validate(line);

                    char firstCharacter = line.charAt(0);

                    switch (firstCharacter) {
                        case 'b':
                            consumer.consume(parseBattle(line));
                            break;
                        case 'n':
                            consumer.consume(parseRunning(line, reader.readLine()));
                            break;
                        case 'e':
                            consumer.consume(parseEnd(line));
                            break;
                    }
                } catch (ValidationException e) {
                    logger.warn(e.getMessage(), e);
                }
            }

        }
    }

    //@Override
    public void iterateAll2(Consumer consumer) throws IOException {

        try (InputStream is = Files.newInputStream(file)) {
            int buffer;
            ByteBuffer list = ByteBuffer.allocate(20000);

            while ((buffer = is.read()) != -1 && buffer != 10) {
                list.put((byte) buffer);
            }
            char firstCharacter = (char) list.get(0);
            System.out.println(firstCharacter);
        }


    }

    private StartBattle parseBattle(String line) {
        return null;
    }

    private RunningBattle parseRunning(String nutrients, String mos) {
        return null;
    }

    private FinishedBattle parseEnd(String line) {
        return null;
    }

}
