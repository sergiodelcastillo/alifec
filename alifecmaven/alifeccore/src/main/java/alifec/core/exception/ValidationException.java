package alifec.core.exception;

/**
 * Created by Sergio Del Castillo on 28/10/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class ValidationException extends Exception {
    private static final long serialVersionUID = 0L;

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable reason) {
        super(message, reason);
    }
}
