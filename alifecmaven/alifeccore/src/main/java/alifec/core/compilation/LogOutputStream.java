package alifec.core.compilation;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.io.LoggerOutputStream;
import org.apache.logging.log4j.spi.ExtendedLogger;

/**
 * Created by nacho on 27/08/17.
 */
public class LogOutputStream extends LoggerOutputStream {

    public LogOutputStream(Logger logger, Level level) {
        super((ExtendedLogger) logger, level, null, null, null);
    }

}
