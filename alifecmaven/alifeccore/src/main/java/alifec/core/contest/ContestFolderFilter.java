/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */
package alifec.core.contest;

import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ContestFolderFilter extends FileFilter implements FilenameFilter {
    public static final String CONTEST_PREFIX = "contest-";
    private static String STRING_PATTERN = "^(" + CONTEST_PREFIX + ")([a-zA-Z_0-9]{1,25})$";

    private Pattern pattern;

    private final boolean checkExistence;


    public ContestFolderFilter() {
        this(true);
    }

    /**
     *
     * @param checkExistence if the parameter is set to <b>true</b> then the existence of the folder will be checked.
     *                       This parameter was designated to use within test purposes.
     */
    public ContestFolderFilter(boolean checkExistence) {
        this.checkExistence = checkExistence;
        pattern = Pattern.compile(STRING_PATTERN, Pattern.CASE_INSENSITIVE);
    }

    public boolean accept(File dir, String name) {
        try {
            if (!checkPattern(name)) return false;

            if (!checkExistence) return true;

            return new File(dir.getAbsolutePath() + File.separator + name).isDirectory();

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean accept(File dir) {
        try {
            String[] names = dir.getAbsolutePath().split("/");
            String name = names[names.length - 1];

            return accept(dir.getParentFile(), name);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public String getDescription() {
        return "Contest-file";
    }

    public boolean checkPattern(String name) {
        if(name == null) return false;

        Matcher matcher = pattern.matcher(name);

        return matcher.matches();
    }
}