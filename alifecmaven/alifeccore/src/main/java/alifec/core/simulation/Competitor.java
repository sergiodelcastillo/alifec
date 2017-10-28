package alifec.core.simulation;

import alifec.core.contest.oponentInfo.OpponentInfo;
import alifec.core.exception.OpponentException;

/**
 * Created by Sergio Del Castillo on 28/10/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class Competitor {

    private final int index;
    private OpponentInfo info;

    public Competitor(int i, String name, String author, String affiliation) throws OpponentException {
        this.index = i;
        this.info = new OpponentInfo(name, author, affiliation);
    }

    public int getId() {
        return index;
    }

    public String getColonyName() {
        return info.getName();
    }

    public String getAuthor() {
        return info.getAuthor();
    }

    public String getAffiliation() {
        return info.getAffiliation();
    }

    @Override
    public String toString() {
        return info.getName();
    }

    public OpponentInfo getInfo() {
        return info;
    }
}
