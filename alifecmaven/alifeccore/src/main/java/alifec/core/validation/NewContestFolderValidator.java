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

        if (folder == null || folder.isEmpty() || folder.trim().isEmpty())
            throw new ValidationException("The new contest name must not be an empty string.");

        Path file = Paths.get(folder);
        String name = file.normalize().getFileName().toString();

        contestNameValidator.validate(name);

        if (Files.exists(file))
            throw new ValidationException("The contest folder already exists.");
    }
}
