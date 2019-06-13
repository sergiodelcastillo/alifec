package alifec.core.persistence;

import alifec.core.persistence.custom.*;

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
        Stream<Path> stream = filesCpp();

        List<String> list = stream.map(new FileNameFunction())
                .collect(Collectors.toList());

        stream.close();
        return list;
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
        Stream<Path> stream = filesCpp();

        List<String> list = stream.map(new CppMOPredicate()).filter(new NotNullPredicate()).collect(Collectors.toList());

        stream.close();
        return list;
    }


    /**
     * List the name of Java MOs which are located in MOs folder
     */
    public List<String> listJavaMOs() throws IOException {
        Stream<Path> stream = javaFiles();

        List<String> list = stream.map(new JavaFileNameFunction()).collect(Collectors.toList());

        stream.close();
        return list;
    }

    /**
     * List all file that ends with .java in the folder path
     *
     * @return list of files that end with java
     */
    public List<Path> listJavaFiles() throws IOException {
        Stream<Path> stream = javaFiles();

        List<Path> list = stream.collect(Collectors.toList());

        stream.close();
        return list;
    }

    private Stream<Path> javaFiles() throws IOException {
        return Files.list(path)
                .filter(new SuffixPredicate(new String[]{".java"}));

    }
}
