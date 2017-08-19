package alifec.contest.view;

import javax.swing.table.DefaultTableModel;

/**
 * Created by Sergio Del Castillo on 18/08/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
class SelectColoniesTableModel extends DefaultTableModel {

    public SelectColoniesTableModel(Object[][] row, Object... column) {
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
