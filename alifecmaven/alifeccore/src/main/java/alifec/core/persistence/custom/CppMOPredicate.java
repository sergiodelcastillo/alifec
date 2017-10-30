package alifec.core.persistence.custom;

import alifec.core.contest.Battle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by Sergio Del Castillo on 23/10/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class CppMOPredicate implements Function<Path, String> {
    private Logger logger = LogManager.getLogger(CppMOPredicate.class);

    private static String MO_PATTERN_STRING = "^([\\s\\S]+class([\\s]+))([\\w_-]+)(\\s)*:([\\s\\S]+)Microorganism[\\s\\S]+\\z";

    // got from https://blog.ostermiller.org/find-comment
    private static String COMMENTS_PATTERN_STRING = "(?://.*)|(?:/\\*(?:[^*]|(?:\\*+[^*/]))*\\*+/)";

    private static Pattern moPattern = Pattern.compile(MO_PATTERN_STRING);
    private static Pattern commentsPattern = Pattern.compile(COMMENTS_PATTERN_STRING);


    @Override
    public String apply(Path path) {
        try {
            byte[] data = Files.readAllBytes(path);
            String line = new String(data);

            String nameOfCppMO = getNameOfMOCpp(line);

            if (nameOfCppMO != null && !nameOfCppMO.trim().isEmpty()) {
                return nameOfCppMO;
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }

    public String getNameOfMOCpp(String line) {
        String lineWithoutComments = removeComments(line);
        Matcher matcher = moPattern.matcher(lineWithoutComments);

        if (matcher.find()) {
            String moName = matcher.group(3);

            if (moName == null) return null;
            return moName.trim();
        }

        return null;
    }

    public String removeComments(String line) {
        Matcher matcher = commentsPattern.matcher(line);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            matcher.appendReplacement(sb, "");
        }
        matcher.appendTail(sb);

        return sb.toString();
    }
}
