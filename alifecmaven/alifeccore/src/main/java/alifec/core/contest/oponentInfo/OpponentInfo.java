package alifec.core.contest.oponentInfo;

import alifec.core.exception.OpponentException;
import alifec.core.exception.ValidationException;
import alifec.core.persistence.config.ContestConfig;
import alifec.core.validation.OpponentInfoLineValidator;
import alifec.core.validation.OpponentInfoValidator;

import java.util.Objects;

public class OpponentInfo {
    private final String name;
    private final String author;
    private final String affiliation;

    public OpponentInfo(String n, String au, String af) throws OpponentException {
        this.name = n;
        this.author = au;
        this.affiliation = af;
        try {
            checkOpponentInfo(this);
        } catch (Throwable t) {
            throw new OpponentException(t.getMessage(), t);
        }
    }

    public OpponentInfo(String line) throws OpponentException {
        try {
            checkLineFromCSV(line);

            String[] info = line.trim().split(",");

            name = info[0];
            author = info[1];
            affiliation = info[2];
            checkOpponentInfo(this);
        } catch (Throwable t) {
            throw new OpponentException(t.getMessage(), t);
        }
    }

    private void checkOpponentInfo(OpponentInfo info) throws ValidationException {
        OpponentInfoValidator validator = new OpponentInfoValidator();

        validator.validate(info);
    }

    private void checkLineFromCSV(String line) throws ValidationException {
        OpponentInfoLineValidator validator = new OpponentInfoLineValidator();

        validator.validate(line);
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
        return String.format(ContestConfig.getOpponentInfoCsvFormat(), name, author, affiliation);
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
