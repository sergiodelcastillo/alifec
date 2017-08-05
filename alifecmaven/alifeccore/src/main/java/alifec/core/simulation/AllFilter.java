package alifec.core.simulation; /**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

import java.io.*;
import java.util.Vector;

/**
 * Omite todos los arhivos temporales y ocultos.
 * Selecciona solo los que empiezan con begin y finalizan con end.
 */
public class AllFilter implements FilenameFilter {
    private String end = "";
    private String begin = "";


    Vector<String> names;


    public AllFilter(String end) {
        this.end = end;
        this.begin = "";
        names = new Vector<String>();
    }

    public boolean accept(File dir, String name) {
        if (name.startsWith(".") || name.endsWith("~"))
            return false;

        if (begin.equals(""))
            return end.equals("") || name.endsWith(end);
        else
            return end.equals("") ?
                    name.startsWith(begin) :
                    name.startsWith(begin) && name.endsWith(end);
    }

    /**
     * Retorna un vector de String con todos los nombres
     * de los archivos .c o .cpp o .h del directorio path.
     */
    public static final Vector<String> list_file_cpp(String path) {

        String[] cpp_files = new String[]{".h", ".c", ".cpp"};
        Vector<String> names = new Vector<String>();

        for (String ext : cpp_files) {
            File[] cpp_colonies = new File(path).listFiles(new AllFilter(ext));

            if (cpp_colonies != null) {
                for (File f : cpp_colonies)
                    names.addElement(f.getName());
            }
        }
        return names;
    }

    /**
     * List all files in the folder path that end with .c or .cpp or .h without the extension  
     * @param path the folder to list the names of files
     * @return list of files without extension
     */
    public static Vector<String> list_names_cpp(String path) {
        String[] cpp_files = new String[]{".h", ".c", ".cpp"};
        Vector<String> names = new Vector<String>();
        try {
            find:
            for (String ext : cpp_files) {
                File[] cpp_colonies = new File(path).listFiles(new AllFilter(ext));

                if (null != cpp_colonies) {
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
     * List all file that end with .java it the folder path without the extension .java
     */
    public static Vector<String> list_names_java(String path) {
        Vector<String> names = new Vector<String>();
        File[] cpp_colonies = new File(path).listFiles(new AllFilter(".java"));

        if (cpp_colonies != null) {
            for (File f : cpp_colonies)
                names.addElement(f.getName().replace(".java", ""));
        }

        return names;
    }

    /**
     * List all file that end with .java in the folder path
     * @param path URL of files.java
     * @return list of files that end with java
     */
    public static Vector<File> get_files_java(String path) {
        Vector<File> names = new Vector<File>();
        File[] cpp_colonies = new File(path).listFiles(new AllFilter(".java"));

        if (cpp_colonies != null) {
            for (File f : cpp_colonies)
                names.addElement(f);
        }

        return names;
    }
}
