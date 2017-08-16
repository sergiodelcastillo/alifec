/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

package alifec.contest.view;



import alifec.contest.simulationUI.GUIdosD;
import alifec.core.contest.ContestConfig;
import alifec.core.contest.tournament.Tournament;
import alifec.core.contest.tournament.battles.BattleManager;
import alifec.core.contest.tournament.battles.BattleRun;
import alifec.core.exception.CreateBattleException;
import alifec.core.simulation.Environment;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

public class BattleUI extends JPanel implements ActionListener {
    private static final long serialVersionUID = 0L;

    private final ContestUI father;

    private final Environment environment;
    private final DefaultListModel<BattleRun> model = new DefaultListModel<>();
    private final JList<BattleRun> battlesList = new JList<>(model);
    private JScrollPane battlesSP;
    private JComboBox oponent1;
    private JComboBox oponent2;
    private JComboBox nutrient;
    private JButton save;
    private JButton delete;
    private JButton deleteAll;
    private JButton run;
    private JButton runAll;
    private JButton addSelected;
    private JButton addAll;

    private Hashtable<String, Integer> oponents, nutrients;

    private String backupFile = "";


    public BattleUI(ContestUI contestUI, boolean restore) throws IOException {
        this.father = contestUI;
        this.environment = father.getContest().getEnvironment();
//		this.tournament = father.getContest().getTournamentManager().getSelected();

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Battles "));
        add(BorderLayout.NORTH, createNorthPanel());
        add(BorderLayout.CENTER, createCenterPanel());
        add(BorderLayout.SOUTH, createSouthPanel());

        if (restore) {
            Vector<String[]> battles = readBattles(ContestConfig.BATTLES_FILENAME);
            Vector<String[]> backup = readBattles(ContestConfig.BATTLES_BACKUP_FILENAME);

            validate(battles, backup);
            restoreBattles(backup);
        }
    }

    private void restoreBattles(Vector<String[]> backup) {
        if (backup.size() == 0) {
            Tournament t = father.getContest().getTournamentManager().lastElement();
            String url = t.getPath() + File.separator + ContestConfig.BATTLES_BACKUP_FILENAME;
            new File(url).delete();
            return;
        }
        for (String[] line : backup) {
            try {
                String name1 = line[0], name2 = line[1], nameNut = line[2];
                int index1 = environment.getColonyID(name1);
                int index2 = environment.getColonyID(name2);
                int indexNut = environment.getAgar().getNutrientID(nameNut);

                BattleRun battle = new BattleRun(index1, index2, indexNut, name1, name2, nameNut);

                addBattle(battle, father.getContest().getMode() == ContestConfig.PROGRAMMER_MODE);
            } catch (CreateBattleException ex) {
                Message.printErr(father, ex.getMessage());
            }
        }// end for!!
    }

    // 0: oponente 1;
    //	1: oponente 2;
    // 2: distribucion de nutrientes

