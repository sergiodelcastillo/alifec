/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

package alifec.core.contest.oponentInfo;

import alifec.core.contest.ContestConfig;

import java.io.*;
import java.util.ArrayList;
import java.util.List;



public class OpponentInfoManager {
    List<OpponentInfo> opponents = new ArrayList<>();

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
            //todo: use ContestConfig
            if (write) op.write(path + File.separator + ContestConfig.REPORT_OPPONENTS_FILE);
            opponents.add(op);
        }
    }

    public void del(String name) throws IOException {
        for (OpponentInfo op : opponents) {
            if (op.contain(name)) {
                //todo: use ContestConfig
                op.del(path + File.separator + ContestConfig.REPORT_OPPONENTS_FILE);
                opponents.remove(op);

            }
        }
    }

    public void read() throws IOException {
        try {
            //todo: use ContestConfig
            BufferedReader in = new BufferedReader(new FileReader(path + File.separator + ContestConfig.REPORT_OPPONENTS_FILE));
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
            //todo: use ContestConfig
            FileWriter f = new FileWriter(path + File.separator + ContestConfig.REPORT_OPPONENTS_FILE);
            f.close();
            read();
        }
    }

    public List<OpponentInfo> getOpponents() {
        return this.opponents;
    }
}
