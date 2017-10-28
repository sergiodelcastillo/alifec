package alifec.core.persistence.custom;

import alifec.core.contest.Battle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.function.Predicate;


/**
 * Created by Sergio Del Castillo on 23/10/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class ExcludeBattlesPredicate implements Predicate<String> {
    private Logger logger = LogManager.getLogger(ExcludeBattlesPredicate.class);
    private final List<Battle> battles;

    public ExcludeBattlesPredicate(List<Battle> battles) {
        this.battles = battles;
    }

    @Override
    public boolean test(String s) {
        try {
            return !battles.contains(new Battle(s));
        } catch (Throwable ex) {
            logger.error(ex.getMessage(), ex);
            return false;
        }
    }
}
