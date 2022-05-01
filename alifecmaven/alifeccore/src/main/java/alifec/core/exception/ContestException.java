package alifec.core.exception;

public class ContestException extends Exception {
    private static final long serialVersionUID = 0L;

    public ContestException(String message) {
        super(message);
    }

    public ContestException(String message, Throwable cause) {
        super(message, cause);
    }


}
