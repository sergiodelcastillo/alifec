/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

package exceptions;

public class SaveNutrientsException extends Exception {
    private static final long serialVersionUID = 0L;

    public SaveNutrientsException(String s){
        super(s);

    }

    public SaveNutrientsException(String s, Throwable e){
        super(s, e);
    }

}
