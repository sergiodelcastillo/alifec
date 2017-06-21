/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

package lib.oponentInfo;

import java.io.*;
import java.util.Vector;


public class OponentInfoManager {
    Vector<OponentInfo> oponents = new Vector<OponentInfo>();
    public static final String OPONENTS_FILE = "competitors";
    private String path = "";

    public OponentInfoManager(String path) {
        this.path = path;
    }

    public void add(String name, String author, String affiliation) throws IOException {
        OponentInfo op = new OponentInfo(name, author, affiliation);
        add(op, true);

    }

    void add(OponentInfo op, boolean write) throws IOException {
        if (!oponents.contains(op)) {
            if (write) op.write(path + File.separator + OPONENTS_FILE);
            oponents.addElement(op);
        }
    }

    public void del(String name) throws IOException {
        for (OponentInfo op : oponents) {
            if (op.contain(name)) {
                op.del(path + File.separator + OPONENTS_FILE);
                oponents.remove(op);

            }
        }
    }

    public void read() throws IOException {
        try {
            BufferedReader in = new BufferedReader(new FileReader(path + File.separator + OPONENTS_FILE));
            String line = "";

            do {
                try {
                    line = in.readLine();
                    OponentInfo op = new OponentInfo(line);
                    add(op, false);
                } catch (IllegalArgumentException ignored) {
                }
            } while (line != null);

            in.close();
        } catch (FileNotFoundException ex) {
            FileWriter f = new FileWriter(path + File.separator + OPONENTS_FILE);
            f.close();
            read();
        }
    }

    public Vector<OponentInfo> getOponents() {
        return this.oponents;
    }
}
