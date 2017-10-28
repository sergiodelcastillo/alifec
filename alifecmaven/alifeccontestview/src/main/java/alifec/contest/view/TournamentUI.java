
package alifec.contest.view;



import alifec.core.contest.Contest;
import alifec.core.contest.Tournament;
import alifec.core.exception.TournamentException;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class TournamentUI extends JPanel implements ActionListener {
    private static final long serialVersionUID = 0L;

    private Contest contest;
    private ContestUI father;

    private List<TournamentPanel> tPanel;

    private List<JScrollPane> tScroll;
    private TitledBorder border = BorderFactory.createTitledBorder("Nothing to show");

    private JButton addColony;
    // private JButton delColony;
    private JButton prevTournament;
    private JButton nextTournament;
    private JButton ranking;
    private JButton addTournament;
    private JButton delTournament;


    public TournamentUI(ContestUI cui) {
        setBorder(border);
        this.father = cui;
        this.contest = cui.getContest();
        this.tPanel = new ArrayList<>(contest.size());
        this.tScroll = new ArrayList<>(contest.size());

        border.setTitle(contest.getSelected().getName());
        this.setLayout(new BorderLayout());
        this.add(createMenuBar(), BorderLayout.SOUTH);
        //      JScrollPane sp = new JScrollPane();

        for (int i = 0; i < contest.size(); i++) {
            Tournament t = contest.getTournament(i);
            addTournament(t);
        }

        add(tScroll.get(contest.getSelectedID()), BorderLayout.CENTER);
        addColony.setEnabled(false);
    }

    public JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        addColony = new JButton(new ImageIcon(getClass().getResource("/icons/openColony.png")));
        //	delColony = new JButton(new ImageIcon(getClass().getResource("icons/deleteColony.png"));
        //JButton firstTourn = new JButton("First");
        prevTournament = new JButton(new ImageIcon(getClass().getResource("/icons/prevTournament.png")));
        nextTournament = new JButton(new ImageIcon(getClass().getResource("/icons/nextTournament.png")));
        ranking = new JButton(new ImageIcon(getClass().getResource("/icons/ranking.png")));
        addTournament = new JButton(new ImageIcon(getClass().getResource("/icons/addTournament.png")));
        delTournament = new JButton(new ImageIcon(getClass().getResource("/icons/delTournament.png")));

        // setTool Tip!
        addColony.setToolTipText("Add colony");
        //	delColony.setToolTipText("Delete colony");
        //	firstTourn.setToolTipText("First tournament");
        prevTournament.setToolTipText("Previous tournament");
        nextTournament.setToolTipText("Next tournament");
        ranking.setToolTipText("Ranking");
        addTournament.setToolTipText("Add tournament");
        delTournament.setToolTipText("Delete tournament");

        addColony.addActionListener(this);
        //     delColony.addActionListener(this);
        prevTournament.addActionListener(this);
        nextTournament.addActionListener(this);
        ranking.addActionListener(this);
        addTournament.addActionListener(this);
        delTournament.addActionListener(this);

        // -- crear los listeners!

        menuBar.add(addColony);
        //	menuBar.add(delColony);
        //menuBar.add(firstTourn);
        menuBar.add(prevTournament);
        menuBar.add(nextTournament);
        menuBar.add(ranking);
        menuBar.add(delTournament);
        menuBar.add(addTournament);
        return menuBar;
    }

    public void updateLast() {
        tPanel.get(tPanel.size()-1).update();
    }

    public void updateAll() {
        for (TournamentPanel tp : tPanel)
            tp.update();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(addColony)) {
            JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new JavaFileFilter());

            if (fc.showOpenDialog(father) == 0 &&
                    fc.getSelectedFile() != null &&
                    fc.getSelectedFile().isFile()) {
                try {
/*						String name = fc.getSelectedFile().getName();
						String path = fc.getSelectedFile().getPath();
						String colony = father.getContest().getEnvironment().getOpponents().lastElement().getName();
						System.out.println("ver");
						father.getContest().getEnvironment().addColony(name,"", path);
						tm.lastElement().addColony(colony);
						tPanel.lastElement().update();
						father.repaint();*/
                } catch (IllegalArgumentException ex) {
                    Message.printErr(father, "Invalid File");
                }
            }
        } else if (e.getSource().equals(prevTournament)) {
            remove(tScroll.get(contest.getSelectedID()));
            contest.prev();
            add(tScroll.get(contest.getSelectedID()), BorderLayout.CENTER);
            border.setTitle(contest.getSelected().getName());
            updateUI();
        } else if (e.getSource().equals(nextTournament)) {
            remove(tScroll.get(contest.getSelectedID()));
            contest.next();
            add(tScroll.get(contest.getSelectedID()), BorderLayout.CENTER);
            border.setTitle(contest.getSelected().getName());
            updateUI();
        } else if (e.getSource().equals(ranking)) {
            new ContestReportUI(father);
        } else if (e.getSource().equals(addTournament)) {
            String txt = "Are you sure you want to add a new tournament?";

            if (Message.printYesNoCancel(father, txt)) {
                try {
                    father.getBattleUI().setEnabled(true);
                    remove(tScroll.get(contest.getSelectedID()));
                    contest.newTournament();

                    addTournament(contest.getSelected());
                    father.getBattleUI().clear();
                    border.setTitle(contest.getSelected().getName());
                    add(tScroll.get(contest.getSelectedID()));
                    updateUI();
                } catch (TournamentException ex) {
                    Message.printErr(father, "Can't create a new Tournament");
                }
            }

        } else if (e.getSource().equals(delTournament)) {
            int selected = contest.getSelectedID();
            String txt = "Are you sure you want to delete " + contest.getSelected().getName() + "?";

            if (Message.printYesNoCancel(father, txt)) {
                if (contest.size() == 0) {
                    Message.printErr(father, "The are not tournament to be removed.");
                    return;
                }
                if (contest.size() == 1) {
                    Message.printErr(father, "You can't delete the tournament. The application requires at least one tournament for the contest");
                    return;
                }


                if (!contest.removeSelected()) {
                    Message.printErr(father, "The tournament can't be removed");
                } else {
                    if (contest.getSelectedID() != selected && contest.getSelected().equals(contest.lastTournament())) {
                        father.getBattleUI().setEnabled(false);
//						father.getBattleUI().setHastTournament(false);
                    }
                    remove(tScroll.get(selected));
                    tPanel.remove(selected);
                    tScroll.remove(selected);
                    add(tScroll.get(contest.getSelectedID()), BorderLayout.CENTER);
                    border.setTitle(contest.getSelected().getName());
                    updateUI();
                }
            }
        }
    }

    private void addTournament(Tournament t) {
        t.setEnabled(true);
        TournamentPanel tp = new TournamentPanel(father, t, getBackground());
        tPanel.add(tp);
        tScroll.add(new JScrollPane(tp));
    }

    /**
     * Delete all instance of the colony colonyName in the last tournament and print log in /log
     *
     * @param colonyName is the name of colony to penalize
     */
    public void penalize(String colonyName) {
        father.getContest().getEnvironment().delete(colonyName);
        contest.lastTournament().delete(colonyName);
        tPanel.get(tPanel.size()-1).update();
    }
}
