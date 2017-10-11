package view.contest;

import controller.java.battle.BattleRun;
import controller.java.contest.Contest;
import data.java.Log;
import view.components.DialogView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class SolveConflictView extends DialogView implements ActionListener {
    private JComboBox comboBox;
    private SolveConflictTable table;
    private JButton accept;
    private JButton cancel;

    private String[] options = new String[]{"Restore with back up file", "Delete back Up File"};

    private Contest contest;
    private BattleRun battle;
    private boolean restored = false;
    ArrayList<BattleRun> list;

    public SolveConflictView(Contest c, ArrayList<BattleRun> battles) {
        super(null, "Solve conflict");
        this.battle = battles.get(0);
        this.list = battles;

        this.getContentPane().add(createNorth(), BorderLayout.NORTH);
        this.getContentPane().add(createCenter(), BorderLayout.CENTER);
        this.getContentPane().add(createSouth(), BorderLayout.SOUTH);
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.pack();

        this.setLocationRelativeTo(null);

        this.contest = c;
        setVisible(true);
    }

    public JComboBox createNorth() {
        String txt = "The last run failed, select an option";

        comboBox = new JComboBox(options);
        comboBox.setBorder(BorderFactory.createTitledBorder(txt));
        comboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                table.setEnabled(options[0].equals(comboBox.getSelectedItem()));
            }
        });

        return comboBox;
    }

    public JScrollPane createCenter() {
        Object[][] rows = {{false, battle.getFirstName()}, {false, battle.getSecondName()}};
        String txt = "Select if you want to remove a colony of the tournament";
        table = new SolveConflictTable(rows);
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createTitledBorder(txt));
        sp.setPreferredSize(new Dimension(420, 100));
        return sp;
    }

    public JPanel createSouth() {
        JPanel p = new JPanel(new FlowLayout());
        accept = new JButton("Ok");
        cancel = new JButton("Cancel");

        cancel.addActionListener(this);
        accept.addActionListener(this);

        p.add(accept);
        p.add(cancel);

        return p;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(cancel)) {
            Log.println("Canceled");
            System.exit(0);
        } else if (e.getSource().equals(accept)) {
            if (options[0].equals(comboBox.getSelectedItem())) {
                delete(0, battle.getFirstName());
                delete(1, battle.getSecondName());

                for (String c : contest.getEnvironment().getNames()) {
                    contest.getTournamentManager().getLastTournament().addColony(c);
                }

                contest.addColonyIds(list);
                restored = true;
            }
            contest.deleteBackup();

            this.setVisible(false);
        }
    }

    private void delete(int index, String name) {
        if (!table.isSelected(index)) return;
        //delete of the file
        contest.getTournamentManager().getLastTournament().deleteColony(name);

        //delete of the environment
        if (contest.getEnvironment().delete(name)) {
            System.out.println("Deleting " + name + "  [OK]");
        } else {
            System.out.println("Deleting " + name + "  [FAIL]");
        }

        //delete of the list of battles
        ArrayList<BattleRun> toDel = new ArrayList<BattleRun>();

        for (BattleRun aList : list) {
            if (aList.contain(name)) {
                toDel.add(aList);
            }
        }

        list.removeAll(toDel);
    }

    public boolean isRestored() {
        return restored;
    }
}
