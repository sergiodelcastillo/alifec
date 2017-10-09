package alifec.core.validation;

import java.io.File;

/**
 * Created by Sergio Del Castillo on 08/10/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class ContestFolderValidator implements Validator<String> {

    private ContestNameValidator contestNameValidator;

    public ContestFolderValidator() {
        this.contestNameValidator = new ContestNameValidator();
    }

    @Override
    public boolean validate(String folder) {

        if (folder == null || folder.isEmpty()) return false;

        File file = new File(folder);

        if (file.exists()) return false;

        String[] names = file.getAbsolutePath().split("/");
        String name = names[names.length - 1];

        return contestNameValidator.validate(name);
    }
}
