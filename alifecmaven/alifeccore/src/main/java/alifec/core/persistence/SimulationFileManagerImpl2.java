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
public class SimulationFileManagerImpl1 implements ISimulationFileManager {

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
        //todo: complete it
        Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);
        byte[] data = toByteArray(nutri.getNutrients());

        deflater.setInput(data);
        deflater.finish();

        byte[] buffer = new byte[data.length];

        Files.write(file, "n,".getBytes(), StandardOpenOption.APPEND);

        while (!deflater.finished()) {
            deflater.deflate(buffer);
            Files.write(file, buffer, StandardOpenOption.APPEND);
        }

        Files.write(file, "\n".getBytes(), StandardOpenOption.APPEND);
    }

    @Override
    public void append(Nutrient nutri, List<Cell> mos) {
        //store only and update.
    }
    @Override
    public void appendFinish(Nutrient nutri, List<Cell> mos, Battle b) {
        //save the env
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
