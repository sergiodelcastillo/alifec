package alifec.contest.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class SelectColoniesUI extends JTable {

    public SelectColoniesUI(Object[][] rows) {
        super(new SelectColoniesTableModel(rows, "Delete", "Colony"));

        setAutoResizeMode(AUTO_RESIZE_LAST_COLUMN);
        shrinkCell();
        getColumnModel().getColumn(1).setResizable(false);
        getColumnModel().getColumn(0).setResizable(false);
        setCellSelectionEnabled(false);
        setColumnSelectionAllowed(false);
        getTableHeader().setReorderingAllowed(false);
    }

    @Override
    public void doLayout() {
        JTableHeader header = getTableHeader();

        boolean wasNull = false;
        if (header.getResizingColumn() == null &&
                getAutoResizeMode() == AUTO_RESIZE_LAST_COLUMN) {
            int delta = getWidth() - getColumnModel().getTotalColumnWidth();
            if (delta == 0) {
                return;
            } else {
                TableColumnModel model = getColumnModel();
                int lastColumnIndex = model.getColumnCount() - 1;
                header.setResizingColumn(model.getColumn(lastColumnIndex));
            }
            wasNull = true;
        }

        super.doLayout();
        if (wasNull) {
            header.setResizingColumn(null);
        }
    }

    private void shrinkCell() {
        TableColumn column = getColumnModel().getColumn(0);

        if (column != null) {
            column.setWidth(50);
        }
    }

    public boolean isSelected(int id) {
        return !(id < 0 || id > 1) && (Boolean) getModel().getValueAt(id, 0);

    }
}

