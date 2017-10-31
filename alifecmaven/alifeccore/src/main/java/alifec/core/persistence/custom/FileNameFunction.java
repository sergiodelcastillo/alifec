package alifec.core.persistence.custom;

import java.nio.file.Path;
import java.util.function.Function;
import java.util.zip.ZipEntry;

/**
 * Created by Sergio Del Castillo on 28/10/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class FileNameFunction implements Function<Object, String> {
    @Override
    public String apply(Object object) {
        if (object instanceof Path) {
            return ((Path) object).getFileName().toString();
        }
        if (object instanceof ZipEntry) {
            return ((ZipEntry) object).getName();
        }

        return object.toString();
    }
}
