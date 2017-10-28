package alifec.contest.view;


import alifec.core.simulation.Competitor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.util.List;

public class ColonyComboboxModel extends AbstractListModel<String> implements ComboBoxModel<String> {
    private static final long serialVersionUID = 0L;

    Logger logger = LogManager.getLogger(getClass());

    private List<Competitor> model;

    private Competitor selected;

    public ColonyComboboxModel(List<Competitor> list) {
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
        if (anItem != null) {
            for (Competitor competitor : model)
                if (competitor.getColonyName().equals(anItem))
                    selected = competitor;
        }
    }

    @Override
    public Object getSelectedItem() {
        return selected;
    }

    public boolean remove(String colony) {
        for (Competitor op : model) {
            if (op.getColonyName().equals(colony))
                return model.remove(op);
        }

        return false;
    }

}

