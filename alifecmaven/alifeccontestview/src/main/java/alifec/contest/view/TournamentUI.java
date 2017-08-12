/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

package alifec.contest.view;



import alifec.core.contest.tournament.JavaFile;
import alifec.core.contest.tournament.Tournament;
import alifec.core.contest.tournament.TournamentManager;
import alifec.core.exception.CreateTournamentException;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

public class TournamentUI extends JPanel implements ActionListener {
    private static final long serialVersionUID = 0L;

    /**
     * administra tanto los turnos ya corridos y como
     * el que se esta corriendo actualmente.
     */
    private TournamentManager tm;
    /**
     * Interface grafica padre (Frame principal).
     */
    private ContestUI father;

    /**
     * Cada TournamentPanel es un panel
     * con cada turno ejecutado o que se esta ejecutando.
     */
    private Vector<TournamentPanel> tPanel;

    private Vector<JScrollPane> tScroll;
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
        this.tm = cui.getContest().getTournamentManager();
        this.tPanel = new Vector<>(tm.size());
        this.tScroll = new Vector<>(tm.size());

        border.setTitle(tm.getSelected().getName());
        this.setLayout(new BorderLayout());
        this.add(createMenuBar(), BorderLayout.SOUTH);
        //      JScrollPane sp = new JScrollPane();

        for (int i = 0; i < tm.size(); i++) {
            Tournament t = tm.getTournament(i);
            addTournament(t);
        }

        add(tScroll.elementAt(tm.getSelectedID()), BorderLayout.CENTER);
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
        tPanel.lastElement().update();
    }

    public void updateAll() {
        for (TournamentPanel tp : tPanel)
            tp.update();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(addColony)) {
            JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new JavaFile());

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
//      } else if(e.getSource().equals(delColony)) {
//         new DeleteColony(father).setVisible(true);		 
        } else if (e.getSource().equals(prevTournament)) {
            remove(tScroll.elementAt(tm.getSelectedID()));
            tm.prev();
            add(tScroll.elementAt(tm.getSelectedID()), BorderLayout.CENTER);
            border.setTitle(tm.getSelected().getName());
            updateUI();
        } else if (e.getSource().equals(nextTournament)) {
            remove(tScroll.elementAt(tm.getSelectedID()));
            tm.next();
            add(tScroll.elementAt(tm.getSelectedID()), BorderLayout.CENTER);
            border.setTitle(tm.getSelected().getName());
            updateUI();
        } else if (e.getSource().equals(ranking)) {
            new ContestReport(father);
        } else if (e.getSource().equals(addTournament)) {
            String txt = "Are you sure you want to add a new tournament?";

            if (Message.printYesNoCancel(father, txt)) {
                try {
                    father.getBattleUI().setEnabled(true);
                    remove(tScroll.elementAt(tm.getSelectedID()));
                    tm.newTournament(father.getContest().getEnvironment().getNames());

                    addTournament(tm.getSelected());
                    father.getBattleUI().clear();
                    border.setTitle(tm.getSelected().getName());
                    add(tScroll.elementAt(tm.getSelectedID()));
                    updateUI();
                } catch (CreateTournamentException ex) {
                    Message.printErr(father, "Can't create a new Tournament");
                }
            }

        } else if (e.getSource().equals(delTournament)) {
            int selected = tm.getSelectedID();
            String txt = "Are you sure you want to delete " + tm.getSelected().getName() + "?";

            if (Message.printYesNoCancel(father, txt)) {
                if (tm.size() == 0) {
                    Message.printErr(father, "The are not tournament to be removed.");
                    return;
                }
                if (tm.size() == 1) {
                    Message.printErr(father, "You can't delete the tournament. The application requires at least one tournament for the contest");
                    return;
                }


                if (!tm.removeSelected()) {
                    Message.printErr(father, "The tournament can't be removed");
                } else {
                    if (tm.getSelectedID() != selected && tm.getSelected().equals(tm.lastElement())) {
                        father.getBattleUI().setEnabled(false);
//						father.getBattleUI().setHastTournament(false);
                    }
                    remove(tScroll.elementAt(selected));
                    tPanel.remove(selected);
                    tScroll.remove(selected);
                    add(tScroll.elementAt(tm.getSelectedID()), BorderLayout.CENTER);
                    border.setTitle(tm.getSelected().getName());
                    updateUI();
                }
            }
        }
    }

    private void addTournament(Tournament t) {
        t.setEnabled(true);
        TournamentPanel tp = new TournamentPanel(father, t, getBackground());
        tPanel.addElement(tp);
        tScroll.addElement(new JScrollPane(tp));
    }

    /**
     * Delete all instance of the colony colonyName in the last tournament and print log in /log
     *
     * @param colonyName is the name of colony to penalize
     */
    public void penalize(String colonyName) {
        father.getContest().getEnvironment().delete(colonyName);
        tm.lastElement().penalize(colonyName);
        tPanel.lastElement().update();
    }
}
