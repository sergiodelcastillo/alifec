package alifec.core.validation;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Validates if the Contest Folder does not exists and have a valid name.
 * Created by Sergio Del Castillo on 08/10/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class NewContestFolderValidator implements Validator<String> {

    private ContestNameValidator contestNameValidator;

    public NewContestFolderValidator() {
        this.contestNameValidator = new ContestNameValidator();
    }

    @Override
    public boolean validate(String folder) {

        if (folder == null || folder.isEmpty()) return false;

        Path file = Paths.get(folder);

        if (Files.exists(file)) return false;

        String[] names = file.toFile().getAbsolutePath().split("/");
        String name = names[names.length - 1];

        return contestNameValidator.validate(name);
    }
}
