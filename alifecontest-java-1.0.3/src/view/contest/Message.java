/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */
package view.contest;

import javax.swing.*;
import java.awt.*;

public class Message {


    public static void printErr(Component f, String err) {
        JOptionPane.showMessageDialog(f, err, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void printOK(Component f, String ok) {
        JOptionPane.showMessageDialog(f, ok, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public static int printYesNoCancel(Component f, String msg) {
        return JOptionPane.showConfirmDialog(f, msg, "Option", JOptionPane.YES_NO_CANCEL_OPTION);
    }

    public static String printChoose(Object[] pos, String txt) {
        String result = (String) JOptionPane.showInputDialog(null, txt, "Select an option",
                JOptionPane.PLAIN_MESSAGE, null, pos, pos[0]);

        if (result == null) result = "";
        return result;
    }
}
