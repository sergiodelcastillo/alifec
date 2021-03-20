package alifec;

import alifec.core.persistence.config.ContestConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by nacho on 08/11/17.
 */
public class Usage {

    private static final String USAGE_FILE = "usage.txt";
    private static final Logger logger = LogManager.getLogger(Usage.class);

    private final String usage;

    public Usage() {
        usage = loadUsage();
    }

    public void show() {
        logger.info(usage,
                ContestConfig.CONTEST_NAME_PREFIX,
                ContestConfig.CONTEST_NAME_PREFIX);
    }

    private String loadUsage() {
        try {
            InputStream usage = Util.class.getClassLoader().getResourceAsStream(USAGE_FILE);
            if (Objects.nonNull(usage)) {
                return read(usage);
            }
        } catch (Throwable t) {
            logger.error(t.getMessage(), t);
        }

        logger.error("Could not load file: " + USAGE_FILE);
        return null;
    }

    private String read(InputStream input) throws IOException {
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(input))) {
            return buffer.lines().collect(Collectors.joining("\n"));
        }
    }
}
