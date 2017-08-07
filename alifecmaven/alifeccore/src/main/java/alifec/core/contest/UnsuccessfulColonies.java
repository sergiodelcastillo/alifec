package alifec.core.contest;

/**
 * Created by Sergio Del Castillo on 06/08/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class UnsuccessfulColonies {

    private final boolean isUnsuccessful;
    private String tournament;
    private String colonyA;
    private String colonyB;

    public UnsuccessfulColonies(String tournament, String a, String b, boolean unsuccessful) {
        this.tournament = tournament;
        this.colonyA = a;
        this.colonyB = b;
        this.isUnsuccessful = unsuccessful;
    }

    public String getTournament() {
        return tournament;
    }

    public String getColonyA() {
        return colonyA;
    }

    public String getColonyB() {
        return colonyB;
    }

    public boolean isUnsuccessful() {
        return isUnsuccessful;
    }
}
