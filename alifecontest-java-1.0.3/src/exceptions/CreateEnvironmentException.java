/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

package exceptions;

public class CreateEnvironmentException extends Exception {
    private static final long serialVersionUID = 0L;

    public CreateEnvironmentException(String s){
        super(s);

    }

    public CreateEnvironmentException(String s, Throwable e){
        super(s, e);
    }

}
