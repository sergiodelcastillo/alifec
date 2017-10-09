package alifec.core.persistence.filter;

import alifec.core.validation.ContestNameValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.io.FilenameFilter;


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
public class ContestFolderFilter extends FileFilter implements FilenameFilter {

    private Logger logger = LogManager.getLogger(getClass());

    private ContestNameValidator validator;

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
        this.validator = new ContestNameValidator();

    }

    public boolean accept(File dir, String name) {
        try {
            if (!validator.validate(name)) return false;

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




}