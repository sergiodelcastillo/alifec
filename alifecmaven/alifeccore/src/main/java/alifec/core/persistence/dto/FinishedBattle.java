package alifec.core.persistence.dto;

/**
 * Created by nacho on 15/11/17.
 */
public class FinishedBattle {

    private int winnerId;
    private String winnerName;
    private float winnerEnergy;

    public FinishedBattle(int winnerId, String winnerName, float winnerEnergy) {
        this.winnerId = winnerId;
        this.winnerName = winnerName;
        this.winnerEnergy = winnerEnergy;
    }

    public int getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(int winnerId) {
        this.winnerId = winnerId;
    }

    public String getWinnerName() {
        return winnerName;
    }

    public void setWinnerName(String winnerName) {
        this.winnerName = winnerName;
    }

    public float getWinnerEnergy() {
        return winnerEnergy;
    }

    public void setWinnerEnergy(float winnerEnergy) {
        this.winnerEnergy = winnerEnergy;
    }
}
