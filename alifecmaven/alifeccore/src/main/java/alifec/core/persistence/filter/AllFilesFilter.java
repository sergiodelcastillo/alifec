package alifec.core.persistence.filter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Predicate;

/**
 * Created by Sergio Del Castillo on 16/08/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class AllFilesFilter implements Predicate<Path> {
    static Logger logger = LogManager.getLogger(AllFilesFilter.class);

    @Override
    public boolean test(Path path) {
        try {
            return !Files.isHidden(path) && !path.getFileName().toString().endsWith("~");
        } catch (IOException e) {
            logger.warn(e.getMessage(), e);
            return false;
        }
    }
}
