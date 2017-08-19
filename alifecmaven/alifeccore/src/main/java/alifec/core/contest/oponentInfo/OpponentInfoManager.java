/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

package alifec.core.contest.oponentInfo;

import alifec.core.contest.ContestConfig;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;



public class OpponentInfoManager {
    Logger logger = Logger.getLogger(getClass());

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
                } catch (IllegalArgumentException ex) {
                    logger.error(ex.getMessage(), ex);
                }
            } while (line != null);

            in.close();
        } catch (FileNotFoundException ex) {
            logger.error(ex.getMessage(), ex);
            //todo: use ContestConfig
            //TODO: mepa que esto está al dope
            FileWriter f = new FileWriter(path + File.separator + ContestConfig.REPORT_OPPONENTS_FILE);
            f.close();
            read();
        }
    }

    public List<OpponentInfo> getOpponents() {
        return this.opponents;
    }
}
