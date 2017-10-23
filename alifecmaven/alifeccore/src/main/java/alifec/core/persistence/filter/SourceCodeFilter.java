package alifec.core.persistence.filter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * @author Sergio
 * @mail: sergio.jose.delcastillo@gmail.com
 * <p>
 * Lists Source code files.
 * It does not list known hidden or temporary files.
 */
public class SourceCodeFilter extends AllFilesFilter {
    static Logger logger = LogManager.getLogger(SourceCodeFilter.class);

    private static String MO_PATTERN_STRING = "^([\\s\\S]+class([\\s]+))([\\w_-]+)(\\s)*:([\\s\\S]+)Microorganism[\\s\\S]+\\z";

    // got from https://blog.ostermiller.org/find-comment
    private static String COMMENTS_PATTERN_STRING = "(?://.*)|(?:/\\*(?:[^*]|(?:\\*+[^*/]))*\\*+/)";

    private static Pattern moPattern = Pattern.compile(MO_PATTERN_STRING);
    private static Pattern commentsPattern = Pattern.compile(COMMENTS_PATTERN_STRING);

    private final String[] suffixes;

    private SourceCodeFilter(String[] array) {
        this.suffixes = array;
    }

    @Override
    public boolean test(Path path) {
        if (!super.test(path)) return false;

        for (String suffix : suffixes) {
            if (path.getFileName().toString().endsWith(suffix)) return true;
        }

        return false;
    }


    /**
     * Returns the name of the files located in MOs folder which ends with .c, .cpp or .h.
     */
    public static List<String> listFilenameCpp(String path) {
        //todo: craete a sourcecodehelper to implement the static methods.
        List<String> names = new ArrayList<>();
        try {
            listFilesCpp(path).forEach(path1 -> {
                names.add(path1.getFileName().toString());
            });
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        return names;
    }

    /**
     * Returns the files located in MOs folder which ends with .c, .cpp or .h.
     */
    private static Stream<Path> listFilesCpp(String path) throws IOException {
        String[] cppFiles = new String[]{".h", ".c", ".cpp"};

        return Files.list(Paths.get(path)).filter(new SourceCodeFilter(cppFiles));
    }

    /**
     * List all names (class name) of cpp MOs which are located in mos folder and inherits from Microorganism class.
     *
     * @param path the folder to list the names of files
     * @return list of cpp mos
     */
    public static List<String> listCppMOs(String path) {
        List<String> names = new ArrayList<>();

        try {
            listFilesCpp(path).forEach(path1 -> {
                try {
                    byte[] data = Files.readAllBytes(path1);
                    String line = new String(data);

                    String nameOfCppMO = getNameOfMOCpp(line);

                    if (nameOfCppMO != null && !nameOfCppMO.trim().isEmpty()) {
                        names.add(nameOfCppMO);
                    }
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            });

        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);

        }
        return names;
    }


    /**
     * List the name of Java MOs which are located in MOs folder
     */
    public static List<String> listJavaMOs(String path) {
        //TODO: investigate if it can be defined by used custom collector
        List<Path> javaFiles = listJavaFiles(path);
        List<String> javaNames = new ArrayList<>();

        for (Path p : javaFiles)
            javaNames.add(p.getFileName().toString().replace(".java", ""));

        return javaNames;
    }

    /**
     * List all file that ends with .java in the folder path
     *
     * @param path URL of files.java
     * @return list of files that end with java
     */
    public static List<Path> listJavaFiles(String path) {
        try {
            return Files.list(Paths.get(path))
                    .filter(new SourceCodeFilter(new String[]{".java"}))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

        return new ArrayList<>();
    }

    public static String getNameOfMOCpp(String line) {
        String lineWithoutComments = removeComments(line);
        Matcher matcher = moPattern.matcher(lineWithoutComments);

        if (matcher.find()) {
            String moName = matcher.group(3);

            if (moName == null) return null;
            return moName.trim();
        }

        return null;
    }

    public static String removeComments(String line) {
        Matcher matcher = commentsPattern.matcher(line);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            matcher.appendReplacement(sb, "");
        }
        matcher.appendTail(sb);

        return sb.toString();
    }
}
