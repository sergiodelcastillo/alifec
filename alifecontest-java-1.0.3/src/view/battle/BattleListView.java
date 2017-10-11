package view.battle;

import view.battle.BattleListCellRenderer;

import javax.swing.*;

/**
 * Created by Sergio Del Castillo
 * User: yeyo
 * Date: Jun 18, 2010
 * Time: 3:29:30 PM
 * Email: yeyo@druidalabs.com
 */
public class BattleListView extends JList {
    public BattleListView(ListModel listModel) {
        super(listModel);
        setCellRenderer(new BattleListCellRenderer());
    }

    @Override
    public void updateUI() {
        
        super.updateUI();

        if (this.getModel().getSize() > 0 && this.getSelectedIndex() < 0) {
            this.setSelectedIndex(getModel().getSize() - 1);
        }
    }

    @Override
    public DefaultListModel getModel() {
        return (DefaultListModel) super.getModel();    
    }
}
