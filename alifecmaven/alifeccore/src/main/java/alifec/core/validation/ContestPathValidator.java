package alifec.core.validation;

import alifec.core.exception.ValidationException;
import alifec.core.persistence.config.ContestConfig;

import java.nio.file.Files;
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
    public void validate(String path) throws ValidationException {

        if (!Files.isDirectory(Paths.get(path)))
            throw new ValidationException("The contest path is not a directory or not exists.");

        if (!ContestConfig.existsConfigFile(path))
            throw new ValidationException("The contest does not have a valid config file.");
    }
}
