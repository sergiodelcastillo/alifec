package alifec.core.persistence.custom;

import alifec.core.persistence.config.ContestConfig;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Predicate;

public class TournamentPredicate implements Predicate<Path> {

    @Override
    public boolean test(Path path) {
        return path.getFileName().toString().startsWith(ContestConfig.TOURNAMENT_PREFIX) &&
                Files.isDirectory(path);
    }
}

