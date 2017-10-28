
package alifec.core.exception;

public class TournamentException extends Exception {
    private static final long serialVersionUID = 0L;
    
	 
    public TournamentException(String message){
        super(message);
		  	 
    }
    public TournamentException(String message, Throwable cause){
        super(message, cause);
    }
}