    private void validate(Vector<String[]> battles, Vector<String[]> backup) {
        // Eliminar las batallas que ya se corrieron
        Vector<String[]> todelete = new Vector<>();
        for (String[] line : battles) {
            for (String[] line_backup : backup) {
                if ((line[0].toLowerCase().equals(line_backup[0].toLowerCase()) &&
                        line[1].toLowerCase().equals(line_backup[1].toLowerCase()) &&
                        line[2].toLowerCase().equals(line_backup[2].toLowerCase())) ||
                        (line[0].toLowerCase().equals(line_backup[1].toLowerCase()) &&
                                line[1].toLowerCase().equals(line_backup[0].toLowerCase()) &&
                                line[2].toLowerCase().equals(line_backup[2].toLowerCase()))) {
                    todelete.addElement(line_backup);
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
                todelete.addElement(l);
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
                todelete.addElement(l);
            }
        }

        backup.removeAll(todelete);
    }

    private Vector<String[]> readBattles(String name) {
        Vector<String[]> res = new Vector<>();
        Tournament t = father.getContest().getTournamentManager().lastElement();
        String url = t.getPath() + File.separator + name;

        try {
            FileReader fr = new FileReader(url);
            BufferedReader br = new BufferedReader(fr);
            String line;

            while ((line = br.readLine()) != null) {
                String[] s = line.split(",");

                if (3 <= s.length) {
                    res.addElement(new String[]{s[0], s[1], s[2]});
                }
            }

            br.close();

        } catch (IOException ignored) {
            res.clear();
        }

        return res;
    }


    private JPanel createNorthPanel() {
        JPanel northPanel = new JPanel();

        JLabel labelOp1 = new JLabel("Op.1");
        JLabel labelOp2 = new JLabel("Op.2");
        JLabel labelDist = new JLabel("Nutrients");

        oponents = environment.getOps();
        nutrients = father.getContest().getNutrients();

        oponent1 = new JComboBox(new MiComboboxModel(oponents));
        oponent2 = new JComboBox(new MiComboboxModel(oponents));
        nutrient = new JComboBox(new MiComboboxModel(nutrients));

        oponent1.setMinimumSize(new Dimension(100, 20));
        oponent2.setMinimumSize(new Dimension(100, 20));

        northPanel.add(labelOp1);
        northPanel.add(oponent1);
        northPanel.add(labelOp2);
        northPanel.add(oponent2);
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
                Message.printErr(father, "You can´t create battles, there are not oponentes");
                return;
            }
            try {
                int index1 = oponents.get(oponent1.getSelectedItem());
                int index2 = oponents.get(oponent2.getSelectedItem());
                int indexNut = nutrients.get(nutrient.getSelectedItem());
                String name1 = oponent1.getSelectedItem().toString();
                String name2 = oponent2.getSelectedItem().toString();
                String nameNut = nutrient.getSelectedItem().toString();

                BattleRun battle = new BattleRun(index1, index2, indexNut, name1, name2, nameNut);

                if (!addBattle(battle, father.getContest().getMode() == ContestConfig.PROGRAMMER_MODE))
                    Message.printErr(this, "Existing battle: " + battle.toString());
            } catch (CreateBattleException ex) {
                Message.printErr(father, ex.getMessage());
            } catch (NullPointerException ex) {
                Message.printErr(this, "List of Nutrient or Oponents are empty.");
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

        for (Enumeration<String> op_a = oponents.keys(); op_a.hasMoreElements();) {
            String n_a = op_a.nextElement(); // name_oponent_a
            Integer i_a = oponents.get(n_a); // index_oponent_a

            for (Enumeration<String> op_b = oponents.keys(); op_b.hasMoreElements();) {
                String n_b = op_b.nextElement(); // name_oponent_b
                Integer i_b = oponents.get(n_b); // index_oponent_b

                if (i_a >= i_b) continue;

                for (Enumeration<String> nut = nutrients.keys(); nut.hasMoreElements();) {
                    String n_n = nut.nextElement();      // name_nutrient
                    Integer i_n = nutrients.get(n_n);    // index_nutrient

                    try {
                        boolean option = father.getContest().getMode() == ContestConfig.PROGRAMMER_MODE;

                        if (!addBattle(new BattleRun(i_a, i_b, i_n, n_a, n_b, n_n), option)) {
                            existingBattle = true;
                        }
                    } catch (CreateBattleException ex) {
                        System.out.println("cant create battle .. :(");
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
            this.oponent1.updateUI();
            this.oponent2.updateUI();
        } catch (Exception ignored) {
        }
    }

    public void updateNutrients() {
        nutrients = father.getContest().getNutrients();

        nutrient.setModel(new MiComboboxModel(nutrients));
        nutrient.updateUI();
    }

    public boolean delete(String colonyName) {
        Vector<BattleRun> indexes = new Vector<>();

        for (int i = 0; i < getBattles().size(); i++) {
            BattleRun b = getBattles().elementAt(i);

            if (b.name1.equals(colonyName) || b.name2.equals(colonyName))
                indexes.addElement(b);
        }

        for (BattleRun b : indexes)
            getBattles().removeElement(b);

        return ((MiComboboxModel) oponent1.getModel()).remove(colonyName) &&
                ((MiComboboxModel) oponent2.getModel()).remove(colonyName);
    }

    public void clear() {
        oponents = environment.getOps();
        nutrients = father.getContest().getNutrients();
        oponent1.setModel(new MiComboboxModel(oponents));
        oponent2.setModel(new MiComboboxModel(oponents));
        nutrient.setModel(new MiComboboxModel(nutrients));

        model.clear();
        battlesList.updateUI();
    }

    void remove(BattleRun b) {
        model.removeElement(b);
        battlesSP.updateUI();
    }

    void createBattlesFileSelected() {
        if (father.getContest().getMode() == ContestConfig.PROGRAMMER_MODE)
            return;

        String path = father.getContest().getTournamentManager().lastElement().getPath();
        backupFile = path + File.separator + "battles_backup.csv";

        System.out.print("Creating back up ");

        try {
            FileWriter fr = new FileWriter(backupFile);
            PrintWriter pw = new PrintWriter(fr);
            String line = battlesList.getSelectedValue().toString();
            line = line.replace(" vs ", ",");
            line = line.replace(" in ", ",");
            pw.println(line);
            pw.close();

            System.out.println("[OK]");

        } catch (FileNotFoundException ex) {
            System.out.println("[FAIL]");
        } catch (IOException ex) {
            System.out.println("[FAIL]");
        }

    }

    boolean deleteBattlesFile() {
        if (father.getContest().getMode() == ContestConfig.PROGRAMMER_MODE)
            return true;

        System.out.print("Removing back up ");

        if (backupFile.equals("")) {
            System.out.println("[FAIL]");
            return false;
        }

        if (new File(backupFile).delete()) {
            System.out.println("[OK]");
            return true;
        } else {
            System.out.println("[FAIL]");
            return false;
        }
    }

    void createBattlesFileAll() {
        if (father.getContest().getMode() == ContestConfig.PROGRAMMER_MODE)
            return;

        //TODO: verificar esto:
        String path = father.getContest().getTournamentManager().lastElement().getPath();
        backupFile = path + File.separator + "battles_backup.csv";

        System.out.print("Creating back up ");

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
            System.out.println("[OK]");

        } catch (FileNotFoundException ex) {
            System.out.println("[FAIL]");
        } catch (IOException ex) {
            System.out.println("[FAIL]");
        }
    }

    public void setEnabled(boolean b) {
        battlesList.setEnabled(b);
        //	private JScrollPane battlesSP;
        oponent1.setEnabled(b);
        oponent2.setEnabled(b);
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


