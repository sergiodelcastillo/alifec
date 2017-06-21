/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */
package lib.contest;

import lib.Agar;
import lib.nutrients.Nutrient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;

public class DialogPreferences extends JDialog implements ActionListener {
    private static final long serialVersionUID = 0L;

    private ContestUI father;
    private JTextField nameOfContest;
    private JTextField defaultPath;
    private JComboBox defaultPause;
    private JComboBox modeOfContest;
    private JCheckBox[] nutrients = new JCheckBox[Agar.nutrient.length];

    private final String[] time = new String[]{"200", "400", "600", "800",
            "1000", "1200", "1400", "1600",
            "1800", "2000"};

    private JButton accept = new JButton("Accept");
    private JButton cancel = new JButton("Cancel");

    public DialogPreferences(ContestUI father) throws IOException {
        super(father, "Preferences ", true);
        this.father = father;

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        this.addEscapeKey();
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

        int posX = father.getX() + (father.getWidth() - getWidth()) / 2;
        int posY = father.getY() + (father.getHeight() - getHeight()) / 2;

        setLocation(posX, posY);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setVisible(true);
    }

    private void addEscapeKey() {
        KeyStroke escape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);

        AbstractAction escapeAction = new AbstractAction() {
            private static final long serialVersionUID = 7L;

            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        };

        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escape, "ESCAPE");
        getRootPane().getActionMap().put("ESCAPE", escapeAction);
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
        nameOfContest = new JTextField(father.getContest().getName());
        nameOfContest.setMaximumSize(max);

        JLabel labelDefaultPath = new JLabel("Default Path: ");
        defaultPath = new JTextField(father.getContest().getPath());
        defaultPath.setMaximumSize(max);

        JLabel labelDefaultPause = new JLabel("Pause Between Battles ");
        defaultPause = new JComboBox(time);
        defaultPause.setSelectedItem(father.getContest().getTimeWait() + "");
        defaultPause.setMaximumSize(max);
        defaultPause.setSelectedItem(father.getContest().getTimeWait() + "");


        JLabel labelDefaultMode = new JLabel("Default Mode");
        modeOfContest = new JComboBox(new String[]{"Programmer", "Competition"});
        modeOfContest.setMaximumSize(max);
        modeOfContest.setSelectedIndex(father.getContest().getMode());

        labelDefaultMode.setMaximumSize(d);
        labelDefaultMode.setMinimumSize(d);
        labelDefaultPath.setMaximumSize(d);
        labelDefaultPath.setMinimumSize(d);
        labelNameOfContest.setMaximumSize(d);
        labelNameOfContest.setMinimumSize(d);
        labelDefaultPause.setMaximumSize(d);
        labelDefaultPause.setMinimumSize(d);


        layout.setHorizontalGroup(
                layout.createParallelGroup().
                        addGroup(layout.createSequentialGroup().
                                addComponent(labelNameOfContest).
                                addComponent(nameOfContest)).
                        addGroup(layout.createSequentialGroup().
                                addComponent(labelDefaultPath).
                                addComponent(defaultPath)).
                        addGroup(layout.createSequentialGroup().
                                addComponent(labelDefaultPause).
                                addComponent(defaultPause)).
                        addGroup(layout.createSequentialGroup().
                        addComponent(labelDefaultMode).
                        addComponent(modeOfContest))
        );

        layout.setVerticalGroup(layout.createSequentialGroup().
                addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).
                        addComponent(labelNameOfContest).
                        addComponent(nameOfContest)).
                addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).
                        addComponent(labelDefaultPath).
                        addComponent(defaultPath)).
                addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).
                        addComponent(labelDefaultPause).
                        addComponent(defaultPause)).
                addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).
                addComponent(labelDefaultMode).
                addComponent(modeOfContest)));

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
        JRadioButton java2D = new JRadioButton("java2D (Doubble Buffer)");
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

    private Component createNutrientsPanel() throws IOException {
        JPanel nutrientPanel = new JPanel();
        GroupLayout layout = new GroupLayout(nutrientPanel);
        Hashtable<String, Integer> selNutrients = father.getContest().getNutrients();

        nutrientPanel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        GroupLayout.SequentialGroup sequentialgroup = layout.createSequentialGroup();
        GroupLayout.ParallelGroup parallelgroup = layout.createParallelGroup();

        for (int i = 0; i < Agar.nutrient.length; i++) {
            Nutrient n = Agar.nutrient[i];
            nutrients[i] = new JCheckBox(n.toString());

            if (selNutrients.containsKey(n.toString()))
                nutrients[i].setSelected(true);

            sequentialgroup.addComponent(nutrients[i]);
            parallelgroup.addComponent(nutrients[i]);
        }

        layout.setHorizontalGroup(parallelgroup);
        layout.setVerticalGroup(sequentialgroup);
        nutrientPanel.setBorder(BorderFactory.createTitledBorder("Nutrient Options "));

        return nutrientPanel;
    }


    @Override
    public void actionPerformed(ActionEvent ev) {
        if (ev.getSource().equals(cancel)) {
            dispose();
        }
        if (ev.getSource().equals(accept)) {
            if (!nameOfContest.getText().toLowerCase().startsWith("lib/contest")) {
                Message.printErr(this, "The Contest's name must be begin with Contest");
                return;
            }
            if (!validateNameOfContest(nameOfContest.getText())) {
                Message.printErr(this, "The Contest's name must contain only numbers or letters and \"-\" or \"_\"");
                return;
            }
            String path = defaultPath.getText();

            if (!validateDefaultPath(path)) {
                Message.printErr(this, "The PATH of Contest is invalid");
                return;
            }

            if (updateConfig()) {
                System.out.println("Update Config [OK]");
                try {
                    father.reloadConfig();
                } catch (Exception ex) {
                    System.out.println("cant reload contest");
                }
            }
            if (updateNutrient()) {
                father.getBattleUI().updateNutrients();
            }
            dispose();
        }
    }

    public boolean validateDefaultPath(String p) {
        File f = new File(p);

        return !(!f.exists() || !f.isDirectory()) && Contest.existConfig(p);
    }
    
    public boolean validateNameOfContest(String s) {
        Pattern p = Pattern.compile("[^A-Za-z0-9\\_\\-]+");
        Matcher m = p.matcher(s);

        return !m.find();
    }

    private boolean updateConfig() {
        Contest contest = father.getContest();
        String path = defaultPath.getText();
        String name = nameOfContest.getText();
        int mode = this.modeOfContest.getSelectedIndex();
        int pause = parseInt(defaultPause.getSelectedItem().toString());

        return contest.updateConfig(path, name, mode, pause);
    }

    private boolean updateNutrient() {
        try {
            Contest contest = father.getContest();
            int size = 0;

            // count the selected nutrients_list.
            for (JCheckBox nut : this.nutrients)
                if (nut.isSelected()) ++size;

            int[] nutrients_list = new int[size];
            int j = 0;

            for (int nut = 0; nut < nutrients_list.length; nut++) {
                while (!this.nutrients[j].isSelected()) ++j;
                nutrients_list[nut] = Agar.nutrient[j].getID();
                j++;
            }

            contest.updateNutrient(nutrients_list);
        } catch (IOException ex) {
            return false;
        }
        return true;
    }
}
