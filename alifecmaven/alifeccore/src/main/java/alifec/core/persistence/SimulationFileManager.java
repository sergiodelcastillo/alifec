package alifec.core.persistence;

import alifec.core.contest.Battle;
import alifec.core.persistence.config.ContestConfig;
import alifec.core.simulation.Cell;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Sergio Del Castillo on 01/11/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class SimulationFileManager {
    private Path file;

    public SimulationFileManager( String path) throws IOException {
        file = Paths.get(path);

        if(Files.notExists(file)){
            Files.createFile(file);
        }
    }

    public void appendFinish(Cell[][] env, Battle b) {
        //save the env
    }

    public void append(Cell[][] env) {
        //store only and update.
    }
    public void appendInit(Cell[][] env, Battle battle) {
        //store only and update.

    }


}
