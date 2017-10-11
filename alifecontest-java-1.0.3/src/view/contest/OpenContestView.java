package view.contest;

import data.java.AllFilter;
import data.java.Config;
import data.java.Log;
import exceptions.CreateConfigException;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by IntelliJ IDEA.
 * User: yeyo
 * Email: yeyo@druidalabs.com
 * Date: 3/12/11
 * Time: 11:45 PM
 */
public class OpenContestView extends JDialog implements ActionListener {

    private ContestView father;
    private JButton cancel;
    private JButton open;
    private JList list;

    public OpenContestView(ContestView father) {
        super(father, "Open Contest");
        this.father = father;

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        initComponents();
        setResizable(false);

        pack();
        setLocationRelativeTo(father);
        setVisible(true);
    }

    private void initComponents() {
        list = new JList(AllFilter.listContests());
        JPanel southPanel = new JPanel();
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("Select a Contest"));

        list.setPreferredSize(new Dimension(300, 150));

        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                open.setEnabled(!list.getSelectedValue().equals(father.getContest().getName()));
            }
        });

        centerPanel.add(new JScrollPane(list), BorderLayout.CENTER);

        open = new JButton("Open");
        cancel = new JButton("Cancel");

        open.addActionListener(this);
        cancel.addActionListener(this);

        southPanel.add(open);
        southPanel.add(cancel);

        this.add(centerPanel, BorderLayout.CENTER);
        this.add(southPanel, BorderLayout.SOUTH);

        //default option
        open.setEnabled(list.getModel().getSize() < 2);

        if(list.getModel().getSize() > 0){
            list.setSelectedIndex(0);
        }
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(open)) {
            try {
                Config.update((String) list.getSelectedValue());
                father.reload();
                dispose();
            } catch (CreateConfigException ex) {
                Log.printlnAndSave(ex.getMessage(), ex.getCause());
                Message.printErr(this, "Cant update the configuration file");
            }
        } else if (e.getSource().equals(cancel)) {
            dispose();
        }
    }


}
