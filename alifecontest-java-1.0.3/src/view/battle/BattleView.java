/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

package view.battle;

import controller.java.Environment;
import controller.java.battle.BattleRun;
import controller.java.contest.ContestMode;
import controller.java.tournament.Tournament;
import view.Properties;
import view.contest.ContestView;
import view.contest.Message;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class BattleView extends JPanel {
    private static final long serialVersionUID = 0L;

    private final ContestView father;

    private final Environment environment;

    private final BattleListView battles = new BattleListView(new DefaultListModel());

    private JComboBox opponent1;
    private JComboBox opponent2;
    private JComboBox nutrient;

    private JButton addSaved;
    private JButton save;
    private JButton delete;
    private JButton deleteAll;
    private JButton run;
    private JButton runAll;
    private JButton addSelected;
  //  private JButton addAll;

    public BattleView(ContestView contestView) {
        this.father = contestView;
        this.environment = father.getContest().getEnvironment();

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Battles "));
        add(BorderLayout.NORTH, createNorthPanel());
        add(BorderLayout.CENTER, createCenterPanel());
        add(BorderLayout.SOUTH, createSouthPanel());
    }

    public BattleView(ContestView contestView, ArrayList<BattleRun> toRestore) {
        this(contestView);

        if (toRestore != null) {
            addBattles(toRestore);
        }
    }


    private JPanel createNorthPanel() {
        JPanel northPanel = new JPanel();

        JLabel labelOp1 = new JLabel("Op.1");
        JLabel labelOp2 = new JLabel("Op.2");
        JLabel labelDist = new JLabel("Nutrients");


        opponent1 = new JComboBox(new BattleComboBoxModel(environment.getColonies()));
        opponent2 = new JComboBox(new BattleComboBoxModel(environment.getColonies()));
        nutrient = new JComboBox(new BattleComboBoxModel(environment.getAgar().getNutrients()));

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

        addSaved = new JButton(new ImageIcon("icons/openColony.png"));
        save = new JButton(new ImageIcon(Properties.getInstance().getSaveIcon()));
        delete = new JButton(new ImageIcon(Properties.getInstance().getDelSelectedBattleIcon()));
        deleteAll = new JButton(new ImageIcon(Properties.getInstance().getDelAllBattleIcon()));
        run = new JButton(new ImageIcon(Properties.getInstance().getRunSelectedBattleIcon()));
        runAll = new JButton(new ImageIcon(Properties.getInstance().getRunAllBattleIcon()));
        addSelected = new JButton(new ImageIcon(Properties.getInstance().getAddSelectedBattleIcon()));
     //   addAll = new JButton(new ImageIcon(Properties.getInstance().getAddAllBattleIcon()));

        // eat tool tip text !!
        addSaved.setToolTipText("Add colony");
        save.setToolTipText("Save list of battle");
        delete.setToolTipText("Delete selected battle");
        deleteAll.setToolTipText("Clear all battle");
        run.setToolTipText("Run selected battle");
        runAll.setToolTipText("Run all battle");
        addSelected.setToolTipText("Add selected battle to list of battle");
     //   addAll.setToolTipText("Add all battle");

        // append listener
        addSaved.addActionListener(new BattleHandler(this));
        save.addActionListener(new BattleHandler(this));
        delete.addActionListener(new BattleHandler(this));
        deleteAll.addActionListener(new BattleHandler(this));
        run.addActionListener(new BattleHandler(this));
        runAll.addActionListener(new BattleHandler(this));
        addSelected.addActionListener(new BattleHandler(this));
     //   addAll.addActionListener(new BattleHandler(this));

        // append to menu bar
        menuBar.add(addSaved);
        menuBar.add(save);
        menuBar.add(delete);
        menuBar.add(deleteAll);
        menuBar.add(run);
        menuBar.add(runAll);
        menuBar.add(addSelected);
  //      menuBar.add(addAll);

        return menuBar;
    }


    private JScrollPane createCenterPanel() {
        JScrollPane battlesSP = new JScrollPane(battles,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        battlesSP.setPreferredSize(new Dimension(200, 200));
        return battlesSP;
    }

    public void filterBattles(ArrayList<BattleRun> list, int mode) {
        switch (mode) {
            case ContestMode.PROGRAMMER_MODE:
                boolean duplicated = false;

                for (BattleRun b : list) {
                    if (battles.getModel().contains(b)) {
                        duplicated = true;
                        break;
                    }
                }

                if (duplicated) {
                    String txt = "There are existing battles. Are you sure you want to create duplicated battles?.";

                    switch (Message.printYesNoCancel(father, txt)) {
                        case JOptionPane.CANCEL_OPTION:
                            list.clear();
                            return;
                        case JOptionPane.OK_OPTION:
                            return;
                        case JOptionPane.NO_OPTION:
                            deleteDuplicatedBattles(list);
                            return;
                    }
                }
                break;
            case ContestMode.COMPETITION_MODE:
                deleteDuplicatedBattles(list);
                deleteExecutedBattles(list);
                break;
        }
    }

    private void deleteDuplicatedBattles(ArrayList<BattleRun> list) {
        ArrayList<BattleRun> toDelete = new ArrayList<BattleRun>();

        for (BattleRun b : list) {
            if (battles.getModel().contains(b)) {
                toDelete.add(b);
            }
        }
        for (BattleRun b : toDelete) {
            list.remove(b);
        }
    }

    private void deleteExecutedBattles(ArrayList<BattleRun> list) {
        Tournament last = father.getContest().getTournamentManager().getLastTournament();

        ArrayList<BattleRun> toDelete = new ArrayList<BattleRun>();

        for (BattleRun b : list) {
            if (last.getBattleManager().contain(b)) {
                toDelete.add(b);
            }
        }
        for (BattleRun b : toDelete) {
            list.remove(b);
        }
    }


    public void addBattles(final ArrayList<BattleRun> list) {
        if (list.isEmpty()) {
            return;
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                for (BattleRun b : list) {
                    battles.getModel().addElement(b);
                }
            }
        });
    }

    public BattleListView getBattleList() {
        return battles;
    }

  /*  @Override
    public void repaint() {
        try {
            super.repaint();
            this.nutrient.updateUI();
            this.opponent1.updateUI();
            this.opponent2.updateUI();
            this.battles.updateUI();
        } catch (Exception ignored) {
        }
    }     */

    public void updateNutrients() {
        nutrient.setModel(new BattleComboBoxModel(environment.getAgar().getNutrients()));
    }

    public void deleteColony(String colonyName) {
        ArrayList<BattleRun> indexes = new ArrayList<BattleRun>();

        for (int i = 0; i < battles.getModel().size(); i++) {
            BattleRun b = (BattleRun) battles.getModel().elementAt(i);

            if (b.contain(colonyName)) {
                indexes.add(b);
            }
        }

        for (BattleRun b : indexes) {
            battles.getModel().removeElement(b);
        }

        update();
    }


    public void setEnabled(boolean b) {
        battles.setEnabled(b);
        opponent1.setEnabled(b);
        opponent2.setEnabled(b);
        nutrient.setEnabled(b);
        save.setEnabled(b);
        delete.setEnabled(b);
        deleteAll.setEnabled(b);
        run.setEnabled(b);
        runAll.setEnabled(b);
        addSelected.setEnabled(b);
     //   addAll.setEnabled(b);
        father.getContest().getTournamentManager().getLastTournament().setEnabled(b);
    }

    public ContestView getFather() {
        return this.father;
    }

    public int getSelectedFirstColony() {
        return opponent1.getSelectedIndex();
    }

    public int getSelectedSecondColony() {
        return opponent2.getSelectedIndex();
    }

    public int getSelectedNutrient() {
        return nutrient.getSelectedIndex();
    }

    public boolean isSave(Object obj) {
        return this.save.equals(obj);
    }

    public boolean isDelete(Object obj) {
        return this.delete.equals(obj);
    }

    public boolean isDeleteAll(Object obj) {
        return this.deleteAll.equals(obj);
    }

    public boolean isRun(Object obj) {
        return this.run.equals(obj);
    }

    public boolean isRunAll(Object obj) {
        return this.runAll.equals(obj);
    }

    public boolean isAddSelected(Object obj) {
        return this.addSelected.equals(obj);
    }

    /*public boolean isAddAll(Object obj) {
        return this.addAll.equals(obj);
    }
        */
    public boolean isAddSaved(Object obj) {
        return this.addSaved.equals(obj);
    }

    public BattleComboBoxModel getFirstOpponentList() {
        return (BattleComboBoxModel) opponent1.getModel();
    }

    public BattleComboBoxModel getSecondOpponentList() {
        return (BattleComboBoxModel) opponent2.getModel();
    }

    public BattleComboBoxModel getNutrientList() {
        return (BattleComboBoxModel) nutrient.getModel();
    }

    public void update() {
        opponent1.setModel(new BattleComboBoxModel(environment.getColonies()));
        opponent2.setModel(new BattleComboBoxModel(environment.getColonies()));
        nutrient.setModel(new BattleComboBoxModel(environment.getAgar().getNutrients()));
    }

}