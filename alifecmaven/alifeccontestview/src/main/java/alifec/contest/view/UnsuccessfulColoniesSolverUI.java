package alifec.contest.view;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class UnsuccessfulColoniesSolverUI extends JDialog {
    private Logger logger = LogManager.getLogger(getClass());
    ContestUI contestUI;
    String c1, c2;
    String[] options = new String[]{"Take action with backup file", "Ignore and delete backup file"};
    JComboBox<String> combobox;
    SelectColoniesUI table;
    JDialog thisFrame;

    public UnsuccessfulColoniesSolverUI(ContestUI father, String c1, String c2) {
        super((JDialog) null, "The last battle didn't finish successfully", true);
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
        this.contestUI = father;
    }


    public JComboBox<String> createNorth() {
        String txt = "The last run didn't finish successfully, please select an option";

        combobox = new JComboBox<>(options);
        combobox.setBorder(BorderFactory.createTitledBorder(txt));
        combobox.addActionListener(e -> table.setEnabled(options[0].equals(combobox.getSelectedItem())));

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

        cancel.addActionListener(arg0 -> {
            logger.info("The application will shutdown.");
            System.exit(0);
        });

        accept.addActionListener(arg0 -> {
            if (options[0].equals(combobox.getSelectedItem())) {
                if (table.isSelected(0))
                    contestUI.excludeColony(c1);

                if (table.isSelected(1))
                    contestUI.excludeColony(c2);
            }
            thisFrame.dispose();
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
