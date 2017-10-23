package alifec.contest.view;


import alifec.core.contest.Contest;
import alifec.core.contest.oponentInfo.OpponentReportLine;
import alifec.core.exception.CreateRankingException;
import alifec.core.persistence.ContestConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


public class ContestReportUI extends JDialog implements ActionListener {
    private static final long serialVersionUID = 10L;
    Logger logger = LogManager.getLogger(getClass());

    ContestUI father;
    JTextArea txt = new JTextArea("");
    JButton ReportTxt = new JButton("Report.txt");
    JButton ReportCsv = new JButton("Report.csv");
    JButton close = new JButton("Close");

    private Contest contest;
    private ContestConfig config;

    public ContestReportUI(ContestUI father) {
        super(father, "Contest Report", true);

        this.father = father;
        this.contest = father.getContest();
        this.config = father.getContest().getConfig();

        addEscapeKey();

        initComponents();
        pack();

        Point p = new Point();
        p.x = father.getX() + (father.getWidth() - getWidth()) / 2;
        p.y = father.getY() + (father.getHeight() - getHeight()) / 2;

        setLocation(p.x, p.y);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void addEscapeKey() {
        // Handle escape key to close the dialog
        KeyStroke escape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);

        AbstractAction escapeAction = new AbstractAction() {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        };

        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escape, "ESCAPE");
        getRootPane().getActionMap().put("ESCAPE", escapeAction);
    }

    private void initComponents() {
        txt.setPreferredSize(new Dimension(450, 300));
        txt.setEditable(false);
        txt.setEnabled(false);
        fillTxt();

        ReportCsv.addActionListener(this);
        ReportTxt.addActionListener(this);
        close.addActionListener(this);

        JPanel panel = new JPanel();
        panel.add(ReportTxt);
        panel.add(ReportCsv);
        panel.add(close);
        getContentPane().add(BorderLayout.CENTER, new JScrollPane(txt));
        getContentPane().add(BorderLayout.SOUTH, panel);
    }

    private boolean createReportTxt() {
        String url = config.getReportFilenameTxt();
        try {
            FileWriter fw = new FileWriter(url, false);
            PrintWriter pw = new PrintWriter(fw);
            pw.println(txt.getText());
            pw.close();
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
            return false;
        }
        return true;
    }

    private boolean createReportCsv() {
        try {

            String url = config.getReportFilenameCsv();
            FileWriter fw = new FileWriter(url, false);
            PrintWriter pw = new PrintWriter(fw);
            pw.println("Contest name:," + contest.getName());
            pw.println("Tournament name:," + contest.lastElement().getName());
            pw.println(String.format(ContestConfig.REPORT_CSV_FORMAT,
                    "NAME",
                    "AUTHOR",
                    "AFFILIATION",
                    "POINTS",
                    "ENERGY"));

            for (OpponentReportLine line : contest.getInfo()) {
                pw.println(String.format(ContestConfig.REPORT_CSV_FORMAT,
                        line.getName(),
                        line.getAuthor(),
                        line.getAffiliation(),
                        line.getRanking(),
                        line.getAccumulated()));
            }

            pw.close();
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
            return false;
        } catch (CreateRankingException ex) {
            logger.error(ex.getMessage(), ex);
            Message.printErr(father, ex.getMessage());
            return false;
        }
        return true;
    }

    private void fillTxt() {
        try {
            StringBuilder builder = new StringBuilder();

            builder.append("NAME OF CONTEST: ");
            builder.append(contest.getName());
            builder.append('\n');
            builder.append("NAME OF TOURNAMENT: ");
            builder.append(contest.lastElement().getName());
            builder.append("\n\n");
            builder.append(String.format(ContestConfig.REPORT_TXT_FORMAT,
                    "NAME",
                    "AUTHOR",
                    "AFFILIATION",
                    "POINTS"));

            for (OpponentReportLine line : contest.getInfo()) {
                builder.append(String.format(ContestConfig.REPORT_TXT_FORMAT,
                        line.getName(),
                        line.getAuthor(),
                        line.getAffiliation(),
                        line.getRanking()));
            }

            txt.setDisabledTextColor(Color.BLACK);
            txt.setText(builder.toString());
        } catch (CreateRankingException ex) {
            logger.error(ex.getMessage(), ex);
            Message.printErr(father, ex.getMessage());
        }
    }

    public void actionPerformed(ActionEvent ev) {
        if (ev.getSource().equals(ReportTxt)) {
            if (createReportTxt()) {
                Message.printInfo(father, "TXT Report created successfully");
            } else {
                Message.printErr(father, "can't create the report.");
            }
        } else if (ev.getSource().equals(ReportCsv)) {
            if (createReportCsv()) {
                Message.printInfo(father, "CSV Report created successfully");
            } else {
                Message.printErr(father, "can't create the report.");
            }
        } else if (ev.getSource().equals(close)) {
            dispose();
        }
    }
}
