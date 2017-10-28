package alifec.core.validation;

import alifec.core.exception.ValidationException;

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
    public void validate(String folder) throws ValidationException {

        if (folder == null || folder.isEmpty())
            throw new ValidationException("The new contest folder is not a valid contest name: name is empty.");

        Path file = Paths.get(folder);

        if (Files.exists(file))
            throw new ValidationException("The contest folder already exists.");

        String[] names = file.toFile().getAbsolutePath().split("/");
        String name = names[names.length - 1];

        contestNameValidator.validate(name);
    }
}
