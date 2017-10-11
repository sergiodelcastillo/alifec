/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

package controller.java.contest;

import data.java.Config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class CodeGenerator {
    /**
     * copy the file src to the file dst
     *
     * @param src
     * @param dst
     * @throws IOException
     */
    public static boolean copy(File src, File dst) {
        try {
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dst);

            byte[] buf = new byte[1024];
            int len;

            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            in.close();
            out.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public static boolean generateExamples(String path) {
        return generateExamples(Config.SRC_FOLDER + File.separator + Config.MOS_FOLDER, path);
    }

    public static boolean generateExamples(String src, String des) {

        File[] files = new File(src).listFiles();

        if (files == null) return true;

        boolean status = true;

        if(!ensureDestination(des)) return false;

        for (File f : files) {
            if (f.isHidden()) continue;
            if (f.isFile()) {
                status &= copy(f, new File(des + File.separator + f.getName()));
            } else {
                File tmp = new File(des + File.separator + f.getName());
                if (!tmp.exists())
                    status &= tmp.mkdirs();
                status &= generateExamples(f.getAbsolutePath(), tmp.getAbsolutePath());
            }
        }
        return status;
    }

    private static boolean ensureDestination(String des) {
        File file = new File(des);

        if (!file.exists()) {
            return  file.mkdir();
        }
        return true;
    }

}
