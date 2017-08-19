/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

package alifec.contest.view;


import alifec.core.contest.tournament.Tournament;
import alifec.core.contest.tournament.TournamentManager;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Hashtable;
import java.util.Iterator;

public class TournamentPanel extends JList {
    private static final long serialVersionUID = 0L;

    Logger logger = Logger.getLogger(getClass());

    JPopupMenu pm = new JPopupMenu("PopUp");
    JMenuItem deleteOfContest = new JMenuItem("Delete Colony of Contest");
    JMenuItem deleteOfTournament = new JMenuItem("Delete Colony of Tournament");

    private Tournament current;
    private TournamentManager tm;

    public TournamentPanel(final ContestUI father, Tournament t, Color c) {
        super();
        current = t;
        tm = father.getContest().getTournamentManager();
        setCellRenderer(new TournamentPanelOpponentUI(this));
        this.setModel(addComponents());
        this.setBackground(new Color(c.getRed(), c.getGreen(), c.getBlue()));

        pm.add(deleteOfTournament);
        pm.add(new JPopupMenu.Separator());
        pm.add(deleteOfContest);

        final JList list = this;
        // add popup menu.
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

                for (int i = 0; i < tm.size(); i++) {
                    tm.getTournament(i).delete(selected);
                }

                father.getBattleUI().delete(selected); // delete of UI
                father.getTournamentUI().updateAll(); // actualizar UI
            }
        });

        deleteOfTournament.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selected = getSelected();
                father.getBattleUI().delete(selected); // delete of UI
                tm.lastElement().delete(selected); // delete of lastTournament
                father.getTournamentUI().updateLast(); // delete of UI
                //father.getContest().getEnvironment().delete(selected); //delete of environment
                update(); // actualizar UI
            }
        });
    }

    private String getSelected() {
        String sel = ((TournamentPanelOpponentData) getSelectedValue()).getName();
        int index = sel.indexOf(". ");
        return sel.substring(index + 2);
    }

    /**
     * cuando cambia el tournament se usa este medoto
     * para actualizar la vista!!
     */
    public synchronized void update() {
        try {
            setModel(addComponents());
        } catch (NullPointerException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    private DefaultListModel<TournamentPanelOpponentData> addComponents() {
        DefaultListModel<TournamentPanelOpponentData> model = new DefaultListModel<TournamentPanelOpponentData>();
        Hashtable<String, Float> h = current.getAccumulatedEnergy();
        int index = 0;

        for (Iterator<String> i = h.keySet().iterator(); i.hasNext(); index++) {
            String key = i.next();
            int value = h.get(key).intValue();
            long max = (long) current.getMaxEnergy();
            model.addElement(new TournamentPanelOpponentData(index + ". " + key, value, max));
        }

        return model;
    }
}



