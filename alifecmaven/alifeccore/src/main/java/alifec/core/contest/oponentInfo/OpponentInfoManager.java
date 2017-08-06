/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

package alifec.core.contest.oponentInfo;

import java.io.*;
import java.util.Vector;


public class OpponentInfoManager {
    Vector<OpponentInfo> opponents = new Vector<>();
    private static final String OPPONENTS_FILE = "competitors";
    private String path = "";

    public OpponentInfoManager(String path) {
        this.path = path;
    }

    public void add(String name, String author, String affiliation) throws IOException {
        OpponentInfo op = new OpponentInfo(name, author, affiliation);
        add(op, true);

    }

    void add(OpponentInfo op, boolean write) throws IOException {
        if (!opponents.contains(op)) {
            if (write) op.write(path + File.separator + OPPONENTS_FILE);
            opponents.addElement(op);
        }
    }

    public void del(String name) throws IOException {
        for (OpponentInfo op : opponents) {
            if (op.contain(name)) {
                op.del(path + File.separator + OPPONENTS_FILE);
                opponents.remove(op);

            }
        }
    }

    public void read() throws IOException {
        try {
            BufferedReader in = new BufferedReader(new FileReader(path + File.separator + OPPONENTS_FILE));
            String line = "";

            do {
                try {
                    line = in.readLine();
                    OpponentInfo op = new OpponentInfo(line);
                    add(op, false);
                } catch (IllegalArgumentException ignored) {
                }
            } while (line != null);

            in.close();
        } catch (FileNotFoundException ex) {
            FileWriter f = new FileWriter(path + File.separator + OPPONENTS_FILE);
            f.close();
            read();
        }
    }

    public Vector<OpponentInfo> getOpponents() {
        return this.opponents;
    }
}
