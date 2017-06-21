/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */
 
package exceptions;

public class CompilerException extends Exception{

    private static final long serialVersionUID = 0L;
    
    public CompilerException(){
        super("cannot find the java compiler.");
    }
           
}
