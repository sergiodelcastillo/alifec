package alifec.core.persistence.custom;

import alifec.core.contest.Battle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Function;

/**
 * Created by Sergio Del Castillo on 28/10/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class BattlesFunction implements Function<String, Battle> {
    Logger logger = LogManager.getLogger(getClass());

    @Override
    public Battle apply(String path) {
        try {
            return new Battle(path);
        } catch (Throwable t) {
            logger.warn(t.getMessage());
        }
        return null;
    }
}
