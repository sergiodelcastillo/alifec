
import java.io.FilenameFilter;
import java.io.File;
import java.util.Vector;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Omite todos los arhivos temporales y ocultos.
 * Selecciona solo los que empiezan con begin y finalizan con end.
 */
public class AllFilter implements FilenameFilter {
    private String end = "";
    private String begin = "";


    Vector<String> names = new Vector<String>();


    public AllFilter(String begin, String end) {
        this.end = end;
        this.begin = begin;
    }

    public boolean accept(File dir, String name) {
        /* Ignore temporary files
        * */
        if (name.startsWith(".") || name.endsWith("~")) {
            return false;
        }

        if (begin.equals(""))
            return end.equals("") ? true : name.endsWith(end);
        else
            return end.equals("") ?
                    name.startsWith(begin) :
                    name.startsWith(begin) && name.endsWith(end);
    }
   
   /*
    * Metodos de clase. Se los usa para facilidad de otras clases.
    */

    /**
     * Retorna un vector de String con todos los nombres
     * de los archivos .c o .cpp o .h del directorio path.
     */
    public static final Vector<String> list_file_cpp(String path) {

        String[] cpp_files = new String[]{".h", ".c", ".cpp"};
        Vector<String> names = new Vector<String>();

        for (String ext : cpp_files) {
            File[] cpp_colonies = new File(path).listFiles(new AllFilter("", ext));

            if (cpp_colonies != null) {
                for (File f : cpp_colonies)
                    names.addElement(f.getName());
            }
        }
        return names;
    }

    /**
     * Lista todos los nombres de los archivos  .c , .cpp o .h
     * del directorio path sin la extension.
     */
    public static final Vector<String> list_names_cpp(String path) {
        String[] cpp_files = new String[]{".h", ".c", ".cpp"};
        Vector<String> names = new Vector<String>();
        try {
            find:
            for (String ext : cpp_files) {
                File[] cpp_colonies = new File(path).listFiles(new AllFilter("", ext));

                if (cpp_colonies != null) {
                    for (File f : cpp_colonies) {
                        BufferedReader br = new BufferedReader(new FileReader(f));
                        String line;

                        while ((line = br.readLine()) != null) {
                            if (line.contains("class") && line.contains("public") && line.contains("Microorganism")) {
                                int index = line.indexOf("*/");

                                if (index >= 0 && index < line.indexOf(":"))
                                    line.replace(line.substring(0, index + 2), "");

                                line = line.replace("class", "").trim();
                                line = line.substring(0, line.indexOf(":"));
                                names.addElement(line);
                                continue find;
                            }
                        }
                    }
                }
            }
        } catch (IOException ex) {
            System.out.println("Cant load files of :" + path);
        }
        return names;
    }

    /**
     * Lista todos los nombres de archivos .java del directorio path sin la extension.
     */
    public static final Vector<String> list_names_java(String path) {
        Vector<String> names = new Vector<String>();
        File[] cpp_colonies = new File(path).listFiles(new AllFilter("", ".java"));

        if (cpp_colonies != null) {
            for (File f : cpp_colonies)
                names.addElement(f.getName().replace(".java", ""));
        }

        return names;
    }

    /**
     * Lista todos los archivos .java del directorio path
     */
    public static final Vector<File> get_files_java(String path) {
        Vector<File> names = new Vector<File>();
        File[] cpp_colonies = new File(path).listFiles(new AllFilter("", ".java"));

        if (cpp_colonies != null) {
            for (File f : cpp_colonies)
                names.addElement(f);
        }

        return names;
    }
}
