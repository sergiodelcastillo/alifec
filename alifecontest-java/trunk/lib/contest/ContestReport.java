/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

package lib.contest;

import exceptions.CreateRankingException;
import lib.Defs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;


public class ContestReport extends JDialog implements ActionListener {
    private static final long serialVersionUID = 10L;

    public final String NAMETXT = "report-" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".txt";
    public final String NAMECSV = "report-" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".csv";
    ContestUI father;
    Contest c;
    JTextArea txt = new JTextArea("");
    JButton ReportTxt = new JButton("Report.txt");
    JButton ReportCsv = new JButton("Report.csv");
    JButton close = new JButton("Close");

    public ContestReport(ContestUI father) {
        super(father, "Contest Report", true);
        this.c = father.getContest();
        this.father = father;

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
        try {
            String url = c.getReportPath() + File.separator + NAMETXT;
            FileWriter fw = new FileWriter(url, false);
            PrintWriter pw = new PrintWriter(fw);
            pw.println(txt.getText());
            pw.close();
        } catch (IOException ex) {
            return false;
        }
        return true;
    }

    private boolean createReportCsv() {
        try {
            String url = c.getReportPath() + File.separator + NAMECSV;
            FileWriter fw = new FileWriter(url, false);
            PrintWriter pw = new PrintWriter(fw);
            pw.println("Contest name:," + c.getName());
            pw.println("Tournament name:," + c.getTournamentManager().lastElement().NAME);
            pw.println("NAME,AUTHOR,AFFILIATION,POINTS,ENERGY");

            for (Vector<Object> line : c.getInfo()) {
                pw.println(line.elementAt(0) + "," +
                        line.elementAt(1) + "," +
                        line.elementAt(2) + "," +
                        line.elementAt(3) + "," +
                        line.elementAt(4));
            }

            pw.close();
        } catch (IOException ex) {
            return false;
        } catch (CreateRankingException ex) {
            Message.printErr(father, ex.getMessage());
            return false;
        }
        return true;
    }

    private void fillTxt() {
        try {
            String text = "";

            text += "NAME OF CONTEST: " + c.getName() + "\n";
            text += "NAME OF TOURNAMENT: " + c.getTournamentManager().lastElement().NAME + "\n\n";
            String[] table = new String[]{"NAME", "AUTHOR", "AFFILIATION", "POINTS"};

            for (String line : table)
                text += addSpace(line) + "\t";

            text += "\n";

            for (Vector<Object> line : c.getInfo()) {

                text += addSpace(line.firstElement().toString());

                for (int i = 1; i < line.size(); i++) {
                    // saltar la info del ultimo tournament!!
                    if (line.elementAt(i) instanceof Float) continue;
                    text += "\t" + addSpace(line.elementAt(i).toString().trim());
                }
                text += "\n";
            }

            txt.setDisabledTextColor(Color.BLACK);
            txt.setText(text);
        } catch (CreateRankingException ex) {
            Message.printErr(father, ex.getMessage());
        }
    }

    private String addSpace(String txt) {
        for (; txt.length() <= Defs.MAX_LENGTH;)
            txt += " ";

        return txt;
    }

    public void actionPerformed(ActionEvent ev) {
        if (ev.getSource().equals(ReportTxt)) {
            if (createReportTxt()) {
                Message.printOK(father, NAMETXT + " created sucessfully");
            } else {
                Message.printErr(father, "can't create the report.");
            }
        } else if (ev.getSource().equals(ReportCsv)) {
            if (createReportCsv()) {
                Message.printOK(father, NAMECSV + " created sucessfully");
            } else {
                Message.printErr(father, "can't create the report.");
            }
        } else if (ev.getSource().equals(close)) {
            dispose();
        }
    }
}
