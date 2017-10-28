package alifec.contest.view;


import alifec.core.simulation.NutrientDistribution;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.util.List;

public class NutrientComboboxModel extends AbstractListModel<String> implements ComboBoxModel<String> {
    private static final long serialVersionUID = 0L;

    Logger logger = LogManager.getLogger(getClass());

    private List<NutrientDistribution> model;

    private NutrientDistribution selected;

    public NutrientComboboxModel(List<NutrientDistribution> list) {
        model = list;

        if (!model.isEmpty()) {
            selected = list.get(0);
        }
    }

    @Override
    public int getSize() {
        return model.size();
    }

    @Override
    public String getElementAt(int index) {
        return model.get(index).toString();
    }

    @Override
    public void setSelectedItem(Object anItem) {
        if (anItem != null )
            for (NutrientDistribution nutrient : model)
                if (nutrient.getNutrientName().equals(anItem))
                    selected = nutrient;
    }

    @Override
    public Object getSelectedItem() {
        return selected;
    }

    public boolean remove(String colony) {
        for (NutrientDistribution op : model) {
            if (op.getNutrientName().equals(colony))
                return model.remove(op);
        }

        return false;
    }

}

