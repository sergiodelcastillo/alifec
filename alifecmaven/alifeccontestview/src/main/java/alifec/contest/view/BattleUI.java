package alifec.contest.view;


import alifec.contest.simulationUI.GUIdosD;
import alifec.core.contest.Battle;
import alifec.core.contest.Contest;
import alifec.core.contest.Tournament;
import alifec.core.exception.CreateBattleException;
import alifec.core.persistence.config.ContestConfig;
import alifec.core.simulation.Environment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

public class BattleUI extends JPanel implements ActionListener {
    enum RunOption {
        ALL,
        SELECTED;
    }

    private static final long serialVersionUID = 0L;

    private static Logger logger = LogManager.getLogger(BattleUI.class);

    private final ContestUI father;

    private final Environment environment;
    private final DefaultListModel<Battle> model = new DefaultListModel<>();
    private final JList<Battle> battlesList = new JList<>(model);
    private JScrollPane battlesSP;
    private JComboBox<String> opponent1;
    private JComboBox<String> opponent2;
    private JComboBox<String> nutrient;
    private JButton save;
    private JButton delete;
    private JButton deleteAll;
    private JButton run;
    private JButton runAll;
    private JButton addSelected;
    private JButton addAll;

    private Hashtable<String, Integer> opponents, nutrients;

    private final Contest contest;
    private final ContestConfig config;

    public BattleUI(ContestUI contestUI, boolean restoreMissingRun) throws IOException {
        this.father = contestUI;
        this.environment = father.getContest().getEnvironment();
        this.contest = contestUI.getContest();
        this.config = contestUI.getContest().getConfig();

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Battles "));
        add(BorderLayout.NORTH, createNorthPanel());
        add(BorderLayout.CENTER, createCenterPanel());
        add(BorderLayout.SOUTH, createSouthPanel());

        if (restoreMissingRun) {

            for(Battle battle: contest.getMissingRunBattles()){
                addBattle(battle, config.isProgrammerMode());
            }
        }
    }

    private JPanel createNorthPanel() {
        JPanel northPanel = new JPanel();

        JLabel labelOp1 = new JLabel("Op.1");
        JLabel labelOp2 = new JLabel("Op.2");
        JLabel labelDist = new JLabel("Nutrients");

        opponents = environment.getOps();
        nutrients = father.getContest().getNutrients();

        opponent1 = new JComboBox<>(new MiComboboxModel(opponents));
        opponent2 = new JComboBox<>(new MiComboboxModel(opponents));
        nutrient = new JComboBox<>(new MiComboboxModel(nutrients));

        opponent1.setMinimumSize(new Dimension(100, 20));
        opponent2.setMinimumSize(new Dimension(100, 20));

        northPanel.add(labelOp1);
        northPanel.add(opponent1);
        northPanel.add(labelOp2);
        northPanel.add(opponent2);
        northPanel.add(labelDist);
        northPanel.add(nutrient);

        return northPanel;
    }

    private JMenuBar createSouthPanel() {
        JMenuBar menuBar = new JMenuBar();

        save = new JButton(new ImageIcon(getClass().getResource("/icons/save.png")));
        delete = new JButton(new ImageIcon(getClass().getResource("/icons/delSelectedBattle.png")));
        deleteAll = new JButton(new ImageIcon(getClass().getResource("/icons/delAllBattle.png")));
        run = new JButton(new ImageIcon(getClass().getResource("/icons/run.png")));
        runAll = new JButton(new ImageIcon(getClass().getResource("/icons/runAll.png")));
        addSelected = new JButton(new ImageIcon(getClass().getResource("/icons/addSelectedBattle.png")));
        addAll = new JButton(new ImageIcon(getClass().getResource("/icons/addAllBattle.png")));

        // set tool tip text !!
        save.setToolTipText("Save list of battles");
        delete.setToolTipText("Delete selected battle");
        deleteAll.setToolTipText("Clear all battles");
        run.setToolTipText("Run selected battle");
        runAll.setToolTipText("Run all battles");
        addSelected.setToolTipText("Add selected battle to list of battles");
        addAll.setToolTipText("Add all battles");

        // add listener
        save.addActionListener(this);
        delete.addActionListener(this);
        deleteAll.addActionListener(this);
        run.addActionListener(this);
        runAll.addActionListener(this);
        addSelected.addActionListener(this);
        addAll.addActionListener(this);

        // add to menu bar
        menuBar.add(save);
        menuBar.add(delete);
        menuBar.add(deleteAll);
        menuBar.add(run);
        menuBar.add(runAll);
        menuBar.add(addSelected);
        menuBar.add(addAll);

        return menuBar;
    }

