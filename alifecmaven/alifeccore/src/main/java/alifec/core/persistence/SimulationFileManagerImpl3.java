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
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.zip.Deflater;

/**
 * Created by Sergio Del Castillo on 01/11/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class SimulationFileManagerImpl3 implements SimulationFileManager {
    private static final String FIELD_SEPARATOR = ",";
    private static final String OBJECT_SEPARATOR = ";";
    private static final String LINE_SEPARATOR = "\n";
    private static final String BATTLE_PREFIX = "b";
    private static final String NUTRIENT_PREFIX = "n";
    private static final String MICROORGANISM_PREFIX = "m";
    private static final String END_PREFIX = "e";

    private static final byte[] FIELD_SEPARATOR_BYTES = FIELD_SEPARATOR.getBytes();
    private static final byte[] OBJECT_SEPARATOR_BYTES = OBJECT_SEPARATOR.getBytes();
    private static final byte[] LINE_SEPARATOR_BYTES = LINE_SEPARATOR.getBytes();
    private static final byte[] BATTLE_PREFIX_BYTES = BATTLE_PREFIX.getBytes();
    private static final byte[] NUTRIENT_PREFIX_BYTES = NUTRIENT_PREFIX.getBytes();
    private static final byte[] MICROORGANISM_PREFIX_BYTES = MICROORGANISM_PREFIX.getBytes();
    private static final byte[] END_PREFIX_BYTES = END_PREFIX.getBytes();

    Logger logger = LogManager.getLogger(getClass());
    // assumed that the data size will never be higher than buffer size. The compressed data should be shorter than
    //the size of the original size 4*50*50.
    private final int BUFFER_SIZE = 4 * Defs.DIAMETER * Defs.DIAMETER;
    private Path file;
    private StringBuilder builder;
    private ByteBuffer byteBuffer;
    private float[][] nutrients;
    private byte[] buffer;
    private Deflater deflater;
    private final LiveInstanceValidator validator;
    private Base64.Encoder encoder;
    private Base64.Decoder decoder;


    public SimulationFileManagerImpl3(String path, boolean createFile) throws IOException {
        file = Paths.get(path);
        builder = new StringBuilder();
        byteBuffer = ByteBuffer.allocate(BUFFER_SIZE);
        nutrients = new float[Defs.DIAMETER][Defs.DIAMETER];
        buffer = new byte[BUFFER_SIZE];
        deflater = new Deflater(Deflater.BEST_COMPRESSION);
        encoder = Base64.getEncoder();
        decoder = Base64.getDecoder();

        if (createFile) {
            if (Files.notExists(file)) {
                Files.createFile(file);
            }
        }
        validator = new LiveInstanceValidator();
    }

    @Override
    public void appendInit(Nutrient nutri, List<Cell> mos, Battle battle) throws IOException {
        saveBattle(battle);
        saveNutrients(nutri);
        saveMOs(mos);

        Files.write(file, LINE_SEPARATOR_BYTES, StandardOpenOption.APPEND);
    }

    @Override
    public void append(Nutrient nutri, List<Cell> mos) throws IOException {
        saveNutrients(nutri);

        saveMOs(mos);

        Files.write(file, LINE_SEPARATOR_BYTES, StandardOpenOption.APPEND);
    }

    private void saveBattle(Battle battle) throws IOException {
        builder.delete(0, builder.length());
        builder.append(FIELD_SEPARATOR).append(battle.getFirstColonyId())
                .append(FIELD_SEPARATOR).append(battle.getFirstColony())
                .append(FIELD_SEPARATOR).append(battle.getSecondColonyId())
                .append(FIELD_SEPARATOR).append(battle.getSecondColony())
                .append(FIELD_SEPARATOR).append(battle.getNutrientId())
                .append(FIELD_SEPARATOR).append(battle.getNutrient())
                .append(OBJECT_SEPARATOR);

        Files.write(file, BATTLE_PREFIX_BYTES, StandardOpenOption.APPEND);
        Files.write(file, builder.toString().getBytes(), StandardOpenOption.APPEND);
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
        builder.delete(0, builder.length());
        builder.append(NUTRIENT_PREFIX)
                .append(nutri.getDx())
                .append(FIELD_SEPARATOR)
                .append(nutri.getDy())
                .append(FIELD_SEPARATOR);
        Files.write(file, builder.toString().getBytes(), StandardOpenOption.APPEND);

        saveCompressedData(byteBuffer.array());
        Files.write(file, OBJECT_SEPARATOR_BYTES, StandardOpenOption.APPEND);
    }

    private void saveMOs(List<Cell> mos) throws IOException {
        builder.delete(0, builder.length());

        for (Cell mo : mos) {
            builder.append(mo.x)
                    .append(FIELD_SEPARATOR)
                    .append(mo.y)
                    .append(FIELD_SEPARATOR)
                    .append(mo.colonyId)
                    .append(FIELD_SEPARATOR)
                    .append(mo.ene)
                    .append(OBJECT_SEPARATOR);
        }

        Files.write(file, MICROORGANISM_PREFIX_BYTES, StandardOpenOption.APPEND);

        saveCompressedData(builder.toString().getBytes());
    }

    private void saveCompressedData(byte[] data) throws IOException {
        deflater.reset();
        deflater.setInput(data);
        deflater.finish();

        int characters;
        while (!deflater.finished()) {
            characters = deflater.deflate(buffer);

            if (characters < buffer.length) {
                saveCompressedDataToFile(Arrays.copyOf(buffer, characters));
            } else {
                //todo: create a object that ensure the compressed list of byte.
                System.out.println("ERROR!!!!!!!!!!");
                saveCompressedDataToFile(buffer);
            }
        }
    }

    private void saveCompressedDataToFile(byte[] data) throws IOException {
        Files.write(file, encoder.encode(data), StandardOpenOption.APPEND);
    }

    @Override
    public void appendFinish(Nutrient nutri, List<Cell> mos, Battle battle) throws IOException {
        builder.delete(0, builder.length());
        builder.append(END_PREFIX)
                .append(battle.getWinnerId()).append(FIELD_SEPARATOR)
                .append(battle.getWinnerName()).append(FIELD_SEPARATOR)
                .append(battle.getWinnerEnergy()).append(LINE_SEPARATOR);

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
        String[] tokens = line.split(FIELD_SEPARATOR);
        return new StartBattle(
                Integer.parseInt(tokens[1]),
                tokens[2],
                Integer.parseInt(tokens[3]),
                tokens[4],
                Integer.parseInt(tokens[5]),
                tokens[6]
        );
    }

    private RunningBattle parseRunning(String nutrients, String mos) {
        return null;
    }

    private FinishedBattle parseEnd(String line) {
        return null;
    }

}
