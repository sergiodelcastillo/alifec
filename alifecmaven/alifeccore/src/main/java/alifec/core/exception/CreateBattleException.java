/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */
package alifec.core.exception;


public class CreateBattleException extends Exception {
    private static final long serialVersionUID = 0L;

    private String tournamentName;

    public CreateBattleException(String message) {
        super(message);
    }

    public CreateBattleException(String message, String tournamentName) {
        super(message);
        this.tournamentName = tournamentName;
    }

    public String getTournamentName() {
        return tournamentName;
    }
}