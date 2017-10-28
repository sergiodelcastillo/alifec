package alifec.core.persistence.custom;

import alifec.core.contest.oponentInfo.OpponentInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Function;

/**
 * Created by Sergio Del Castillo on 28/10/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class OpponentFunction implements Function<String, OpponentInfo> {
    Logger logger = LogManager.getLogger(getClass());

    @Override
    public OpponentInfo apply(String s) {
        return new OpponentInfo(s);
    }
}
