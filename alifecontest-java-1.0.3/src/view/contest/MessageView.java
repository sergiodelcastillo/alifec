/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */
package view.contest;

import javax.swing.*;
import java.awt.*;

public class MessageView extends JPanel {
    private static final long serialVersionUID = 0L;
    private JLabel text;

    public MessageView() {
        super();
        setLayout(new BorderLayout());
        text = new JLabel(" ", SwingConstants.LEFT);

        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(text);
    }

    public void setText(String t) {
        text.setText(t);
    }
}
