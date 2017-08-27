/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */
package alifec.core.persistence.filter;

import alifec.core.persistence.ContestConfig;
import alifec.core.validation.Validator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * <n>ContestFolderFilter class was designated to filter only valid contest' names which
 * follows the below rule:
 * <ol>
 * <li> Starts with the prefix <b>"contest-"</b> (case insensitive).</li>
 * <li> Continues with up to 25 letters or digits.</li>
 * </ol>
 *
 * <n> For example, a valid name is "contest-01" but "contesto" is not valid. </n>
 */
public class ContestFolderFilter extends FileFilter implements FilenameFilter, Validator<String> {

    private Logger logger = LogManager.getLogger(getClass());

    private static String STRING_PATTERN = "^(" + ContestConfig.CONTEST_NAME_PREFIX+ ")([a-zA-Z_0-9]{1,25})$";

    private Pattern pattern;

    private final boolean checkExistence;


    public ContestFolderFilter() {
        this(true);
    }

    /**
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

            File contest = new File(dir.getAbsolutePath() + File.separator + name);

            return contest.exists() && contest.isDirectory();

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return false;
        }
    }

    public boolean accept(File dir) {
        try {
            String[] names = dir.getAbsolutePath().split("/");
            String name = names[names.length - 1];

            return accept(dir.getParentFile(), name);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return false;
        }
    }

    @Override
    public String getDescription() {
        return "Contest-file";
    }

    public boolean checkPattern(String name) {
        if (name == null) return false;

        Matcher matcher = pattern.matcher(name);

        return matcher.matches();
    }

    @Override
    public boolean validate(String contestFolder) {
        return accept(new File(contestFolder));
    }
}