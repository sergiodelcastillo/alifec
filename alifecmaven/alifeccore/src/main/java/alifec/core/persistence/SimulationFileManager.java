package alifec.core.persistence;

import alifec.core.contest.Battle;
import alifec.core.persistence.dto.LiveInstance;
import alifec.core.simulation.Cell;
import alifec.core.simulation.nutrient.Nutrient;

import java.io.IOException;
import java.util.List;

/**
 * Created by Sergio Del Castillo on 12/11/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public interface SimulationFileManager {
    interface Consumer {
        void consume(LiveInstance line);
    }

    void appendInit(Nutrient nutri, List<Cell> mos, Battle battle) throws IOException;

    void append(Nutrient nutri, List<Cell> mos) throws IOException;

    void appendFinish(Nutrient nutri, List<Cell> mos, Battle battle) throws IOException;

    void iterateAll(Consumer consumer);
}
