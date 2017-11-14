package alifec.core.persistence;

import alifec.core.contest.Battle;
import alifec.core.simulation.Cell;
import alifec.core.simulation.nutrient.Nutrient;

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

    public SimulationFileManagerList(String path, boolean createFile) throws IOException {
        list = new ArrayList<>();
        //todo: add dx,dy to persistence
        list.add(new SimulationFileManagerImpl1(path, createFile));
       list.add(new SimulationFileManagerImpl2(path + "2", createFile));
        list.add(new SimulationFileManagerImpl3(path + "3", createFile));
        list.add(new SimulationFileManagerImpl4(path + "4", createFile));
        list.add(new SimulationFileManagerImpl5(path + "5", createFile));
        list.add(new SimulationFileManagerImpl6(path + "6", createFile));
      /*
        */
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