    public void actionPerformed(ActionEvent e) {
        Tournament last = father.getContest().lastTournament();
        if (e.getSource().equals(save)) {
            Message.printErr(father, "Not Supported yet");
        } else if (e.getSource().equals(delete)) {
            if (model.size() == 0) {
                Message.printErr(father, "You must create a battle.");
            } else if (battlesList.isSelectionEmpty()) {
                Message.printErr(father, "You must select a battle.");
            } else {
                remove(battlesList.getSelectedValue());

                if (battlesList.getSelectedIndex() >= model.size())
                    battlesList.setSelectedIndex(model.size() - 1);
            }
        } else if (e.getSource().equals(deleteAll)) {
            if (model.size() > 0) {
                model.clear();
                battlesList.updateUI();
            } else
                Message.printErr(father, "You can´t delete battles, there are not battles");
        } else if (e.getSource().equals(run)) {
            if (!last.isEnabled()) {
                Message.printErr(father, "You must create a tournament.");
            } else if (model.size() == 0) {
                Message.printErr(father, "You must create a battle.");
            } else if (battlesList.isSelectionEmpty()) {
                Message.printErr(father, "You must select a battle.");
            } else {
                father.setMessage("Running Selected Battle");
                final DefaultListModel<Battle> model_tmp = new DefaultListModel<>();
                model_tmp.addElement(battlesList.getSelectedValue());

                createTargetRunFile(RunOption.SELECTED);
                //
                new GUIdosD(father, model_tmp);

                deleteTargetRunFile();
                remove(battlesList.getSelectedValue());

                if (battlesList.getSelectedIndex() >= model.size())
                    battlesList.setSelectedIndex(model.size() - 1);

                father.setMessage("Ok");
            }
        } else if (e.getSource().equals(runAll)) {
            if (!last.isEnabled()) {
                Message.printErr(father, "You must create a tournament.");
            } else if (model.size() == 0) {
                Message.printErr(father, "You must create a battle.");
            } else {
                father.setMessage("Running All Battles");
                createTargetRunFile(RunOption.ALL);
                new GUIdosD(father, model);
                deleteTargetRunFile();
                father.setMessage("Ok");
            }

        } else if (e.getSource().equals(addSelected)) {
            if (environment.getOps().isEmpty()) {
                Message.printErr(father, "You can´t create battles, there are not opponents");
                return;
            }
            try {
                int index1 = opponents.get(opponent1.getSelectedItem());
                int index2 = opponents.get(opponent2.getSelectedItem());
                int indexNut = nutrients.get(nutrient.getSelectedItem());
                String name1 = opponent1.getSelectedItem().toString();
                String name2 = opponent2.getSelectedItem().toString();
                String nameNut = nutrient.getSelectedItem().toString();

                Battle battle = new Battle(index1, index2, indexNut, name1, name2, nameNut);

                if (!addBattle(battle, father.getContest().getMode() == ContestConfig.PROGRAMMER_MODE))
                    Message.printErr(this, "Existing battle: " + battle.toString());
            } catch (CreateBattleException ex) {
                logger.error(ex.getMessage(), ex);
                Message.printErr(father, ex.getMessage());
            } catch (NullPointerException ex) {
                logger.info(ex.getMessage(), ex);
                Message.printErr(this, "List of Nutrient or Opponents are empty.");
            }

        } else if (e.getSource().equals(addAll)) {
            if (environment.getOps().isEmpty())
                Message.printErr(father, "You can´t create battles, there are not oponentes");

            else
                generateAllBattle();
        }
    }

