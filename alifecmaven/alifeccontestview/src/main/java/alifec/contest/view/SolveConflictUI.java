package alifec.contest.view;

import lib.contest.Contest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class SolveConflictUI extends JDialog {
    Contest contest;
    String c1, c2;
    String[] options = new String[]{"Restore with back up file", "Delete back Up File"};
    JComboBox combobox;
    SelectColoniesUI table;
    JDialog thisFrame;

    public SolveConflictUI(Contest c, String c1, String c2) {
        super((JDialog) null, "Solve conflict", true);
        this.c1 = c1;
        this.c2 = c2;
        this.getContentPane().add(createNorth(), BorderLayout.NORTH);
        this.getContentPane().add(createCenter(), BorderLayout.CENTER);
        this.getContentPane().add(createSouth(), BorderLayout.SOUTH);
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.addEscapeKey();
        this.pack();

        Dimension scr = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension d = this.getPreferredSize();
        this.setLocation((scr.width - d.width) / 2, (scr.height - d.height) / 2);


        this.thisFrame = this;
        this.contest = c;
    }

    /*
       public static void main(String [] args){
          new SolveConflictUI(null, "aa", "bb").setVisible(true);
          System.out.println("sigue");
       }
    */
    public JComboBox createNorth() {
        String txt = "The last run failed, select an option";

        combobox = new JComboBox(options);
        combobox.setBorder(BorderFactory.createTitledBorder(txt));
        combobox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                table.setEnabled(options[0].equals(combobox.getSelectedItem()));
            }
        });

        return combobox;
    }

    public JScrollPane createCenter() {
        Object[][] rows = {{false, c1}, {false, c2}};
        String txt = "Select if you want to remove a colony of the tournament";
        table = new SelectColoniesUI(rows);
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createTitledBorder(txt));
        sp.setPreferredSize(new Dimension(420, 100));
        return sp;
    }

    public JPanel createSouth() {
        JPanel p = new JPanel(new FlowLayout());
        JButton accept = new JButton("Ok");
        JButton cancel = new JButton("Cancel");

        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                System.exit(0);
            }
        });
        accept.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                if (options[0].equals(combobox.getSelectedItem())) {
                    if (table.isSelected(0)) {
                        if (contest.getEnvironment().delete(c1)) {
                            System.out.println("Deleting " + c1 + "  [OK]");
                        } else {
                            System.out.println("Deleting " + c1 + "  [FAIL]");
                        }

                    }
                    if (table.isSelected(1)) {
                        if (contest.getEnvironment().delete(c2)) {
                            System.out.println("Deleting " + c2 + "  [OK]");
                        } else {
                            System.out.println("Deleting " + c2 + "  [FAIL]");
                        }
                    }
                    for (String c : contest.getEnvironment().getNames()) {
                        contest.getTournamentManager().lastElement().addColony(c);
                    }
                } else {
                    contest.delete_backup();
                }
                thisFrame.dispose();
            }
        });

        p.add(accept);
        p.add(cancel);

        return p;
    }

    private void addEscapeKey() {
        KeyStroke escape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);

        AbstractAction escapeAction = new AbstractAction() {
            private static final long serialVersionUID = 8L;

            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        };

        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escape, "ESCAPE");
        getRootPane().getActionMap().put("ESCAPE", escapeAction);
    }
}
