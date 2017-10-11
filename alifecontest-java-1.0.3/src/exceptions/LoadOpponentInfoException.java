/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

package exceptions;

public class LoadOpponentInfoException extends Exception {
    private static final long serialVersionUID = 0L;

    public LoadOpponentInfoException(String s){
        super(s);

    }

    public LoadOpponentInfoException(String s, Throwable e){
        super(s, e);
    }

}
