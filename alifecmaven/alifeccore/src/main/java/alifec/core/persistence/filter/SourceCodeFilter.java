package alifec.core.persistence.filter;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author Sergio
 * @mail: sergio.jose.delcastillo@gmail.com
 * <p>
 * Lists Source code files.
 * It does not list known hidden or temporary files.
 */
public class SourceCodeFilter extends AllFilesFilter {
    static Logger logger = Logger.getLogger(SourceCodeFilter.class);

    private static String MO_PATTERN_STRING = "^([\\s\\S]+class([\\s]+))([\\w_-]+)(\\s)*:([\\s\\S]+)Microorganism[\\s\\S]+\\z";

    // got from https://blog.ostermiller.org/find-comment
    private static String COMMENTS_PATTERN_STRING = "(?://.*)|(?:/\\*(?:[^*]|(?:\\*+[^*/]))*\\*+/)";

    private static Pattern moPattern = Pattern.compile(MO_PATTERN_STRING);
    private static Pattern commentsPattern = Pattern.compile(COMMENTS_PATTERN_STRING);

    private final String[] suffixes;

    private SourceCodeFilter(String[] array) {
        this.suffixes = array;
    }

    public boolean accept(File dir, String name) {
        if (!super.accept(dir, name)) return false;

        for (String suffix : suffixes) {
            if (name.endsWith(suffix)) return true;
        }

        return false;
    }

    /**
     * Returns the name of the files located in MOs folder which ends with .c, .cpp or .h.
     */
    public static List<String> listFilenameCpp(String path) {
        List<String> names = new ArrayList<>();
        File[] files = listFileCpp(path);

        for (File f : files) names.add(f.getName());

        return names;
    }

    /**
     * Returns the files located in MOs folder which ends with .c, .cpp or .h.
     */
    private static File[] listFileCpp(String path) {
        String[] cppFiles = new String[]{".h", ".c", ".cpp"};
        File[] cppColonies = new File(path).listFiles(new SourceCodeFilter(cppFiles));

        if (cppColonies == null)
            return new File[0];

        return cppColonies;
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

            File[] cppCodeList = listFileCpp(path);

            for (File cppCode : cppCodeList) {
                byte[] data = Files.readAllBytes(cppCode.toPath());
                String line = new String(data);

                String nameOfCppMO = getNameOfMOCpp(line);

                if (nameOfCppMO != null && !nameOfCppMO.trim().isEmpty()) {
                    names.add(nameOfCppMO);
                }
            }

        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);

        }
        return names;
    }


    /**
     * List all file that end with .java it the folder path without the extension .java
     */
    public static List<String> listJavaFilesWithoutExtension(String path) {
        List<File> javaFiles = listJavaFiles(path);
        List<String> javaNames = new ArrayList<>();

        for (File javaFile : javaFiles)
            javaNames.add(javaFile.getName().replace(".java", ""));

        return javaNames;
    }

    /**
     * List all file that end with .java in the folder path
     *
     * @param path URL of files.java
     * @return list of files that end with java
     */
    public static List<File> listJavaFiles(String path) {
        List<File> names = new ArrayList<>();
        File[] cpp_colonies = new File(path).listFiles(new SourceCodeFilter(new String[]{".java"}));

        if (cpp_colonies != null) {
            names.addAll(Arrays.asList(cpp_colonies));
        }

        return names;
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
