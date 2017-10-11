package view.battle;

import controller.java.battle.BattleRun;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Sergio Del Castillo
 * Mail: yeyo@druidalabs.com
 * Date: Sep 23, 2010
 * Time: 11:14:19 PM
 */
public class BattleListCellRenderer extends JLabel implements ListCellRenderer {
    public BattleListCellRenderer() {
        this.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 0));
        this.setOpaque(true);
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object ob, int index, boolean selected, boolean focus) {
        BattleRun battle = (BattleRun) ob;
        if (selected) {
            this.setText(getSelectedText(battle.getFirstName(), battle.getSecondName(), battle.getNutrientName()));
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            this.setText(getUnSelectedText(battle.getFirstName(), battle.getSecondName(), battle.getNutrientName()));
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        return this;
    }

 /*   private String getSelectedText(String a, String b, String n) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<html><font color=\"B22222\"><b> ");
        buffer.append(a);
        buffer.append("</b></font> vs <font color=\"00008B\"><b>");
        buffer.append(b);
        buffer.append("</b></font> in <font color=\"2F4F4F\"><b>");
        buffer.append(n);
        buffer.append("</b></font></html>");

        return buffer.toString();
    }
    private String getUnSelectedText(String a, String b, String n) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<html><b> ");
        buffer.append(a);
        buffer.append("</b> vs <b>");
        buffer.append(b);
        buffer.append("</b> in <b>");
        buffer.append(n);
        buffer.append("</b></html>");

        return buffer.toString();
    }*/
    private String getSelectedText(String a, String b, String n) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<html><b> ");
        buffer.append(a);
        buffer.append("</b> vs <b>");
        buffer.append(b);
        buffer.append("</b> in <b>");
        buffer.append(n);
        buffer.append("</b></html>");

        return buffer.toString();
    }
    private String getUnSelectedText(String a, String b, String n) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<html> ");
        buffer.append(a);
        buffer.append(" vs ");
        buffer.append(b);
        buffer.append(" in ");
        buffer.append(n);
        buffer.append("</html>");

        return buffer.toString();
    }
}
