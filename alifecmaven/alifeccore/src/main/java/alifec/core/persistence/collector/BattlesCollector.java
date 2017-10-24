package alifec.core.persistence.collector;

import alifec.core.contest.Battle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * Created by Sergio Del Castillo on 23/10/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class BattlesCollector implements Collector<String, List<Battle>, List<Battle>> {
    Logger logger = LogManager.getLogger(BattlesCollector.class);

    @Override
    public Supplier<List<Battle>> supplier() {
        return ArrayList::new;
    }

    @Override
    public BiConsumer<List<Battle>, String> accumulator() {
        return (list, line) -> {
            try {
                list.add(new Battle(line));
            } catch (Throwable ex) {
                logger.warn("Can not load battle line: " + line);
                logger.warn(ex.getMessage(), ex);
            }
        };
    }

    @Override
    public BinaryOperator<List<Battle>> combiner() {
        return (list1, list2) -> {
            list1.addAll(list2);
            return list1;
        };
    }

    @Override
    public Function<List<Battle>, List<Battle>> finisher() {
        return battles -> battles;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.IDENTITY_FINISH));
    }
}
