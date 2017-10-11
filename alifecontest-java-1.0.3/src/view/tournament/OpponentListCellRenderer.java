package view.tournament;

import data.java.tournament.ListOpponent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Created by Sergio Del Castillo
 * User: yeyo
 * Date: Jun 11, 2010
 * Time: 10:43:57 PM
 * Email: yeyo@druidalabs.com
 */
public class OpponentListCellRenderer extends JPanel implements ListCellRenderer {
    private static final long serialVersionUID = 0L;
    private JLabel label;
    private JProgressBar progressBar;

    public OpponentListCellRenderer() {
        setLayout(new GridLayout());
        setBorder(new EmptyBorder(6, 6, 6, 6));

        label = new JLabel();
        progressBar = new JProgressBar();
        progressBar.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        label.setPreferredSize(new Dimension(200, 20));
        add(label);
        add(progressBar);

        setAlignmentX(Component.RIGHT_ALIGNMENT);
    }

    public Component getListCellRendererComponent(JList list, Object ob, int index, boolean selected, boolean focus) {

        ListOpponent accumulated = (ListOpponent) ob;

        if (selected) {
            label.setText(getSelectedText(accumulated.getIndex() + ". " + accumulated.getName()));
            setBackground(list.getSelectionBackground());
        } else {
            label.setText(getUnSelectedText(accumulated.getIndex() + ". " + accumulated.getName()));
            setBackground(list.getBackground());
        }


        label.setAlignmentX(JLabel.LEFT_ALIGNMENT);

        progressBar.setValue((int)accumulated.getValue());
        progressBar.setString("" + accumulated.getValue());
        progressBar.setStringPainted(true);
        progressBar.setMaximum((int) accumulated.getMax());

        return this;
    }

    private String getSelectedText(String t) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<html><b>");
        buffer.append(t);
        buffer.append("</b></html>");

        return buffer.toString();
    }

    private String getUnSelectedText(String t) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<html>");
        buffer.append(t);
        buffer.append("</html>");

        return buffer.toString();
    }
}
