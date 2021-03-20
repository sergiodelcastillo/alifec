package alifec.core.contest.oponentInfo;

import alifec.core.exception.OpponentException;
import alifec.core.exception.ValidationException;
import alifec.core.persistence.config.ContestConfig;
import alifec.core.validation.OpponentInfoLineValidator;
import alifec.core.validation.OpponentInfoValidator;

import java.util.Objects;

public class OpponentInfo {
    //validators
    private static OpponentInfoValidator opponentInfoValidator = new OpponentInfoValidator();
    private static OpponentInfoLineValidator opponentInfoLineValidator = new OpponentInfoLineValidator();

    private final String name;
    private final String author;
    private final String affiliation;

    private OpponentInfo(String n, String au, String af) {
        this.name = n;
        this.author = au;
        this.affiliation = af;
    }

    public static OpponentInfo buildFromCSVLine(String line) throws OpponentException {
        checkLineFromCSV(line);

        String[] info = line.trim().split(",");

        OpponentInfo obj = new OpponentInfo(info[0], info[1], info[2]);

        return checkOpponentInfo(obj);
    }

    public static OpponentInfo build(String n, String au, String af) throws OpponentException {
        OpponentInfo obj = new OpponentInfo(n, au, af);

        return checkOpponentInfo(obj);
    }

    private static OpponentInfo checkOpponentInfo(OpponentInfo info) throws OpponentException {
        try {
            return opponentInfoValidator.validate(info);
        } catch (ValidationException t) {
            throw new OpponentException(t.getMessage(), t);
        }
    }

    private static String checkLineFromCSV(String line) throws OpponentException {
        try {
            return opponentInfoLineValidator.validate(line);
        } catch (ValidationException t) {
            throw new OpponentException(t.getMessage(), t);
        }
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
