package alifec.core.persistence;

import alifec.core.contest.Battle;
import alifec.core.simulation.Cell;
import alifec.core.simulation.nutrient.Nutrient;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.zip.Deflater;

/**
 * Created by Sergio Del Castillo on 01/11/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class SimulationFileManagerImpl1 implements SimulationFileManager {

    private Path file;


    public SimulationFileManagerImpl1(String path) throws IOException {
        file = Paths.get(path);
/*
        if(Files.notExists(file)){
            Files.createFile(file);
        }*/
    }

    @Override
    public void appendInit(Nutrient nutri, List<Cell> mos, Battle battle) throws IOException {

        StringBuilder battleBuilder = new StringBuilder();

        battleBuilder.append("b,")
                .append(battle.getFirstColonyId()).append(",")
                .append(battle.getFirstColony()).append(",")
                .append(battle.getSecondColonyId()).append(",")
                .append(battle.getSecondColony()).append(",")
                .append(battle.getNutrientId()).append(",")
                .append(battle.getNutrient()).append(System.lineSeparator());

        Files.write(file, battleBuilder.toString().getBytes(), StandardOpenOption.APPEND);

        append(nutri, mos);
    }

    private void saveCompressed(String code, byte[] nutrientsData) throws IOException {
        Deflater deflaterNutrients = new Deflater(Deflater.BEST_COMPRESSION);
        deflaterNutrients.setInput(nutrientsData);
        deflaterNutrients.finish();

        byte[] bufferNutrients = new byte[nutrientsData.length];

        Files.write(file, (code + ",").getBytes(), StandardOpenOption.APPEND);

        while (!deflaterNutrients.finished()) {
            deflaterNutrients.deflate(bufferNutrients);
            Files.write(file, bufferNutrients, StandardOpenOption.APPEND);
        }

        Files.write(file, System.lineSeparator().getBytes(), StandardOpenOption.APPEND);

    }

    @Override
    public void append(Nutrient nutri, List<Cell> mos) throws IOException {
        saveCompressed("n", toByteArray(nutri.getNutrients()));

        StringBuilder mosBuilder = new StringBuilder();
        for (Cell mo : mos) {
            mosBuilder.append(mo.x).append(',').append(mo.y).append(',').append(mo.ene).append(',');
        }

        saveCompressed("m", mosBuilder.toString().getBytes());

        Files.write(file, System.lineSeparator().getBytes(), StandardOpenOption.APPEND);
    }

    @Override
    public void appendFinish(Nutrient nutri, List<Cell> mos, Battle battle) throws IOException {
        StringBuilder battleBuilder = new StringBuilder();
        battleBuilder.append("e,")
                .append(battle.getWinnerId()).append(",")
                .append(battle.getWinnerName()).append(",")
                .append(battle.getWinnerEnergy()).append(System.lineSeparator());

        Files.write(file, battleBuilder.toString().getBytes(), StandardOpenOption.APPEND);
    }


    private byte[] toByteArray(float[][] values) {
        ByteBuffer buffer = ByteBuffer.allocate(+4 * values.length * values[0].length);

        for (float[] values1 : values) {
            for (float value : values1) {
                buffer.putFloat(value);
            }
        }

        return buffer.array();
    }

}
