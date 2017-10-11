/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

package view.contest;

import controller.java.contest.Contest;
import data.java.Log;
import data.java.contest.RankingDTO;
import view.components.DialogView;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;


public class RankingView extends DialogView implements ActionListener {
    private static final long serialVersionUID = 10L;

    private ContestView father;
    private Contest contest;

    private JButton toTxt = new JButton("Report.txt");
    private JButton toCSV = new JButton("Report.csv");
    private JButton cancel = new JButton("Close");

    private ArrayList<RankingDTO> ranking;

    public RankingView(ContestView father, ArrayList<RankingDTO> ranking) {
        super(father, "Ranking");
        this.contest = father.getContest();
        this.father = father;
        this.ranking = ranking;

        initComponents();

        this.setMinimumSize(new Dimension(600, 360));
       this.setLocationRelativeTo(father);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);

    }

    private void initComponents() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Ranking"));

        GroupLayout layout = new GroupLayout(this.getContentPane());
        this.getContentPane().setLayout(layout);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        createRanking(panel);


        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).
                addComponent(panel).
                addGroup(layout.createSequentialGroup().
                addComponent(toCSV).
                addComponent(toTxt).
                addComponent(this.cancel)));

        layout.setVerticalGroup(layout.createSequentialGroup().
                addComponent(panel).
                addGroup(layout.createParallelGroup().
                addComponent(toCSV).
                addComponent(toTxt).
                addComponent(cancel)));

        toCSV.addActionListener(this);
        toTxt.addActionListener(this);
        cancel.addActionListener(this);
    }

    private void createRanking(JPanel panel) {
        JLabel contestLabel = new JLabel("<html>Name of Contest: <b>" + contest.getName() + "</b></html>");
        JLabel tournamentLabel = new JLabel("<html>Current tournament: <b>" + contest.getTournamentManager().getLastTournament().getName() + "</b></html>");
        JTable rankingTable = new JTable(new RankingTableModel(ranking, father.getContest().getMode()));
        JScrollPane scrollPane = new JScrollPane(rankingTable);

        DefaultTableCellRenderer tcr = new DefaultTableCellRenderer();
        tcr.setHorizontalAlignment(SwingConstants.CENTER);

        rankingTable.getColumnModel().getColumn(3).setMaxWidth(70);
        rankingTable.getColumnModel().getColumn(2).setMaxWidth(150);

        rankingTable.getColumnModel().getColumn(3).setCellRenderer(tcr);
        rankingTable.getColumnModel().getColumn(2).setCellRenderer(tcr);
        //rankingTable.createDefaultColumnsFromModel();

        GroupLayout layout = new GroupLayout(panel);
        panel.setLayout(layout);

        layout.setHorizontalGroup(layout.createParallelGroup().
                addComponent(contestLabel).
                addComponent(tournamentLabel).
                addComponent(scrollPane));

        layout.setVerticalGroup(layout.createSequentialGroup().
                addGap(10, 10, 15).
                addComponent(contestLabel).
                addGap(10, 10, 15).
                addComponent(tournamentLabel).
                addGap(15, 15, 20).
                addComponent(scrollPane));
        
    }

    public void actionPerformed(ActionEvent ev) {
        if (ev.getSource().equals(toTxt)) {
            try {
                contest.createReport(Contest.Report.TXT, ranking);
                Message.printOK(father, "The report.txt was created successful");
            } catch (IOException e) {
                Log.printlnAndSave("error creating TXT report", e);
                Message.printErr(father, "Can't create the report.");
            }
        } else if (ev.getSource().equals(toCSV)) {
            try {
                contest.createReport(Contest.Report.CSV, ranking);
                Message.printOK(father, "The report.csv was created successful");
            } catch (IOException e) {
                Log.printlnAndSave("error creating TXT report", e);
                Message.printErr(father, "Can't create the report.");
            }
        } else if (ev.getSource().equals(cancel)) {
            dispose();
        }
    }
}




