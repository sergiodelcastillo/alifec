package alifec.core.persistence;

import alifec.core.contest.Battle;
import alifec.core.persistence.dto.FinishedBattle;
import alifec.core.persistence.dto.RunningBattle;
import alifec.core.persistence.dto.StartBattle;
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
    void appendInit(Nutrient nutri, List<Cell> mos, Battle battle) throws IOException;

    void append(Nutrient nutri, List<Cell> mos) throws IOException;

    void appendFinish(Nutrient nutri, List<Cell> mos, Battle battle) throws IOException;

    void iterateAll(Consumer consumer) throws IOException;

    interface Consumer {
        void consume(StartBattle line);

        void consume(RunningBattle line);

        void consume(FinishedBattle line);
    }
}
