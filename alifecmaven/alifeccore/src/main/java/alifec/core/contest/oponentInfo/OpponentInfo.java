package alifec.core.contest.oponentInfo;

import java.util.Objects;

public class OpponentInfo {
    private final String name;
    private final String author;
    private final String affiliation;

    public OpponentInfo(String n, String au, String af) {
        this.name = n;
        this.author = au;
        this.affiliation = af;
    }

    public OpponentInfo(String line) {
        if (line == null || line.isEmpty())
            throw new IllegalArgumentException("The line is empty.");

        String[] info = line.trim().split(",");

        if (info.length != 3)
            throw new IllegalArgumentException("The line does not have the expected pattern.");

        name = info[0];
        author = info[1];
        affiliation = info[2];
    }

    public String getAffiliation() {
        return affiliation;
    }

    public String getAuthor() {
        return author;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(name)
                .append(',')
                .append(author)
                .append(',')
                .append(affiliation);

        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OpponentInfo)) return false;
        OpponentInfo that = (OpponentInfo) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(author, that.author) &&
                Objects.equals(affiliation, that.affiliation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, author, affiliation);
    }
}
