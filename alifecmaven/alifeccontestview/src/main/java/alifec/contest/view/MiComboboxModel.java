package alifec.contest.view;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.util.Enumeration;
import java.util.Hashtable;

public class MiComboboxModel extends AbstractListModel<String> implements ComboBoxModel<String> {
    private static final long serialVersionUID = 0L;

    Logger logger = LogManager.getLogger(getClass());

    Hashtable<String, Integer> model;

    String selected = "";

    public MiComboboxModel(Hashtable<String, Integer> m) {
        if (m == null || m.size() == 0)
            logger.debug("There are no elements");

        model = m;

        if (model != null) {
            if (model.keys().hasMoreElements())
                selected = model.keys().nextElement();
        }
    }

    @Override
    public int getSize() {
        return model.size();
    }

    @Override
    public String getElementAt(int index) {
        if (model.size() < index) {
            logger.warn("error: MiComboboxModel.getElementAt(" + index + ")");
            return null;
        }

        Enumeration<String> e = model.keys();

        for (int i = 0; i < index; i++)
            e.nextElement();

        return e.nextElement();
    }

    @Override
    public void setSelectedItem(Object anItem) {
        if (anItem != null)
            selected = anItem.toString();
    }

    @Override
    public Object getSelectedItem() {
        return selected;
    }

    public boolean remove(String colony) {
        return model.remove(colony) != null;
    }

}

