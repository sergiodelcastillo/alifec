
package alifec.core.exception;

public class CreateContestException extends Exception {
    private static final long serialVersionUID = 0L;
    
    public CreateContestException(String message){
        super(message);
    }

    public CreateContestException(String message, Throwable cause){
        super(message, cause);
    }
	 
           
}
