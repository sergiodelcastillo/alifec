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
public class ContestConfigValidator implements Validator<ContestConfig> {

    @Override
    public ContestConfig validate(ContestConfig config) throws ValidationException {

        if (config.getPauseBetweenBattles() < 0) {
            throw new ValidationException("property pause_between_battles must have a positive integer.");
        }

        if (config.getMode() < 0 || config.getMode() > 1) {
            throw new ValidationException("property mode must have values 0 or 1.");
        }

        if (config.getPath().isEmpty()) {
            throw new ValidationException("The contest path must not be an empty string.");
        }

        if (config.getContestName().isEmpty()) {
            throw new ValidationException("The contest name must not be an empty string.");
        }

        if (!Files.isDirectory(Paths.get(config.getPath())))
            throw new ValidationException("The contest path folder does not exists: " + config.getPath());

        if (!Files.isDirectory(Paths.get(config.getContestPath())))
            throw new ValidationException("The contest name folder does not exists: " + config.getContestPath());

        if (config.getNutrients().isEmpty()) {
            throw new ValidationException("Please specify one or more distribution of nutrients.");
        }

        return config;
    }
}
