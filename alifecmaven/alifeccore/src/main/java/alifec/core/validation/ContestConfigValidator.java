package alifec.core.validation;

import alifec.core.exception.ValidationException;
import alifec.core.persistence.config.ContestConfig;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Validate if the Contest path is valid.
 * It means that path is an existing folder and the config file is located in the folder.
 * </p>
 * Created by Sergio Del Castillo on 08/10/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class ContestPathValidator implements Validator<String> {
    @Override
    public void validate(String rootFolder) throws ValidationException {

        Path path = Paths.get(rootFolder);
        if (!Files.isDirectory(path))
            throw new ValidationException("The contest path is not a directory or not exists.");

        path.resolve(ContestConfig.BASE_APP_FOLDER+ File.separator+ContestConfig.CONFIG_FILE);

        if (!Files.isRegularFile(path))
            throw new ValidationException("The contest does not have a valid config file.");
    }
}
