package alifec.core.persistence.filter; /**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Omite todos los arhivos temporales y ocultos.
 * Selecciona solo los que empiezan con begin y finalizan con end.
 */
public class SourceCodeFilter extends AllFilesFilter {
    static Logger logger = LogManager.getLogger(SourceCodeFilter.class);

    private final String[] suffixes;

    public SourceCodeFilter(String[] array) {
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
     * Retorna un vector de String con todos los nombres
     * de los archivos .c o .cpp o .h del directorio path.
     */
    public static List<String> listFileCpp(String path) {
        String[] cpp_files = new String[]{".h", ".c", ".cpp"};
        List<String> names = new ArrayList<>();


        File[] cpp_colonies = new File(path).listFiles(new SourceCodeFilter(cpp_files));

        if (cpp_colonies != null) {
            for (File f : cpp_colonies)
                names.add(f.getName());
        }

        return names;
    }

    /**
     * List all files in the folder path that end with .c or .cpp or .h without the extension
     *
     * @param path the folder to list the names of files
     * @return list of files without extension
     */
    public static List<String> listNamesCpp(String path) {
        String[] cpp_files = new String[]{".h", ".c", ".cpp"};
        List<String> names = new ArrayList<>();
        try {


            File[] cpp_colonies = new File(path).listFiles(new SourceCodeFilter(cpp_files));

            if (null == cpp_colonies) {
                return names;
            }

            for (File f : cpp_colonies) {
                BufferedReader br = new BufferedReader(new FileReader(f));
                String line;

                //todo: improve the way to detect the mo name
                //todo: it should use a patter/match instance
                while ((line = br.readLine()) != null) {
                    if (line.contains("class") && line.contains("public") && line.contains("Microorganism")) {
                        int index = line.indexOf("*/");

                        if (index >= 0 && index < line.indexOf(":"))
                            line.replace(line.substring(0, index + 2), "");

                        line = line.replace("class", "").trim();
                        line = line.substring(0, line.indexOf(":"));
                        names.add(line);
                        break;
                    }
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
    public static List<String> listNamesJava(String path) {
        List<File> javaFiles = getFilesJava(path);
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
    public static List<File> getFilesJava(String path) {
        List<File> names = new ArrayList<>();
        File[] cpp_colonies = new File(path).listFiles(new SourceCodeFilter(new String[]{".java"}));

        if (cpp_colonies != null) {
            names.addAll(Arrays.asList(cpp_colonies));
        }

        return names;
    }
}
