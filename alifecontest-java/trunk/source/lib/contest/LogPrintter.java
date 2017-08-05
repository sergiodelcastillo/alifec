/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */
package lib.contest;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;


public class LogPrintter {
    private static final String LOG_FOLDER = "log";

    private static String getName(String packageName) {
        return packageName.replace(".java", "").replace(".h", "").replace(".c", "");
    }

    public static File getErrorFile(String path, String name) {
        File tmp = new File(path + File.separator + LOG_FOLDER);
        if (!tmp.exists()) {
            tmp.mkdir();
        }

        String url = path + File.separator + LOG_FOLDER + File.separator;
        url += "log-" + name.replace(".java", "") + "-";
        url += new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());

        File res = new File(url);
        return res;
    }
}
