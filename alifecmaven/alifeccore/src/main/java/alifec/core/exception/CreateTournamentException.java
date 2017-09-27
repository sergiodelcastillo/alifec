/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */
 
package alifec.core.exception;

public class CreateTournamentException extends Exception {
    private static final long serialVersionUID = 0L;
    
	 
    public CreateTournamentException(String message){
        super(message);
		  	 
    }
    public CreateTournamentException(String message, Throwable cause){
        super(message, cause);
    }
}