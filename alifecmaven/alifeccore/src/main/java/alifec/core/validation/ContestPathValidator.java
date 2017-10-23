package alifec.core.validation;

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
    public boolean validate(String path) {

        return Files.isDirectory(Paths.get(path)) &&
                ContestConfig.existsConfigFile(path);

    }
}
