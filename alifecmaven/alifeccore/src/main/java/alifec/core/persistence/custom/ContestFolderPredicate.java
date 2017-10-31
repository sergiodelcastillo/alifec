package alifec.core.persistence.custom;

import alifec.core.exception.ValidationException;
import alifec.core.persistence.config.ContestConfig;
import alifec.core.validation.ContestNameValidator;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Predicate;


/**
 * <n>ContestFolderPredicate class was designated to filter only valid contest' names which
 * follows the below rule:
 * <ol>
 * <li> Starts with the prefix <b>"contest-"</b> (case insensitive).</li>
 * <li> Continues with up to 25 letters or digits and have a valid structure.</li>
 * </ol>
 * <p>
 * <n> For example, a valid name is "contest-01" but "contesto" is not valid. </n>
 */
public class ContestFolderPredicate implements Predicate<Path> {

    //private Logger logger = LogManager.getLogger(getClass());

    private final ContestNameValidator validator;

    private final boolean checkExistence;


    public ContestFolderPredicate() {
        this(true);
    }

    /**
     * @param checkExistence if the parameter is set to <b>true</b> then the existence of the folder will be checked.
     *                       This parameter was designated to use within test purposes.
     */
    public ContestFolderPredicate(boolean checkExistence) {
        this.checkExistence = checkExistence;
        this.validator = new ContestNameValidator();

    }

    @Override
    public boolean test(Path path) {
        String folderName = path.getFileName().toString();

        try {
            validator.validate(folderName);
        } catch (ValidationException e) {
            return false;
        }
        return !checkExistence || checkContestFolder(path.getParent().toString(), folderName);
    }

    private boolean checkContestFolder(String path, String name) {
        if (path == null || path.trim().isEmpty()) {
            return false;
        }
        if (name == null || path.trim().isEmpty()) {
            return false;
        }
        ContestConfig config = new ContestConfig(path, name);

        Path contestName = Paths.get(config.getContestPath());
        Path MOsFolder = Paths.get(config.getMOsPath());
        Path ReportFolder = Paths.get(config.getReportPath());
        Path CppFolder = Paths.get(config.getCppApiFolder());
        Path BackupFolder = Paths.get(config.getBackupFolder());

        return Files.isDirectory(contestName) &&
                Files.isDirectory(MOsFolder) &&
                Files.isDirectory(ReportFolder) &&
                Files.isDirectory(CppFolder) &&
                Files.isDirectory(BackupFolder);
    }
}