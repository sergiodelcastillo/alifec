package view.tournament;

import controller.java.tournament.Tournament;
import controller.java.tournament.TournamentManager;
import data.java.tournament.AccumulatedDTO;
import data.java.tournament.ListOpponent;
import view.contest.ContestView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 * Created by Sergio Del Castillo
 * User: yeyo
 * Date: Jun 11, 2010
 * Time: 9:50:04 PM
 * Email: yeyo@druidalabs.com
 */
public class TournamentPanel extends JList {
    private static final long serialVersionUID = 0L;

    private JPopupMenu pm = new JPopupMenu("PopUp");
    private JMenuItem deleteOfContest = new JMenuItem("Delete Colony of Contest");
    private JMenuItem deleteOfTournament = new JMenuItem("Delete Colony of Tournament");

    private Tournament current;
    private TournamentManager tm;

    public TournamentPanel(final ContestView father, Tournament t, Color c) {
        super();
        current = t;
        tm = father.getContest().getTournamentManager();
        setCellRenderer(new OpponentListCellRenderer());
        this.setModel(addComponents());
        this.setBackground(new Color(c.getRed(), c.getGreen(), c.getBlue()));

        pm.add(deleteOfTournament);
        pm.add(new JPopupMenu.Separator());
        pm.add(deleteOfContest);

        final JList list = this;
        // append popUp menu.
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent me) {
                //verify if the selected tournament has tournament.
                if (!tm.getSelected().isEnabled()) {
                    return;
                }

                if (SwingUtilities.isRightMouseButton(me) && !isSelectionEmpty() &&
                        locationToIndex(me.getPoint()) == getSelectedIndex()) {
                    pm.show(list, me.getX(), me.getY());
                }
            }
        });
        deleteOfContest.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selected = getSelected();

                father.getContest().getEnvironment().delete(selected); //delete of environment

                for (int i = 0; i < tm.getSize(); i++) {
                    tm.get(i).deleteColony(selected);
                }

                father.getBattleView().update();
                father.getTournamentView().updateAll(); // saveConfig UI
            }
        });

        deleteOfTournament.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selected = getSelected();
                father.getBattleView().deleteColony(selected); // delete UI
                tm.getLastTournament().deleteColony(selected); // delete of lastTournament
                father.getTournamentView().updateLast(); // delete of UI
                //father.getContest().getEnvironment().delete(selected); //delete of environment
                update(); // actualizar UI
            }
        });
    }

    private String getSelected() {
        String sel = ((ListOpponent) getSelectedValue()).getName();
        int index = sel.indexOf(". ");
        return sel.substring(index + 2);
    }

    /**
     * cuando cambia el tournament se usa este medoto
     * para actualizar la vista!!
     */
    public void update() {
        setModel(addComponents());
    }

    private DefaultListModel addComponents() {
        DefaultListModel model = new DefaultListModel();
        ArrayList<AccumulatedDTO> accumulated = current.getAccumulatedEnergy();
        long max = (long) current.getMaxEnergy();

        for (int index = 0; index < accumulated.size(); index++) {
            model.addElement(new ListOpponent(accumulated.get(index), max, index + 1));
        }

        return model;

    }
}

