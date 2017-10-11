package data.java;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by IntelliJ IDEA.
 * User: yeyo
 * Email: yeyo@druidalabs.com
 * Date: Oct 16, 2010
 * Time: 2:00:44 PM
 */
public class JavaFilter extends AbstractFilter {
    @Override
    public boolean valid(File dir, String name) {
        return dir.getName().equals(Config.MOS_FOLDER) && name.endsWith(".java");

    }
}
