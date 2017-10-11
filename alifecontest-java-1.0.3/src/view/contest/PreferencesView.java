/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */
package view.contest;

import controller.java.Agar;
import controller.java.Validator;
import controller.java.nutrients.Nutrient;
import data.java.Config;
import data.java.Log;
import exceptions.ContestException;
import exceptions.CreateConfigException;
import exceptions.LoadNutrientsException;
import exceptions.SaveNutrientsException;
import view.components.DialogView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Enumeration;

import static java.lang.Integer.parseInt;

public class PreferencesView extends DialogView implements ActionListener {
    private static final long serialVersionUID = 0L;

    private ContestView father;
    private JTextField contestName;
    private JComboBox pause;
    private JComboBox contestMode;
    private JCheckBox[] nutrients = new JCheckBox[Agar.getCount()];

    private final String[] time = new String[]{
            "200", "400", "600", "800",
            "1000", "1200", "1400", "1600",
            "1800", "2000", "2200", "2400",
            "2600", "2800", "3000",
            "3500", "4000", "5000",};

    private JButton accept = new JButton("Accept");
    private JButton cancel = new JButton("Cancel");

    public PreferencesView(ContestView father) {
        super(father, "Preferences ");
        this.father = father;

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        accept.addActionListener(this);
        cancel.addActionListener(this);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("General", createGeneralPanel());
        tabbedPane.addTab("Nutrients", createNutrientsPanel());
        tabbedPane.addTab("Graphics", createGraphicsPanel());

        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).
                addComponent(tabbedPane).
                addGroup(layout.createSequentialGroup().
                        addComponent(accept).
                        addComponent(cancel)));

        layout.setVerticalGroup(layout.createSequentialGroup().
                addComponent(tabbedPane).
                addGroup(layout.createParallelGroup().
                        addComponent(accept).addComponent(cancel)));

        pack();
        this.setLocationRelativeTo(father);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setVisible(true);
    }


    private Component createGeneralPanel() {
        JPanel general = new JPanel();
        GroupLayout layout = new GroupLayout(general);
        general.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        Dimension d = new Dimension(170, 25);
        Dimension max = new Dimension(300, 25);

        JLabel labelNameOfContest = new JLabel("Name of Contest");
        contestName = new JTextField(father.getContest().getName());
        contestName.setMaximumSize(max);


        JLabel labelDefaultPause = new JLabel("Pause Between Battles ");
        pause = new JComboBox(time);
        pause.setSelectedItem(Config.getInstance().getPauseBetweenBattles() + "");
        pause.setMaximumSize(max);
        pause.setSelectedItem(Config.getInstance().getPauseBetweenBattles() + "");


        JLabel labelDefaultMode = new JLabel("Default Mode");
        contestMode = new JComboBox(new String[]{"Programmer", "Competition"});
        contestMode.setMaximumSize(max);
        contestMode.setSelectedIndex(father.getContest().getMode());

        labelDefaultMode.setMaximumSize(d);
        labelDefaultMode.setMinimumSize(d);
        labelNameOfContest.setMaximumSize(d);
        labelNameOfContest.setMinimumSize(d);
        labelDefaultPause.setMaximumSize(d);
        labelDefaultPause.setMinimumSize(d);


        layout.setHorizontalGroup(
                layout.createParallelGroup().
                        addGroup(layout.createSequentialGroup().
                                addComponent(labelNameOfContest).
                                addComponent(contestName)).
                        addGroup(layout.createSequentialGroup().
                                addComponent(labelDefaultPause).
                                addComponent(pause)).
                        addGroup(layout.createSequentialGroup().
                                addComponent(labelDefaultMode).
                                addComponent(contestMode))
        );

        layout.setVerticalGroup(layout.createSequentialGroup().
                addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).
                        addComponent(labelNameOfContest).
                        addComponent(contestName)).
                addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).
                        addComponent(labelDefaultPause).
                        addComponent(pause)).
                addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).
                        addComponent(labelDefaultMode).
                        addComponent(contestMode)));

        general.setBorder(BorderFactory.createTitledBorder("Default Options "));
        return general;
    }

    private Component createGraphicsPanel() {
        JPanel graphics = new JPanel();
        GroupLayout layout = new GroupLayout(graphics);

        graphics.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        ButtonGroup group = new ButtonGroup();
        JRadioButton openGL = new JRadioButton("OpenGL");
        JRadioButton java2D = new JRadioButton("java2D");
        JRadioButton java3D = new JRadioButton("Java3D");

        openGL.setEnabled(false);
        java3D.setEnabled(false);
        java2D.setSelected(true);

        group.add(openGL);
        group.add(java2D);
        group.add(java3D);

        layout.setVerticalGroup(layout.createSequentialGroup().
                addComponent(openGL).
                addComponent(java2D).
                addComponent(java3D));

        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING).
                        addComponent(openGL).
                        addComponent(java2D).
                        addComponent(java3D));

        graphics.setBorder(BorderFactory.createTitledBorder("Graphics Options"));

        return graphics;
    }

    private Component createNutrientsPanel() {
        JPanel nutrientPanel = new JPanel();
        GroupLayout layout = new GroupLayout(nutrientPanel);

        nutrientPanel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        GroupLayout.SequentialGroup sequentialGroup = layout.createSequentialGroup();
        GroupLayout.ParallelGroup parallelGroup = layout.createParallelGroup();

        int index = 0;
        for (Enumeration<Nutrient> en = Agar.getAllNutrients(); en.hasMoreElements(); index++) {
            Nutrient n = en.nextElement();
            nutrients[index] = new JCheckBox(n.toString());

            if (father.getContest().getEnvironment().getAgar().contain(n.getId())) {
                nutrients[index].setSelected(true);
            }

            sequentialGroup.addComponent(nutrients[index]);
            parallelGroup.addComponent(nutrients[index]);
        }

        layout.setHorizontalGroup(parallelGroup);
        layout.setVerticalGroup(sequentialGroup);
        nutrientPanel.setBorder(BorderFactory.createTitledBorder("Nutrient Options "));

        return nutrientPanel;
    }


    @Override
    public void actionPerformed(ActionEvent ev) {
        if (ev.getSource().equals(cancel)) {
            dispose();
        }
        if (ev.getSource().equals(accept)) {
            if (!Validator.validateContestName(contestName.getText())) {
                Message.printErr(this, "The Contest's name must contain numbers, letters, \"-\" or \"_\"");
                return;
            }



            try {
                String name = contestName.getText();
                int mode = this.contestMode.getSelectedIndex();
                int pause = parseInt(this.pause.getSelectedItem().toString());

                Config.update(name, mode, pause);
                father.renameContestTo(name);


                Log.println("\nUpdate Config [OK]");

                father.getContest().reloadConfig();

                ArrayList<String> nutrients = new ArrayList<String>();

                // Get the selected nutrients_list.
                for (JCheckBox nut : this.nutrients) {
                    if (nut.isSelected())
                        nutrients.add(nut.getText());
                }

                father.getContest().saveNutrients(nutrients);
                father.getBattleView().updateNutrients();

            } catch (CreateConfigException e) {
                Log.printlnAndSave(e.getMessage(), e.getCause());
            } catch (SaveNutrientsException e) {
                Log.println(e.getMessage(), e.getCause());
            } catch (LoadNutrientsException e) {
                Log.println(e.getMessage(), e.getCause());
            } catch (ContestException e) {
                Log.println(e.getMessage(), e.getCause());
            }

            dispose();
        }

    }
}
