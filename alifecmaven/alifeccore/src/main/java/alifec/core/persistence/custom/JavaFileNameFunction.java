package alifec.core.persistence.custom;

import java.nio.file.Path;
import java.util.function.Function;

/**
 * Created by Sergio Del Castillo on 30/10/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class JavaFileNameFunction implements Function<Path, String> {
    @Override
    public String apply(Path path) {
        return path.getFileName().toString().replace(".java", "");
    }
}
