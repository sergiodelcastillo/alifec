package alifec.core.persistence.custom;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * @author Sergio
 * @mail: sergio.jose.delcastillo@gmail.com
 * <p>
 * Lists Source code files.
 * It does not list known hidden or temporary files.
 */
public class SuffixPredicate implements Predicate<Path> {
    static Logger logger = LogManager.getLogger(SuffixPredicate.class);

    private final String[] suffixes;

    public SuffixPredicate(String[] array) {
        this.suffixes = array;
    }

    @Override
    public boolean test(Path path) {
        try {
            if (Files.isHidden(path)) return false;
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return false;
        }

        for (String suffix : suffixes) {
            if (path.getFileName().toString().endsWith(suffix)) return true;
        }

        return false;
    }
}
