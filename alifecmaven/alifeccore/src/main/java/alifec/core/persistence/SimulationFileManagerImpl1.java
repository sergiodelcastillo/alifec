package alifec.core.persistence;

import alifec.core.contest.Battle;
import alifec.core.simulation.Cell;
import alifec.core.simulation.Defs;
import alifec.core.simulation.nutrient.Nutrient;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.zip.Deflater;

/**
 * Created by Sergio Del Castillo on 01/11/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class SimulationFileManagerImpl1 implements SimulationFileManager {

    private Path file;
    private StringBuilder builder;
    private ByteBuffer byteBuffer;

    public SimulationFileManagerImpl1(String path, boolean createFile) throws IOException {
        file = Paths.get(path);
        builder = new StringBuilder();
        byteBuffer = ByteBuffer.allocate(4 * Defs.DIAMETER * Defs.DIAMETER);

        if (createFile) {
            if (Files.notExists(file)) {
                Files.createFile(file);
            }
        }
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

    private void saveCompressed(String code, byte[] nutrientsData) throws IOException {
        Deflater deflaterNutrients = new Deflater(Deflater.BEST_COMPRESSION);
        deflaterNutrients.setInput(nutrientsData);
        deflaterNutrients.finish();

        byte[] buffer = new byte[1024];

        Files.write(file, (code + ",").getBytes(), StandardOpenOption.APPEND);

        int seek;
        while (!deflaterNutrients.finished()) {
            seek = deflaterNutrients.deflate(buffer);
            if (seek < buffer.length) {
                Files.write(file, Arrays.copyOf(buffer, seek), StandardOpenOption.APPEND);
            } else {
                Files.write(file, buffer, StandardOpenOption.APPEND);
            }
        }


        Files.write(file, System.lineSeparator().getBytes(), StandardOpenOption.APPEND);

    }

    @Override
    public void append(Nutrient nutri, List<Cell> mos) throws IOException {
        saveCompressed("n", toByteArray(nutri.getNutrients()));
        builder.delete(0, builder.length());

        for (Cell mo : mos) {
            builder.append(mo.x).append(',').append(mo.y).append(',').append(mo.id).append(',').append(mo.ene).append(',');
        }

        saveCompressed("m", builder.toString().getBytes());

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


    private byte[] toByteArray(float[][] values) {
        byteBuffer.clear();

        for (float[] values1 : values) {
            for (float value : values1) {
                byteBuffer.putFloat(value);
            }
        }

        return byteBuffer.array();
    }

}
