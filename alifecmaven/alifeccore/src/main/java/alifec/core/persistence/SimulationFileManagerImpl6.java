package alifec.core.persistence;

import alifec.core.contest.Battle;
import alifec.core.simulation.Cell;
import alifec.core.simulation.Defs;
import alifec.core.simulation.nutrient.Nutrient;

import java.io.IOException;
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
public class SimulationFileManagerImpl6 implements SimulationFileManager {

    private Path file;
    private StringBuilder builder;

    private float[][] nutrients;
    //  private short[][] difference;
    //private List<Float> list;

    public SimulationFileManagerImpl6(String path, boolean createFile) throws IOException {
        //first improvement: save only one number to represent mo position: x, y = x*50+y. it saves about 6 or 7 %
        file = Paths.get(path);
        builder = new StringBuilder();
        nutrients = new float[Defs.DIAMETER][Defs.DIAMETER];
        // difference = new short[Defs.DIAMETER][Defs.DIAMETER];
        // list = new ArrayList<>(Defs.DIAMETER * Defs.DIAMETER);

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

    private void saveCompressed(String code, byte[] data) throws IOException {
        Deflater deflaterNutrients = new Deflater(Deflater.BEST_COMPRESSION);
        deflaterNutrients.setInput(data);
        deflaterNutrients.finish();

        byte[] buffer = new byte[1024];

        Files.write(file, (code + ",").getBytes(), StandardOpenOption.APPEND);

        if (data.length == 0) return;

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
        builder.delete(0, builder.length());
        calculateDiff(nutri);
        saveCompressed("n", builder.toString().getBytes());

        builder.delete(0, builder.length());


        for (Cell mo : mos) {
            //builder.append(mo.x).append(',').append(mo.y).append(',').append(mo.id).append(',').append(mo.ene).append(',');
            builder.append(mo.x * Defs.DIAMETER + mo.y).append(',').append(mo.colonyId).append(',').append(mo.ene).append(',');
        }

        saveCompressed("m", builder.toString().getBytes());

        Files.write(file, System.lineSeparator().getBytes(), StandardOpenOption.APPEND);
    }

    private void calculateDiff(Nutrient nutri) {
        float[][] ntmp = nutri.getNutrients();
        //  int count = 0;
        for (int x = 0; x < Defs.DIAMETER; x++) {
            for (int y = 0; y < Defs.DIAMETER; y++) {
                float diff = nutrients[x][y] - ntmp[x][y];

                if (diff > 0.001f || diff < -0.001f) {
                    builder.append(x).append(',').append(y).append(',').append(diff).append(',');
                    //                count++;
                }
                nutrients[x][y] = ntmp[x][y];
            }
        }
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
    public void iterateAll(Consumer consumer) {

    }


}
