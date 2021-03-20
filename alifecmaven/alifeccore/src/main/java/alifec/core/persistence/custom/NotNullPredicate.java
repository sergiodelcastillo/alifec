package alifec.core.persistence.custom;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * Created by Sergio Del Castillo on 30/10/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class NotNullPredicate implements Predicate<Object> {
    @Override
    public boolean test(Object t) {
        return Objects.nonNull(t);
    }
}
