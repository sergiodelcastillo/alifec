package alifec.core.exception;

public class MoveMicroorganismException extends Exception {
    private static final long serialVersionUID = 0L;
    private String colony;
    private Exception exception;

    /**
     * @param s
     */
    public MoveMicroorganismException(String s, String c, Exception ex) {
        super(s);
        exception = ex;
        colony = c;
    }

    public String getColonyName() {
        return colony;
    }

    public Exception getException() {
        return exception;
    }
}
