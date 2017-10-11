package data.java.tournament;

import data.java.tournament.AccumulatedDTO;

/**
 * Created by Sergio Del Castillo
 * User: yeyo
 * Date: Jun 11, 2010
 * Time: 10:37:00 PM
 * Email: yeyo@druidalabs.com
 */
public class ListOpponent {
    /**
     * list index
     */
    private int index;
    /**
     * max value of all list
     */
    private long max = 0L;
    /**
     * information of the colony
     */
    private AccumulatedDTO dto;

    public ListOpponent(AccumulatedDTO dto, long max, int index) {
        this.dto = dto;
        this.max = max;
        this.index = index;
    }

    public String getName() {
        return dto.getName();
    }

    public long getMax() {
        return max;
    }

    public float getValue() {
        return dto.getEnergy();
    }
    public int getIndex() {
        return index;
    }
}