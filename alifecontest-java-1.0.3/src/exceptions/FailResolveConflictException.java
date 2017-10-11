package exceptions;

/**
 * Created by IntelliJ IDEA.
 * User: yeyo
 * Email: yeyo@druidalabs.com
 * Date: Oct 17, 2010
 * Time: 8:52:12 PM
 */
public class FailResolveConflictException extends Exception {
    private static final long serialVersionUID = 0L;

    public FailResolveConflictException(String what) {
        super(what);
    }
}