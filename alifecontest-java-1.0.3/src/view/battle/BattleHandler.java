package view.battle;

import controller.java.battle.BattleRun;
import controller.java.tournament.Tournament;
import view.contest.ContestView;
import view.contest.Message;
import view.simulation.BattleInvoker;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Sergio Del Castillo
 * User: yeyo
 * Date: Jun 20, 2010
 * Time: 7:21:34 PM
 * Email: yeyo@druidalabs.com
 */
public class BattleHandler implements ActionListener {

    private BattleView view;
    private ContestView father;

    public BattleHandler(BattleView view) {
        this.view = view;
        this.father = view.getFather();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (view.isAddSaved(e.getSource())) {
            Message.printErr(father, "Not Supported yet");

        } else if (view.isSave(e.getSource())) {
            Message.printErr(father, "Not Supported yet");

        } else if (view.isDelete(e.getSource())) {
            handleDelete();

        } else if (view.isDeleteAll(e.getSource())) {
            handleDeleteAll();

        } else if (view.isRun(e.getSource())) {
            handleRun();

        } else if (view.isRunAll(e.getSource())) {
            handleRunAll();

        } else if (view.isAddSelected(e.getSource())) {
            handleAddSelected();

        }
    }


    private void handleAddSelected() {
        int first = view.getSelectedFirstColony();
        int second = view.getSelectedSecondColony();
        int nutrient = view.getSelectedNutrient();

        if (first == second && first != 0) {
            Message.printErr(view.getFather(), "Opponent 1 and 2 can not be the equals.");
            return;
        }

        ArrayList<Integer> firstId = new ArrayList<Integer>();
        ArrayList<Integer> secondId = new ArrayList<Integer>();
        ArrayList<Integer> nutrientId = new ArrayList<Integer>();
        ArrayList<String> firstName = new ArrayList<String>();
        ArrayList<String> secondName = new ArrayList<String>();
        ArrayList<String> nutrientName = new ArrayList<String>();

        addToList(firstId, firstName, first, view.getFirstOpponentList());
        addToList(secondId, secondName, second, view.getSecondOpponentList());
        addToList(nutrientId, nutrientName, nutrient, view.getNutrientList());

        ArrayList<BattleRun> battles = new ArrayList<BattleRun>();

        for (int i = 0; i < firstId.size(); i++) {
            for (int j = 0; j < secondId.size(); j++) {
                for (int k = 0; k < nutrientId.size(); k++) {
                    BattleRun battle = new BattleRun();

                    battle.setFirstOpponent(firstName.get(i), firstId.get(i));
                    battle.setSecondOpponent(secondName.get(j), secondId.get(j));
                    battle.setNutrient(nutrientName.get(k), nutrientId.get(k));

                    if (battle.valid() && !battles.contains(battle)) {
                        battles.add(battle);
                    }
                }
            }
        }

        view.filterBattles(battles, father.getContest().getMode());
        Collections.shuffle(battles);
        view.addBattles(battles);
    }

    private void addToList(ArrayList<Integer> ids, ArrayList<String> names, int selected, BattleComboBoxModel model) {
        if (model.getId(selected) == -1) {
            for (int i = 0; i < model.getSize(); i++) {
                if (model.getId(i) == -1) continue;

                names.add(model.getName(i));
                ids.add(model.getId(i));
            }
        } else {
            ids.add(model.getId(selected));
            names.add(model.getName(selected));
        }
    }


    private void handleRunAll() {
        if (canRun(false)) {
            BattleInvoker.invoke(father, false);
        }
    }

    private void handleRun() {
        if (canRun(true)) {
            BattleInvoker.invoke(father, true);
        }
    }

    private boolean canRun(boolean isRunSelected) {
        Tournament last = father.getContest().getTournamentManager().getLastTournament();

        if (!last.isEnabled()) {
            Message.printErr(father, "You must create a tournament.");
        } else if (view.getBattleList().getModel().size() == 0) {
            Message.printErr(father, "You must create a battle.");
            return false;
        } else if (isRunSelected && view.getBattleList().isSelectionEmpty()) {
            Message.printErr(father, "You must select a battle.");
            return false;
        }

        return true;
    }

    private void handleDeleteAll() {
        if (view.getBattleList().getModel().size() > 0) {
            view.getBattleList().getModel().clear();

            //TODO: ver si es necesario.
            view.getBattleList().updateUI();
        }
    }

    private void handleDelete() {
        if (view.getBattleList().getModel().size() == 0) {
            Message.printErr(father, "You must create a battle.");
        } else if (view.getBattleList().isSelectionEmpty()) {
            Message.printErr(father, "You must select a battle.");
        } else {

            view.getBattleList().getModel().remove(view.getBattleList().getSelectedIndex());
            //TODO: ver si es necesario.
            view.getBattleList().updateUI();
        }
    }
}
