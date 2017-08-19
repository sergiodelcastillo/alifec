package alifec.contest.view;

/**
 * Created by Sergio Del Castillo on 18/08/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */

public class TournamentPanelOpponentData {
    private String name = "";
    private long max = 0L;
    private int value = 0;

    public TournamentPanelOpponentData(String name, int value, long max) {
        if (name != null)
            this.name = name;

        this.value = value;
        this.max = max;
    }

    public String getName() {
        return name;
    }

    public long getMax() {
        return max;
    }

    public int getValue() {
        return value;
    }
}

