package data.java.contest;

/**
 * Created by Sergio Del Castillo
 * User: yeyo
 * Date: Jul 15, 2010
 * Time: 7:18:10 PM
 * Email: yeyo@druidalabs.com
 */
public class RankingDTO implements Comparable<RankingDTO> {

    /**
     * The name of the colony
     */
    private String name;

    /**
     * The author of the colony
     */
    private String author;

    /**
     * The affiliation of the colony
     */
    private String aff;

    /**
     * The accumulated points
     */
    private int won;


    public RankingDTO(String name, int won) {
        this.name = name;

        this.won = won;
    }

    @Override
    public int compareTo(RankingDTO o) {
        if (won < o.won) return -1;
        else return won > o.won ? 1 : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RankingDTO that = (RankingDTO) o;

        return name.equals(that.name);

    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public String getAffiliation() {
        return aff;
    }

    public int getPoints() {
        return won;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setAff(String aff) {
        this.aff = aff;
    }

    public void add(int points) {
        this.won += points;
    }


    public String toCSV() {
        return name + "," + author + "," + aff + "," + won + ",";
    }


    public String toTxt() {
        return ensureSize(name, data.java.Defs.MAX_LENGTH) +
                ensureSize(author, data.java.Defs.MAX_LENGTH) +
                ensureSize(aff, data.java.Defs.MAX_LENGTH) +
                ensureSize(won + "", data.java.Defs.MAX_LENGTH);
    }

    private static String ensureSize(String text, int size) {
        while (text.length() < size) {
            text += ' ';
        }
        return text;
    }

    public static String getTxtSignature(int size) {
        return ensureSize("Name", size) +
                ensureSize("Author", size) +
                ensureSize("Affiliation", size) +
                ensureSize("Points", size);
    }
}
