package alifec.core.simulation;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;

/**
 * Created by Sergio Del Castillo on 16/08/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class AllFilesFilter implements FilenameFilter {

    @Override
    public boolean accept(File dir, String name) {
        if (name.startsWith(".") || name.endsWith("~"))
            return false;

        return true;
    }
}
