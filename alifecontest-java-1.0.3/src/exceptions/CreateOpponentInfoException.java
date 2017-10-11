/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

package exceptions;

public class CreateOpponentInfoException extends Exception {
    private static final long serialVersionUID = 0L;

    public CreateOpponentInfoException(String s){
        super(s);

    }

    public CreateOpponentInfoException(String s, Throwable e){
        super(s, e);
    }

}
