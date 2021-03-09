package alifec.core.exception;

/**
 * Created by Sergio Del Castillo on 23/06/18.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class ConfigFileNotFoundException extends ConfigFileException {

    public ConfigFileNotFoundException(String text, Throwable ex) {
        super(text, ex);
    }
}
