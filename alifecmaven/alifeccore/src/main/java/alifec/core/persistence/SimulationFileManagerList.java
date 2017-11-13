package alifec.core.persistence;

import alifec.core.contest.Battle;
import alifec.core.simulation.Cell;
import alifec.core.simulation.nutrient.Nutrient;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sergio Del Castillo on 12/11/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class SimulationFileManagerList implements SimulationFileManager {

    private List<SimulationFileManager> list;

    public SimulationFileManagerList(String path) throws IOException {
        list = new ArrayList<>();
        list.add(new SimulationFileManagerImpl1(path));
        list.add(new SimulationFileManagerImpl2(path + "2"));
    }

    @Override
    public void appendInit(Nutrient nutri, List<Cell> mos, Battle battle) throws IOException {
        for (SimulationFileManager sfm : list) {
            sfm.appendInit(nutri, mos, battle);
        }
    }

    @Override
    public void append(Nutrient nutri, List<Cell> mos) throws IOException {
        for (SimulationFileManager sfm : list) {
            sfm.append(nutri, mos);
        }
    }

    @Override
    public void appendFinish(Nutrient nutri, List<Cell> mos, Battle battle) throws IOException {
        for (SimulationFileManager sfm : list) {
            sfm.appendInit(nutri, mos, battle);
        }
    }
}
