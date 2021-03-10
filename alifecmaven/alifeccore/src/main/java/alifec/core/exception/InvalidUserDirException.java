package alifec.core.exception;

/**
 * Created by Sergio Del Castillo on 23/06/18.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class InvalidUserDirException extends ConfigFileException {
    public InvalidUserDirException(String text, Throwable ex) {
        super(text, ex);
    }


}
