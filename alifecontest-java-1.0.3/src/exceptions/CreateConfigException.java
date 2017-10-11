/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

package exceptions;

public class CreateConfigException extends Exception {
    private static final long serialVersionUID = 0L;

    public CreateConfigException(String s){
        super(s);

    }

    public CreateConfigException(String s, Throwable e){
        super(s, e);
    }

}
