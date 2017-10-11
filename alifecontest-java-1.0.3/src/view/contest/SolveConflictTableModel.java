package view.contest;

import javax.swing.table.DefaultTableModel;

/**
 * Created by IntelliJ IDEA.
 * User: yeyo
 * Email: yeyo@druidalabs.com
 * Date: Oct 22, 2010
 * Time: 1:25:41 AM
 */
public class SolveConflictTableModel extends DefaultTableModel {

    public SolveConflictTableModel(Object[][] row, Object... column) {
        super(row,  column);
    }

    @Override
    public Class getColumnClass(int columnIndex) {
        return (columnIndex == 0) ? Boolean.class : String.class;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column == 0;
    }
}