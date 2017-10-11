package view.battle;

import controller.java.Colony;
import controller.java.nutrients.Nutrient;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * Created by IntelliJ IDEA.
 * User: yeyo
 * Email: yeyo@druidalabs.com
 * Date: Oct 16, 2010
 * Time: 6:52:16 PM
 */
public class BattleComboBoxModel extends AbstractListModel implements ComboBoxModel {
    private ArrayList<String> names;
    private ArrayList<Integer> ids;
    private int selected;

    private BattleComboBoxModel(){
        this.names = new ArrayList<String>();
        this.ids = new ArrayList<Integer>();
        init();
    }
    public BattleComboBoxModel(ArrayList<Colony> instances) {
        this();

        for (Colony colony : instances) {
            this.names.add(colony.getName());
            this.ids.add(colony.getId());
        }
    }

    public BattleComboBoxModel(Enumeration<Nutrient> instances) {
        this();

        for (; instances.hasMoreElements();) {
            Nutrient nutrient = instances.nextElement();
            this.names.add(nutrient.toString());
            this.ids.add(nutrient.getId());
        }
    }

    private void init() {
        this.names.add("All");
        this.ids.add(-1);
        this.selected = 0;
    }

    @Override
    public int getSize() {
        return names.size();
    }

    @Override
    public Object getElementAt(int index) {
        return names.get(index);
    }

    @Override
    public void setSelectedItem(Object anItem) {
        int index = names.indexOf(anItem);

        if (index >= 0){
            selected = index;
        }
    }

    @Override
    public Object getSelectedItem() {
        if (selected < 0) return null;

        return names.get(selected);
    }

    public String getName(int index) {
        return names.get(index);
    }

    public int getId(int index) {
        return ids.get(index);
    }
}
