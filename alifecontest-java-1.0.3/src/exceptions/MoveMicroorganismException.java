/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */
package exceptions;

public class MoveMicroorganismException extends Exception {
    private static final long serialVersionUID = 0L;
    private String colony;
    private int id;
    private Exception exception;

    /**
     * @param s
     * @param id
     */
    public MoveMicroorganismException(String s, String c, int id, Exception ex) {
        super(s);
        this.exception = ex;
        this.colony = c;
        this.id = id;
    }

    public String getColonyName() {
        return colony;
    }

    public Exception getException() {
        return exception;
    }

    public int getId(){
        return this.id;
    }
}
