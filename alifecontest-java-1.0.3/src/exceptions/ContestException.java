/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

package exceptions;

public class ContestException extends Exception {
    private static final long serialVersionUID = 0L;

    public ContestException(String s){
        super(s);

    }
    public ContestException(String s, Throwable e){
        super(s, e);
    }


}
