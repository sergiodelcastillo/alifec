package alifec.core.contest.oponentInfo;

import alifec.core.persistence.ContestConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class OpponentInfoManager {
    Logger logger = LogManager.getLogger(getClass());

    List<OpponentInfo> opponents = new ArrayList<>();


    private ContestConfig config;

    public OpponentInfoManager(ContestConfig config) {
        this.config = config;
    }

    public void add(String name, String author, String affiliation) throws IOException {
        OpponentInfo op = new OpponentInfo(name, author, affiliation);
        add(op, true);
    }

    void add(OpponentInfo op, boolean write) throws IOException {
        if (!opponents.contains(op)) {
            if (write) op.write(config.getCompetitorsFile());
            opponents.add(op);
        }
    }

    public void del(String name) throws IOException {
        for (OpponentInfo op : opponents) {
            if (op.contain(name)) {
                op.del(config.getCompetitorsFile());
                opponents.remove(op);

            }
        }
    }

    public void read() throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(config.getCompetitorsFile()));
        String line = "";

        do {
            try {
                line = in.readLine();
                OpponentInfo op = new OpponentInfo(line);
                add(op, false);
            } catch (IllegalArgumentException ex) {
                logger.warn("Ignore Opponent info line. Reason: " + ex.getMessage());
                logger.trace(ex);
            }
        } while (line != null);

        in.close();
    }

    public List<OpponentInfo> getOpponents() {
        return this.opponents;
    }
}
