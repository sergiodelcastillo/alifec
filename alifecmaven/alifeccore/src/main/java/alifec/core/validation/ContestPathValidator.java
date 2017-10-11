package alifec.core.validation;

import alifec.core.persistence.ContestConfig;

import java.io.File;

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
        File f = new File(path);

        if (!f.exists() || !f.isDirectory()) return false;

        return ContestConfig.existsConfigFile(path);

    }
}
