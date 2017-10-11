package view.contest;

import controller.java.contest.Contest;
import controller.java.contest.ContestMode;
import data.java.contest.RankingDTO;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: yeyo
 * Email: yeyo@druidalabs.com
 * Date: Oct 22, 2010
 * Time: 1:13:19 AM
 */
public class RankingTableModel extends AbstractTableModel {
    private ArrayList<RankingDTO> ranking;
    private final String[] columns = new String[]{
            "Name",
            "Author",
            "Affiliation",
            "Points"
    };
    private int mode;

    public RankingTableModel(ArrayList<RankingDTO> ranking, int mode) {
        this.ranking = ranking;
        this.mode = mode;
    }

    public String getColumnName(int column) {
        return columns[column];
    }

    public int getRowCount() {
        return ranking.size();
    }

    public int getColumnCount() {
        return columns.length;
    }

    public Object getValueAt(int row, int col) {
        RankingDTO r = ranking.get(row);

        switch (col) {
            case 0:
                return r.getName();
            case 1:
                return r.getAuthor();
            case 2:
                return r.getAffiliation();
            case 3:
                return r.getPoints();
        }
        return null;
    }

    public boolean isCellEditable(int row, int column) {
        if(column == 3 && mode == ContestMode.COMPETITION_MODE){
            return true;
        }
        return false;
    }

    /*public void setValueAt(Object value, int row, int col) {
        // rowData[row][col] = value;

    }*/
    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

}
