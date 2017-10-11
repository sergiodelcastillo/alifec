package view.simulation;

import controller.java.battle.BattleRun;
import data.java.Log;
import view.contest.ContestView;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Sergio Del Castillo
 * User: yeyo
 * Date: Jun 18, 2010
 * Time: 3:12:37 PM
 * Email: yeyo@druidalabs.com
 */
public class BattleInvoker {
    private static enum Simulator {
        BUFFERED_RUNNER,
        OPEN_GL,
        NOTHING
    }

    private static Simulator getSimulator() {
        // todo agregar un property.
        return Simulator.BUFFERED_RUNNER;
    }

    public static void invoke(ContestView father, boolean selected) {
        if (selected) {
            father.setMessage("Running selected battle.");
            saveSelectedBattle(father);
        } else {
            father.setMessage("Running all battles.");
            saveAllBattles(father);
        }

        switch (getSimulator()) {
            case BUFFERED_RUNNER:
                Date ini = new Date();
                new BufferedRunner(father, selected);
                Date end = new Date();
                System.out.println("Time=" + (end.getTime() - ini.getTime()));
                break;
            case OPEN_GL:
                System.out.println("todo");
                break;
            case NOTHING:
                System.out.println("todo");
                break;
        }

        deleteBackUp(father);
        father.setMessage("Ok.");

    }

    private static void deleteBackUp(ContestView father) {
        father.getContest().getTournamentManager().getLastTournament().deleteBackUp();
    }

    private static void saveAllBattles(ContestView father) {
        DefaultListModel list = father.getBattleView().getBattleList().getModel();
        ArrayList<BattleRun> backup = new ArrayList<BattleRun>();

        for (int i = 0; i < list.size(); i++) {
            backup.add((BattleRun) list.get(i));
        }

        try {
            father.getContest().getTournamentManager().getLastTournament().saveBackUp(backup);
        } catch (IOException e) {
            Log.printlnAndSave("", e);
        }
    }

    private static void saveSelectedBattle(ContestView father) {
        BattleRun sel = (BattleRun) father.getBattleView().getBattleList().getSelectedValue();

        ArrayList<BattleRun> backup = new ArrayList<BattleRun>();

        backup.add(sel);
        try {
            father.getContest().getTournamentManager().getLastTournament().saveBackUp(backup);
        } catch (IOException e) {
            Log.printlnAndSave("", e);
        }
    }
}
