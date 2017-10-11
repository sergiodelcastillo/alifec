package controller.java.oponentInfo;
/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */


import controller.java.Colony;
import controller.java.contest.ContestMode;
import data.java.Config;
import data.java.Log;
import data.java.contest.RankingDTO;
import exceptions.CreateOpponentInfoException;
import exceptions.LoadOpponentInfoException;
import exceptions.SaveOpponentInfoException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class OpponentInfoManager {

    private ArrayList<OpponentInfo> info;

    public OpponentInfoManager() throws CreateOpponentInfoException {
        try {
            info = new ArrayList<OpponentInfo>();

            File ops = new File(Config.getInstance().getAbsoluteInfo());

            if (!ops.exists()) {
                ops.getParentFile().mkdirs();
                ops.createNewFile();
            }
        } catch (IOException e) {
            throw new CreateOpponentInfoException("Cant create the Opponent File", e);
        }
    }

    public void add(ArrayList<Colony> colonies, int mode) throws SaveOpponentInfoException {
        try {
            for (Colony c : colonies) {
                try {
                    OpponentInfo op = new OpponentInfo(c.getName(), c.getAuthor(), c.getAffiliation());

                    if (!this.info.contains(op)) {
                        this.info.add(op);

                        if (mode == ContestMode.COMPETITION_MODE) {
                            op.save(Config.getInstance().getAbsoluteInfo());
                        }
                    }
                } catch (IllegalArgumentException ex) {
                    Log.printlnAndSave("Illegal Opponent Information", ex);
                }
            }
        } catch (Exception ex) {
            throw new SaveOpponentInfoException("Cant save the Opponent info", ex);
        }
    }


    /**
     * Delete the colony <b> name </b> of the opponent file.
     *
     * @param name the name of the colony
     * @throws IOException
     */
    public void del(String name) throws IOException {
        ArrayList<OpponentInfo> toDel = new ArrayList<OpponentInfo>();

        for (OpponentInfo op : info) {
            if (op.contain(name)) {
                op.del(Config.getInstance().getAbsoluteInfo());
                toDel.add(op);
            }
        }

        for (OpponentInfo del : toDel) {
            info.remove(del);
        }
    }

    /**
     * load the opponent details of the file of opponents.
     *
     * @throws IOException
     */
    public void load() throws LoadOpponentInfoException {
        try {
            BufferedReader in = new BufferedReader(new FileReader(Config.getInstance().getAbsoluteInfo()));
            String line;

            while ((line = in.readLine()) != null) {
                try {
                    OpponentInfo op = new OpponentInfo(line);

                    if (!info.contains(op)) {
                        info.add(op);
                    }
                } catch (IllegalArgumentException ignored) {
                    Log.println("Bad line in opponents file (it will be ignored): " + line);
                }
            }

            in.close();
        } catch (Exception ex) {
            throw new LoadOpponentInfoException("Cant load the opponent info", ex);
        }
    }

    /**
     * @return ArrayList with opponent details
     */
    public ArrayList<OpponentInfo> getInfo() {
        return this.info;
    }

    public void addInfo(ArrayList<RankingDTO> ranking) {
        for (RankingDTO r : ranking) {
            addInfo(r);
        }
    }

    private void addInfo(RankingDTO ranking) {
        for (OpponentInfo info : this.info) {
            if (info.name.equals(ranking.getName())) {
                ranking.setAff(info.affiliation);
                ranking.setAuthor(info.author);
                return;
            }
        }
    }
}
