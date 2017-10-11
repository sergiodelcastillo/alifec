/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */
package data.java;

import controller.java.Validator;
import controller.java.contest.Contest;
import data.java.Config;

import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.io.FilenameFilter;


public class ContestFilter extends AbstractFilter {
    @Override
    public boolean valid(File dir, String name) {
        String path = dir + File.separator + name;
        File f = new File(path);

        return f.isDirectory() && Contest.existContest(path); 
    }
}