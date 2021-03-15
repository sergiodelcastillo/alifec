package alifec.core.persistence;

import alifec.core.persistence.custom.CppMOPredicate;
import alifec.core.persistence.custom.FileNameFunction;
import alifec.core.persistence.custom.JavaFileNameFunction;
import alifec.core.persistence.custom.NotNullPredicate;
import alifec.core.persistence.custom.SuffixPredicate;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Sergio Del Castillo on 30/10/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class SourceCodeFileManager {
    private final Path path;

    public SourceCodeFileManager(String mosPath) {
        this.path = Paths.get(mosPath);
    }

    /**
     * Returns the name of the files located in MOs folder which ends with .c, .cpp or .h.
     */
    public List<String> listFilenameCpp() throws IOException {
        try (Stream<Path> stream = filesCpp()) {
            return stream.map(new FileNameFunction())
                    .collect(Collectors.toList());
        }
    }

    /**
     * Returns the files located in MOs folder which ends with .c, .cpp or .h.
     */
    private Stream<Path> filesCpp() throws IOException {
        String[] cppFiles = new String[]{".h", ".c", ".cpp"};

        return Files.list(path).filter(new SuffixPredicate(cppFiles));
    }

    /**
     * List all names (class name) of cpp MOs which are located in mos folder and inherits from Microorganism class.
     *
     * @return list of cpp mos
     */
    public List<String> listCppMOs() throws IOException {
        try (Stream<Path> stream = filesCpp()) {
            return stream.map(new CppMOPredicate())
                    .filter(new NotNullPredicate())
                    .collect(Collectors.toList());
        }
    }


    /**
     * List the name of Java MOs which are located in MOs folder
     */
    public List<String> listJavaMOs() throws IOException {
        try (Stream<Path> stream = javaFiles()) {
            return stream.map(new JavaFileNameFunction())
                    .collect(Collectors.toList());
        }
    }

    /**
     * List all file that ends with .java in the folder path
     *
     * @return list of files that end with java
     */
    public List<Path> listJavaFiles() throws IOException {
        try (Stream<Path> stream = javaFiles()) {
            return stream.collect(Collectors.toList());
        }
    }

    private Stream<Path> javaFiles() throws IOException {
        return Files.list(path)
                .filter(new SuffixPredicate(new String[]{".java"}));

    }
}
