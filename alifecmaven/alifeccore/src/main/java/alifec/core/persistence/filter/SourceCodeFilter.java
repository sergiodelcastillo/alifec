package alifec.core.persistence.filter;

import alifec.core.persistence.custom.CppMOPredicate;
import alifec.core.persistence.custom.FileNameFunction;
import alifec.core.persistence.custom.JavaFileNameFunction;
import alifec.core.persistence.custom.NotNullPredicate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
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
    public static List<String> listFilenameCpp(String path) throws IOException {
        return filesCpp(path)
                .map(new FileNameFunction())
                .collect(Collectors.toList());
    }

    /**
     * Returns the files located in MOs folder which ends with .c, .cpp or .h.
     */
    private static Stream<Path> filesCpp(String path) throws IOException {
        String[] cppFiles = new String[]{".h", ".c", ".cpp"};

        return Files.list(Paths.get(path)).filter(new SourceCodeFilter(cppFiles));
    }

    /**
     * List all names (class name) of cpp MOs which are located in mos folder and inherits from Microorganism class.
     *
     * @param path the folder to list the names of files
     * @return list of cpp mos
     */
    public static List<String> listCppMOs(String path) throws IOException {
        return filesCpp(path)
                .map(new CppMOPredicate())
                .filter(new NotNullPredicate())
                .collect(Collectors.toList());
    }


    /**
     * List the name of Java MOs which are located in MOs folder
     */
    public static List<String> listJavaMOs(String path) throws IOException {
        return javaFiles(path)
                .map(new JavaFileNameFunction())
                .collect(Collectors.toList());
    }

    /**
     * List all file that ends with .java in the folder path
     *
     * @param path URL of files.java
     * @return list of files that end with java
     */
    public static List<Path> listJavaFiles(String path) throws IOException {
        return javaFiles(path).collect(Collectors.toList());
    }

    private static Stream<Path> javaFiles(String path) throws IOException {
        return Files.list(Paths.get(path))
                .filter(new SourceCodeFilter(new String[]{".java"}));

    }


}
