/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

package lib.tournament;

import lib.contest.ContestUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Hashtable;
import java.util.Iterator;

public class TournamentPanel extends JList {
    private static final long serialVersionUID = 0L;

    JPopupMenu pm = new JPopupMenu("PopUp");
    JMenuItem deleteOfContest = new JMenuItem("Delete Colony of Contest");
    JMenuItem deleteOfTournament = new JMenuItem("Delete Colony of Tournament");

    private Tournament current;
    private TournamentManager tm;

    public TournamentPanel(final ContestUI father, Tournament t, Color c) {
        super();
        current = t;
        tm = father.getContest().getTournamentManager();
        setCellRenderer(new OponentUI(this));
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
        String sel = ((ListOponent) getSelectedValue()).getName();
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
            System.out.println("tournamentpanel.update().. error");
            ex.printStackTrace();
        }
    }

    private synchronized DefaultListModel addComponents() {
        synchronized (Tournament.class) {

            DefaultListModel model = new DefaultListModel();
            Hashtable<String, Float> h = current.getAcumulatedEnergy();
            int index = 0;

            for (Iterator<String> i = h.keySet().iterator(); i.hasNext(); index++) {
                String key = i.next();
                int value = h.get(key).intValue();
                long max = (long) current.getMaxEnergy();
                model.addElement(new ListOponent(index + ". " + key, value, max));
            }
            return model;
        }
    }
}

class OponentUI extends JPanel implements ListCellRenderer {
    private static final long serialVersionUID = 0L;
    private JLabel label;
    private JProgressBar progressbar;

    public OponentUI(JComponent c) {
        setLayout(new GridLayout());
        setBorder(new EmptyBorder(6, 6, 6, 6));

        label = new JLabel();
        progressbar = new JProgressBar();
        progressbar.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        label.setPreferredSize(new Dimension(150, 20));
        add(label);
        add(progressbar);

        setAlignmentX(Component.RIGHT_ALIGNMENT);
    }

    public Component getListCellRendererComponent(JList list, Object value,
                                                  int index, boolean isSelected, boolean cellHasFocus) {

        if (isSelected) {
            setBackground(list.getSelectionBackground());
            label.setBackground(list.getSelectionBackground());
            label.setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            label.setBackground(list.getBackground());
            label.setForeground(list.getForeground());
        }

        ListOponent l = (ListOponent) value;

        label.setText(l.getName());
        label.setAlignmentX(JLabel.LEFT_ALIGNMENT);

        progressbar.setValue(l.getValue());
        progressbar.setString("" + l.getValue());
        progressbar.setStringPainted(true);
        progressbar.setMaximum((int) l.getMax());
        try {
            progressbar.updateUI();
        } catch (Exception ignored) {
        }
        return this;
    }
}

class ListOponent {
    private String name = "";
    private long max = 0L;
    private int value = 0;

    public ListOponent(String name, int value, long max) {
        if (name != null)
            this.name = name;

        this.value = value;
        this.max = max;
    }

    public String getName() {
        if (name == null) return "";
        return name;
    }

    public long getMax() {
        return max;
    }

    public int getValue() {
        return value;
    }
}
