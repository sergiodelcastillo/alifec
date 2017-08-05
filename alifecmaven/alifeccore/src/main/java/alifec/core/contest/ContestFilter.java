/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */
package alifec.core.contest;

import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.io.FilenameFilter;


public class ContestFilter extends FileFilter implements FilenameFilter {


    public boolean accept(File dir, String name) {
        try {
            return name.toLowerCase().indexOf("contest-") == 0 &&
                    new File(dir.getAbsolutePath() + File.separator + name).isDirectory();

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean accept(File dir) {
        try {
            String[] names = dir.getAbsolutePath().split("/");
            String name = names[names.length - 1];

            if (name.toLowerCase().indexOf("contest-") != 0) return false;
        } catch (Exception ex) { 
            ex.printStackTrace();
            return false;
        }
        return dir.isDirectory();
    }

    @Override
    public String getDescription() {
        return "Contest-file";
    }
}