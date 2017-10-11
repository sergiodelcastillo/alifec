package view.components;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Sergio Del Castillo
 * Mail: yeyo@druidalabs.com
 * Date: Sep 19, 2010
 * Time: 10:24:17 PM
 */
public class DialogView extends JDialog {

    protected DialogView(JFrame father, String msg) {
        super(father, msg, true);
        addEscapeKey();
    }

    private void addEscapeKey() {
        KeyStroke escape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);

        AbstractAction escapeAction = new AbstractAction() {
            private static final long serialVersionUID = 8L;

            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        };

        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escape, "ESCAPE");
        getRootPane().getActionMap().put("ESCAPE", escapeAction);
    }
}
