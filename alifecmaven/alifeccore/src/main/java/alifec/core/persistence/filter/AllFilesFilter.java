package alifec.core.persistence.filter;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by Sergio Del Castillo on 16/08/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class AllFilesFilter implements FilenameFilter {

    @Override
    public boolean accept(File dir, String name) {
        return !(name.startsWith(".") || name.endsWith("~"));
    }
}