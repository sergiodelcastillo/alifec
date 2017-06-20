/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

package lib.contest;

import java.io.*;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


class Compressor {
    public static void addToZip(String source, ArrayList<String[]> des) throws IOException {
        FileOutputStream destination = new FileOutputStream(source);
        ZipOutputStream zo = new ZipOutputStream(new BufferedOutputStream(destination));

        for (String[] file : des) {
            FileInputStream im = new FileInputStream(file[0]);
            ZipEntry ze = new ZipEntry(file[1]);
            zo.putNextEntry(ze);

            /*read and write with the buffer of source to source.zip*/
            BufferedInputStream origin = new BufferedInputStream(im, 500);
            byte[] data = new byte[500];
            int count;
            while ((count = origin.read(data, 0, 500)) != -1) {
                zo.write(data, 0, count);
            }
            origin.close();
        }
        zo.close();
    }
}

