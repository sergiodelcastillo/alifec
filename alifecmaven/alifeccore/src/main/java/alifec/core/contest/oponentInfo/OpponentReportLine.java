package alifec.core.contest.oponentInfo;

/**
 * Created by Sergio Del Castillo on 19/08/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class OpponentReportLine implements Comparable<OpponentReportLine> {
    private String name;
    private String author;
    private String affiliation;
    private int ranking;
    private float accumulated;

    public OpponentReportLine(String name, String author, String affiliation, int ranking, float accumulated) {
        this.name = name;
        this.author = author;
        this.affiliation = affiliation;
        this.ranking = ranking;
        this.accumulated = accumulated;
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public String getAffiliation() {
        return affiliation;
    }

    public int getRanking() {
        return ranking;
    }

    public float getAccumulated() {
        return accumulated;
    }

    @Override
    public int compareTo(OpponentReportLine o) {
        return Integer.compare(ranking, o.ranking);
    }


}
