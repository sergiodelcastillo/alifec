package alifec.contest.view;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Created by Sergio Del Castillo on 18/08/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class TournamentPanelOpponentUI extends JPanel implements ListCellRenderer {
    private static final long serialVersionUID = 0L;
    Logger logger = LogManager.getLogger(getClass());

    private JLabel label;
    private JProgressBar progressbar;


    public TournamentPanelOpponentUI(JComponent c) {
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

        TournamentPanelOpponentData l = (TournamentPanelOpponentData) value;

        label.setText(l.getName());
        label.setAlignmentX(JLabel.LEFT_ALIGNMENT);

        try {
        progressbar.setValue(l.getValue());
        progressbar.setString("" + l.getValue());
        progressbar.setStringPainted(true);
        progressbar.setMaximum((int) l.getMax());
            progressbar.updateUI();
        } catch (Throwable ex) {
            logger.trace(ex.getMessage(), ex);

        }
        return this;
    }
}