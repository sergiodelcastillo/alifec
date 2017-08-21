/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

package alifec.contest.view;


import alifec.contest.simulationUI.GUIdosD;
import alifec.core.contest.Contest;
import alifec.core.contest.ContestConfig;
import alifec.core.contest.tournament.Tournament;
import alifec.core.contest.tournament.battles.BattleManager;
import alifec.core.contest.tournament.battles.BattleRun;
import alifec.core.exception.CreateBattleException;
import alifec.core.simulation.Environment;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

public class BattleUI extends JPanel implements ActionListener {
    private static final long serialVersionUID = 0L;

    private static Logger logger = Logger.getLogger(BattleUI.class);

    private final ContestUI father;

    private final Environment environment;
    private final DefaultListModel<BattleRun> model = new DefaultListModel<>();
    private final JList<BattleRun> battlesList = new JList<>(model);
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

    public BattleUI(ContestUI contestUI, boolean restore) throws IOException {
        this.father = contestUI;
        this.environment = father.getContest().getEnvironment();
        this.contest = contestUI.getContest();
        this.config = contestUI.getContest().getConfig();

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Battles "));
        add(BorderLayout.NORTH, createNorthPanel());
        add(BorderLayout.CENTER, createCenterPanel());
        add(BorderLayout.SOUTH, createSouthPanel());

        if (restore) {
            String lastTournament = getLastTournamentName();
            List<String[]> battles = readBattles(config.getBattlesFile(lastTournament));
            List<String[]> backup = readBattles(config.getBattlesBackupFile(lastTournament));

            validate(battles, backup);
            restoreBattles(backup, config.getBattlesBackupFile(lastTournament));
        }
    }

    private void restoreBattles(List<String[]> backup, String file) {
        if (backup.size() == 0) {
            new File(file).delete();
            return;
        }
        for (String[] line : backup) {
            try {
                String name1 = line[0], name2 = line[1], nameNut = line[2];
                int index1 = environment.getColonyID(name1);
                int index2 = environment.getColonyID(name2);
                int indexNut = environment.getAgar().getNutrientID(nameNut);

                BattleRun battle = new BattleRun(index1, index2, indexNut, name1, name2, nameNut);

                addBattle(battle, config.isProgrammerMode());
            } catch (CreateBattleException ex) {
                logger.error(ex.getMessage(), ex);
                Message.printErr(father, ex.getMessage());
            }
        }// end for!!
    }

    // 0: oponente 1;
    //	1: oponente 2;
    // 2: distribucion de nutrientes

    private void validate(List<String[]> battles, List<String[]> backup) {
        // Eliminar las batallas que ya se corrieron
        List<String[]> todelete = new ArrayList<>();
        for (String[] line : battles) {
            for (String[] line_backup : backup) {
                if ((line[0].toLowerCase().equals(line_backup[0].toLowerCase()) &&
                        line[1].toLowerCase().equals(line_backup[1].toLowerCase()) &&
                        line[2].toLowerCase().equals(line_backup[2].toLowerCase())) ||
                        (line[0].toLowerCase().equals(line_backup[1].toLowerCase()) &&
                                line[1].toLowerCase().equals(line_backup[0].toLowerCase()) &&
                                line[2].toLowerCase().equals(line_backup[2].toLowerCase()))) {
                    todelete.add(line_backup);
                }
            }
        }
        // Eliminar las batallas con distribuciones de nutrientes que no están disponibles.
        List<String> nutrients = environment.getAgar().getNutrients();

        for (String[] l : backup) {
            boolean b = false;
            for (String n : nutrients) {
                if (l[2].toLowerCase().equals(n.toLowerCase())) {
                    b = true;
                }
            }
            if (!b) {
                todelete.add(l);
            }
        }

        // eliminar las batallas con Colonias que no estan disponibles.
        List<String> colonies = environment.getNames();

        for (String[] l : backup) {
            int b = 0;
            for (String c : colonies) {
                if (l[0].toLowerCase().equals(c.toLowerCase()))
                    b++;
                else if (l[1].toLowerCase().equals(c.toLowerCase()))
                    b++;
            }
            if (b != 2) {
                todelete.add(l);
            }
        }

        backup.removeAll(todelete);
    }

