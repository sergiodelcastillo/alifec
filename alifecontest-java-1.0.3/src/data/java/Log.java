package data.java;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Sergio Del Castillo
 * User: yeyo
 * Date: Jun 18, 2010
 * Time: 3:44:06 PM
 * Email: yeyo@druidalabs.com
 */
public class Log {
    private static String LOG_FOLDER = Config.getAbsoluteDefaultPath() + File.separator + Config.LOG_FOLDER;

    public static void print(String txt) {
        System.out.print("LOG:" + txt);
    }

    public static void println(String txt, Throwable ex) {
        System.out.println("LOG:" + txt);
        if(ex != null) ex.printStackTrace();
    }

    public static void println(String txt) {
        System.out.println("LOG:" + txt);
    }

    public static void save(String txt) {
        try {
            if (!ensureFolder()) {
                return ;
            }

            File f = new File(getAbsoluteLogFile());
            FileWriter fw = new FileWriter(f, true);

            fw.append(txt).append('\n');
            fw.close();

        } catch (IOException ignored) {
        }
    }

    public static void save(String txt, Throwable ex) {
        save(txt);

        try {
            ensureFolder();

            File f = new File(getAbsoluteLogFile());
            FileWriter fw = new FileWriter(f, true);
            PrintWriter pw = new PrintWriter(fw);
            
            ex.printStackTrace(pw);
            pw.close();
        } catch (IOException ignored) {
        }
    }

    public static void printlnAndSave(String txt) {
        println(txt);
        save(txt);
    }

    public static void printlnAndSave(String txt, Throwable ex) {
        println(txt, ex);
        save(txt, ex);
    }

    private static boolean ensureFolder() {
        File f = new File(LOG_FOLDER);

        if (!f.exists()) {
            if (!f.mkdirs())
                return false;
        }
        return true;
    }

    public static String getAbsoluteLogFile() {
        return LOG_FOLDER + File.separator + "log-" + Config.getFormattedDate();
    }
}
