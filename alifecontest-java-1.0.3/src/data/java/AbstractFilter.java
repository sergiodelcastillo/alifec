package data.java;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by IntelliJ IDEA.
 * User: yeyo
 * Email: yeyo@druidalabs.com
 * Date: Oct 16, 2010
 * Time: 2:03:35 PM
 */
public abstract class AbstractFilter implements FilenameFilter {
    @Override
    public boolean accept(File dir, String name) {
        File file = new File(dir + File.separator + name);

        return  file.canRead() &&
                file.canWrite() &&
                !file.isHidden() &&
                valid(dir, name) ;
    }

    public abstract boolean valid(File dir, String name);
}
