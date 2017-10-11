package view.tournament;

import controller.java.tournament.Tournament;
import controller.java.tournament.TournamentManager;
import exceptions.CreateTournamentException;
import view.Properties;
import view.contest.ContestView;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Vector;

public class TournamentView extends JPanel {
    private static final long serialVersionUID = 0L;

    /**
     * administra tanto los turnos ya corridos y como
     * el que se esta corriendo actualmente.
     */
    private TournamentManager tournamentManager;
    /**
     * Interface grafica padre (Frame principal).
     */
    private ContestView father;

    /**
     * Cada TournamentPanel es un panel
     * con cada turno ejecutado o que se esta ejecutando.
     */
    private Vector<TournamentPanel> tPanel;

    private Vector<JScrollPane> tScroll;
    private TitledBorder border = BorderFactory.createTitledBorder("Nothing to show");


    // private JButton delColony;
    private JButton prevTournament;
    private JButton nextTournament;
    private JButton ranking;
    private JButton addTournament;
    private JButton delTournament;


    public TournamentView(ContestView cui) {
        setBorder(border);
        this.father = cui;
        this.tournamentManager = cui.getContest().getTournamentManager();
        this.tPanel = new Vector<TournamentPanel>(tournamentManager.getSize());
        this.tScroll = new Vector<JScrollPane>(tournamentManager.getSize());

        border.setTitle(tournamentManager.getSelected().getName());
        this.setLayout(new BorderLayout());
        this.add(createMenuBar(), BorderLayout.SOUTH);

        for (int i = 0; i < tournamentManager.getSize(); i++) {
            addTournament(tournamentManager.get(i));
        }

        add(tScroll.elementAt(tournamentManager.getSelectedIndex()), BorderLayout.CENTER);
    }

    public JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        prevTournament = new JButton(new ImageIcon(Properties.getInstance().getPrevTournamentIcon()));
        nextTournament = new JButton(new ImageIcon(Properties.getInstance().getNextTournamentIcon()));
        ranking = new JButton(new ImageIcon(Properties.getInstance().getRankingIcon()));
        addTournament = new JButton(new ImageIcon(Properties.getInstance().getAddTournamentIcon()));
        delTournament = new JButton(new ImageIcon(Properties.getInstance().getDelTournamentIcon()));

        // setTool Tip!

        prevTournament.setToolTipText("Previous tournament");
        nextTournament.setToolTipText("Next tournament");
        ranking.setToolTipText("Ranking");
        addTournament.setToolTipText("Add tournament");
        delTournament.setToolTipText("Delete tournament");

        TournamentHandler handler = new TournamentHandler(this);

        prevTournament.addActionListener(handler);
        nextTournament.addActionListener(handler);
        ranking.addActionListener(handler);
        addTournament.addActionListener(handler);
        delTournament.addActionListener(handler);

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

    public boolean deleteTournament(int selected) {
        boolean enabled = true;

        if (tournamentManager.getSelectedIndex() == tournamentManager.getSize() - 1) {
            enabled = false;
        }
        if (!tournamentManager.removeSelected()) {
            return false;
        }

        father.getBattleView().setEnabled(enabled);

        remove(tScroll.elementAt(selected));
        tPanel.remove(selected);
        tScroll.remove(selected);
        showSelectedView();
        return true;
    }

    public void addTournament() throws CreateTournamentException {
        father.getBattleView().setEnabled(true);
        remove(tScroll.elementAt(tournamentManager.getSelectedIndex()));
        tournamentManager.newTournament(father.getContest().getEnvironment().getNames());

        addTournament(tournamentManager.getSelected());
        father.getBattleView().update();
        showSelectedView();
    }


    public void nextView() {
        remove(tScroll.elementAt(tournamentManager.getSelectedIndex()));
        tournamentManager.next();
        showSelectedView();
    }

    private void showSelectedView() {
        add(tScroll.elementAt(tournamentManager.getSelectedIndex()), BorderLayout.CENTER);
        border.setTitle(tournamentManager.getSelected().getName());
        updateUI();
    }

    private void addTournament(Tournament t) {
        t.setEnabled(true);
        TournamentPanel tp = new TournamentPanel(father, t, getBackground());
        tPanel.addElement(tp);
        tScroll.addElement(new JScrollPane(tp));
    }

    public void previousView() {
        remove(tScroll.elementAt(tournamentManager.getSelectedIndex()));
        tournamentManager.prev();
       showSelectedView();
    }

    public TournamentManager getTournamentManager() {
        return this.tournamentManager;
    }

    public ContestView getFather() {
        return father;
    }

    public boolean isPrevTournament(Object ob) {
        return prevTournament.equals(ob);
    }

    public boolean isNextTournament(Object ob) {
        return nextTournament.equals(ob);
    }

    public boolean isRanking(Object ob) {
        return ranking.equals(ob);
    }

    public boolean isAddTournament(Object ob) {
        return addTournament.equals(ob);
    }

    public boolean isDelTournament(Object ob) {
        return delTournament.equals(ob);
    }
}
