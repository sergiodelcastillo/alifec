/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

package exceptions;

public class LoadNutrientsException extends Exception {
    private static final long serialVersionUID = 0L;

    public LoadNutrientsException(String s){
        super(s);

    }

    public LoadNutrientsException(String s, Throwable e){
        super(s, e);
    }

}
