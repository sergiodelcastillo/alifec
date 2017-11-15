package alifec.core.persistence.dto;

import alifec.core.exception.ValidationException;
import alifec.core.validation.LiveInstanceValidator;

/**
 * Created by Sergio Del Castillo on 14/11/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class LiveInstance {


    public LiveInstance(String line) throws ValidationException {
        LiveInstanceValidator validator = new LiveInstanceValidator();
        validator.validate(line);

        char firstCharacter = line.charAt(0);

        switch (firstCharacter){
            case 'b':
                parseBattle(line);
                break;
            case 'n':
                parseNutrients(line);
                break;
            case 'm':
                parseMOs(line);
                break;
            case 'e':
                parseEnd(line);
                break;
        }
    }

    private void parseEnd(String line) {

    }

    private void parseMOs(String line) {

    }

    private void parseNutrients(String line) {

    }

    private void parseBattle(String line) {

    }


}
