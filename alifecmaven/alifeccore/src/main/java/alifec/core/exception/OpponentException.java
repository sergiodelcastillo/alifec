
package alifec.core.exception;


public class OpponentException extends Exception {
    private static final long serialVersionUID = 0L;

    public OpponentException(String message) {
        super(message);
    }

    public OpponentException(String message, Throwable reason) {
        super(message, reason);
    }
}