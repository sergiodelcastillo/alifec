/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

package exceptions;

public class LoadTournamentException extends Exception {
    private static final long serialVersionUID = 0L;

    public LoadTournamentException(String s){
        super(s);

    }

    public LoadTournamentException(String s, Throwable e){
        super(s, e);
    }

}
