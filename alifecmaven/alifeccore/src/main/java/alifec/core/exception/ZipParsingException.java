package alifec.core.exception;

/**
 * Created by Sergio Del Castillo on 22/08/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 * <p>
 * We want to let a checked exception escape from a lambda that does not
 * allow exceptions. The only way I can see of doing this is to wrap the
 * exception in a RuntimeException. This is a somewhat unfortunate side
 * effect of lambda's being based off of interfaces.
 */
public class ZipParsingException extends RuntimeException {

    public ZipParsingException(String reason, Exception inner) {
        super(reason, inner);
    }
}