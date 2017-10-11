/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

package exceptions;

public class SaveOpponentInfoException extends Exception {
    private static final long serialVersionUID = 0L;

    public SaveOpponentInfoException(String s){
        super(s);

    }

    public SaveOpponentInfoException(String s, Throwable e){
        super(s, e);
    }

}