    private List<String[]> readBattles(String path) {
        List<String[]> res = new ArrayList<>();

        try {
            FileReader fr = new FileReader(path);
            BufferedReader br = new BufferedReader(fr);
            String line;

            while ((line = br.readLine()) != null) {
                String[] s = line.split(",");

                if (3 <= s.length) {
                    res.add(new String[]{s[0], s[1], s[2]});
                }
            }

            br.close();

        } catch (IOException ignored) {
            logger.trace(ignored.getMessage());
            res.clear();
        }

        return res;
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
        Tournament last = father.getContest().getTournamentManager().lastElement();
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
                final DefaultListModel<BattleRun> model_tmp = new DefaultListModel<>();
                model_tmp.addElement(battlesList.getSelectedValue());

                createBattlesFileSelected();
                //
                new GUIdosD(father, model_tmp);

                deleteBattlesFile();
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
                createBattlesFileAll();
                new GUIdosD(father, model);
                deleteBattlesFile();
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

                BattleRun battle = new BattleRun(index1, index2, indexNut, name1, name2, nameNut);

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
    private boolean addBattle(BattleRun b, boolean programmerMode) {
        Tournament last = father.getContest().getTournamentManager().lastElement();
        BattleManager bm = last.getBattleManager();

        boolean addOK = last.isEnabled(); // to be sure

        if (programmerMode) {
            if (model.contains(b)) {
                addOK &= Message.printYesNoCancel(father, "Existing battle: " + b.toString() + ". Are you sure you want to create a battle.");
            }
        } else {
            if (model.contains(b)) { // battle existing
                addOK = false;
            }
            if (bm.contain(b)) { // battle was run
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

        for (Enumeration<String> op_a = opponents.keys(); op_a.hasMoreElements(); ) {
            String n_a = op_a.nextElement(); // name_oponent_a
            Integer i_a = opponents.get(n_a); // index_oponent_a

            for (Enumeration<String> op_b = opponents.keys(); op_b.hasMoreElements(); ) {
                String n_b = op_b.nextElement(); // name_oponent_b
                Integer i_b = opponents.get(n_b); // index_oponent_b

                if (i_a >= i_b) continue;

                for (Enumeration<String> nut = nutrients.keys(); nut.hasMoreElements(); ) {
                    String n_n = nut.nextElement();      // name_nutrient
                    Integer i_n = nutrients.get(n_n);    // index_nutrient

                    try {
                        boolean option = father.getContest().getMode() == ContestConfig.PROGRAMMER_MODE;

                        if (!addBattle(new BattleRun(i_a, i_b, i_n, n_a, n_b, n_n), option)) {
                            existingBattle = true;
                        }
                    } catch (CreateBattleException ex) {
                        logger.error(ex.getMessage(), ex);
                    }
                }
            }
        }
        if (existingBattle)
            Message.printErr(this, "There are existing battles.");
    }

    public DefaultListModel<BattleRun> getBattles() {
        return model;
    }

    @Override
    public synchronized void repaint() {
        try {
            super.repaint();
            this.nutrient.updateUI();
            this.opponent1.updateUI();
            this.opponent2.updateUI();
        } catch (Exception ignored) {
            logger.trace(ignored.getMessage());
        }
    }

    public void updateNutrients() {
        nutrients = father.getContest().getNutrients();

        nutrient.setModel(new MiComboboxModel(nutrients));
        nutrient.updateUI();
    }

    public boolean delete(String colonyName) {
        List<BattleRun> indexes = new ArrayList<>();

        for (int i = 0; i < getBattles().size(); i++) {
            BattleRun b = getBattles().elementAt(i);

            if (b.name1.equals(colonyName) || b.name2.equals(colonyName))
                indexes.add(b);
        }

        for (BattleRun b : indexes)
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

    private void remove(BattleRun b) {
        model.removeElement(b);
        battlesSP.updateUI();
    }

    private void createBattlesFileSelected() {
        if (config.isProgrammerMode()) return;
//TODO: ver en todos lados se llama battle_backup.
        String backupFile = getLastTournamentBattlesBackupFile();

        try {
            FileWriter fr = new FileWriter(backupFile);
            PrintWriter pw = new PrintWriter(fr);
            String line = battlesList.getSelectedValue().toString();
            line = line.replace(" vs ", ",");
            line = line.replace(" in ", ",");
            pw.println(line);
            pw.close();

            logger.info("Creating back up [OK]");

        } catch (IOException ex) {
            logger.error("Creating back up [FAIL]", ex);
        }

    }

    private String getLastTournamentBattlesBackupFile() {
        return config.getBattlesBackupFile(getLastTournamentName());
    }

    private String getLastTournamentName() {
        return contest.getTournamentManager().lastElement().getName();
    }

    private boolean deleteBattlesFile() {
        if (config.isProgrammerMode()) return true;

//TODO: ver en todos lados se llama battle_backup.
        if (new File(getLastTournamentBattlesBackupFile()).delete()) {
            logger.info("Removing back up [OK]");
            return true;
        } else {
            logger.warn("Removing back up [FAIL]");
            return false;
        }
    }

    private void createBattlesFileAll() {
        if (config.isProgrammerMode()) return;

        //TODO: ver en todos lados se llama battle_backup.
        String backupFile = getLastTournamentBattlesBackupFile();


        try {
            String line;
            FileWriter fr = new FileWriter(backupFile);
            PrintWriter pw = new PrintWriter(fr);

            for (int i = 0; i < model.getSize(); i++) {
                line = model.getElementAt(i).toString();
                line = line.replace(" vs ", ",");
                line = line.replace(" in ", ",");
                pw.println(line);
            }

            pw.close();
            logger.info("Creating back up [OK]");

        } catch (IOException ex) {
            logger.error("Creating back up [FAIL]", ex);
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
        father.getContest().getTournamentManager().lastElement().setEnabled(b);
    }
}