    private JScrollPane createCenterPanel() {
        battlesSP = new JScrollPane(battlesList,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        battlesSP.setPreferredSize(new Dimension(200, 200));
        return battlesSP;
    }

    /**
     * @param b              information of battle
     * @param programmerMode mode of contest, can be PROGRAMMER or COMPETITION
     * @return true if is successfully
     */
    private boolean addBattle(Battle b, boolean programmerMode) {
        Tournament last = father.getContest().lastTournament();

        boolean addOK = last.isEnabled(); // to be sure

        if (programmerMode) {
            if (model.contains(b)) {
                addOK &= Message.printYesNoCancel(father, "Existing battle: " + b.toString() + ". Are you sure you want to create a battle.");
            }
        } else {
            if (model.contains(b)) { // battle existing
                addOK = false;
            }

        }

        if (addOK) {
            model.addElement(b);
            battlesList.updateUI();
            battlesList.setSelectedIndex(0);
        }
        return addOK;
    }

    private void generateAllBattle() {
        boolean existingBattle = false;
        boolean option = father.getContest().getMode() == ContestConfig.PROGRAMMER_MODE;
        List<Battle> list = contest.lastTournament().generateAllBattles(opponents, nutrients, option);

        for(Battle battle: list){
        if (!addBattle(battle, option)) {
            existingBattle = true;
        }

        if(list.size() == 0 && existingBattle)
            Message.printErr(this, "Battle/s already run.");
        }
    }

    public DefaultListModel<Battle> getBattles() {
        return model;
    }

    @Override
    public synchronized void repaint() {
        try {
            super.repaint();
            if (nutrient != null)
                this.nutrient.updateUI();
            if (opponent1 != null)
                this.opponent1.updateUI();
            if (opponent2 != null)
                this.opponent2.updateUI();
        } catch (Exception ignored) {
            logger.trace(ignored.getMessage(), ignored);
        }
    }

    public void updateNutrients() {
        nutrients = father.getContest().getNutrients();

        nutrient.setModel(new MiComboboxModel(nutrients));
        nutrient.updateUI();
    }

    public boolean delete(String colonyName) {
        List<Battle> indexes = new ArrayList<>();

        for (int i = 0; i < getBattles().size(); i++) {
            Battle b = getBattles().elementAt(i);

            if (b.getFirstColony().equals(colonyName) || b.getSecondColony().equals(colonyName))
                indexes.add(b);
        }

        for (Battle b : indexes)
            getBattles().removeElement(b);

        return ((MiComboboxModel) opponent1.getModel()).remove(colonyName) &&
                ((MiComboboxModel) opponent2.getModel()).remove(colonyName);
    }

    public void clear() {
        opponents = environment.getOps();
        nutrients = father.getContest().getNutrients();
        opponent1.setModel(new MiComboboxModel(opponents));
        opponent2.setModel(new MiComboboxModel(opponents));
        nutrient.setModel(new MiComboboxModel(nutrients));

        model.clear();
        battlesList.updateUI();
    }

    private void remove(Battle b) {
        model.removeElement(b);
        battlesSP.updateUI();
    }

    private void createTargetRunFile(RunOption mode) {
        if (config.isProgrammerMode()) return;

        try {
            switch (mode) {
                case ALL:
                    contest.lastTournament().saveTargetRun(Collections.list(model.elements()));
                    break;
                case SELECTED:
                    contest.lastTournament().saveTargetRun(battlesList.getSelectedValuesList());
                    break;
            }

            logger.info("Creating target run file [OK]");

        } catch (IOException ex) {
            logger.error("Creating back up [FAIL]");
            logger.error(ex.getMessage(), ex);
        }

    }


    private void deleteTargetRunFile() {
        try {
            if (config.isProgrammerMode()) {
                contest.lastTournament().deleteTargetRunFile();
                logger.info("Removing target run file [OK]");
            }
        } catch (Throwable t) {
            logger.warn("Removing target run file [FAIL]");
            logger.error(t.getMessage(), t);
        }
    }


    public void setEnabled(boolean b) {
        battlesList.setEnabled(b);
        //	private JScrollPane battlesSP;
        opponent1.setEnabled(b);
        opponent2.setEnabled(b);
        nutrient.setEnabled(b);
        save.setEnabled(b);
        delete.setEnabled(b);
        deleteAll.setEnabled(b);
        run.setEnabled(b);
        runAll.setEnabled(b);
        addSelected.setEnabled(b);
        addAll.setEnabled(b);
        father.getContest().lastTournament().setEnabled(b);
    }
}


