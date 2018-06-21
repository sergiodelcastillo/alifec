package alifec.contest.view;

import alifec.core.contest.Contest;
import alifec.core.exception.ConfigFileException;
import alifec.core.exception.ValidationException;
import alifec.core.simulation.Agar;
import alifec.core.simulation.NutrientDistribution;
import alifec.core.simulation.nutrient.Nutrient;
import alifec.core.validation.ContestNameValidator;
import alifec.core.validation.ContestPathValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;

import static java.lang.Integer.parseInt;

public class DialogPreferences extends JDialog implements ActionListener {
    private static final long serialVersionUID = 0L;

    Logger logger = LogManager.getLogger(getClass());

    private ContestUI father;
    private JTextField nameOfContest;
    private JTextField defaultPath;
    private JComboBox<String> defaultPause;
    private JComboBox<String> modeOfContest;
    private JCheckBox[] nutrients;
    private ContestNameValidator contestNameValidator;
    private ContestPathValidator contestPathValidator;

    private final String[] time = new String[]
            {"200", "400", "600", "800", "1000",
                    "1200", "1400", "1600", "1800", "2000"};

    private JButton accept = new JButton("Accept");
    private JButton cancel = new JButton("Cancel");

    public DialogPreferences(ContestUI father) throws IOException {
        super(father, "Preferences ", true);
        //todo: fix issue to save nutrients and load them
        //todo: after update then the batlle ui does not update the battles.
        this.father = father;
        this.contestNameValidator = new ContestNameValidator();
        this.contestPathValidator = new ContestPathValidator();

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
        defaultPause = new JComboBox<>(time);
        defaultPause.setSelectedItem(father.getContest().getTimeWait() + "");
        defaultPause.setMaximumSize(max);
        defaultPause.setSelectedItem(father.getContest().getTimeWait() + "");


        JLabel labelDefaultMode = new JLabel("Default Mode");
        modeOfContest = new JComboBox<>(new String[]{"Programmer", "Competition"});
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
        java.util.List<NutrientDistribution> currentNutrients = father.getContest().getCurrentNutrients();
        java.util.List<NutrientDistribution> allNutrients = father.getContest().getAllNutrients();

        this.nutrients = new JCheckBox[allNutrients.size()];

        nutrientPanel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        GroupLayout.SequentialGroup sequentialGroup = layout.createSequentialGroup();
        GroupLayout.ParallelGroup parallelGroup = layout.createParallelGroup();

        int i = 0;

        for (NutrientDistribution nutrient : allNutrients) {
            nutrients[i] = new JCheckBox(nutrient.toString());
            nutrients[i].setSelected(currentNutrients.contains(nutrient));
            sequentialGroup.addComponent(nutrients[i]);
            parallelGroup.addComponent(nutrients[i]);
            i++;
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
            try {
                contestNameValidator.validate(nameOfContest.getText());
                contestPathValidator.validate(defaultPath.getText());

                if (updateConfig()) {
                    father.reloadConfig();
                    father.getBattleUI().updateNutrients();
                    logger.info("Update Config [OK]");
                }

                dispose();
            } catch (ValidationException ex) {
                logger.warn(ex.getMessage(), ex);
                Message.printErr(this, ex.getMessage());
            } catch (Exception ex) {
                logger.error("Cant reload contest: " + ex.getMessage(), ex);
            }
        }
    }

    private boolean updateConfig() {
        Contest contest = father.getContest();
       //todo: remove this field from ui: String path = defaultPath.getText();
        String name = nameOfContest.getText();
        int mode = this.modeOfContest.getSelectedIndex();
        int pause = parseInt(defaultPause.getSelectedItem().toString());

        ArrayList<Integer> nutrientsIds = new ArrayList<>();

        for (JCheckBox nutrient : nutrients) {
            if (nutrient.isSelected()) {
                Nutrient nutri = Agar.getNutrientByName(nutrient.getText());
                if (nutri != null) nutrientsIds.add(nutri.getId());
            }
        }

        return contest.updateConfigFile(name, mode, pause, nutrientsIds);
    }

}
