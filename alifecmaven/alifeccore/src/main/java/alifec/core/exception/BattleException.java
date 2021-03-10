package alifec.core.exception;


public class BattleException extends Exception {
    private static final long serialVersionUID = 0L;

    public BattleException(String message) {
        super(message);
    }

    public BattleException(String message, Throwable reason) {
        super(message, reason);
    }
}