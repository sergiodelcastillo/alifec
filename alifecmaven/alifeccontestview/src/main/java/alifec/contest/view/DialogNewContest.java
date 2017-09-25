/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

package alifec.contest.view;

import alifec.core.persistence.ContestConfig;
import alifec.core.persistence.filter.ContestFolderFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Calendar;

public class DialogNewContest extends JDialog implements ActionListener, KeyListener {
    private static final long serialVersionUID = 0L;
    Logger logger = LogManager.getLogger(getClass());

    private JPanel centerPanel = new JPanel();
    private JPanel southPanel = new JPanel();
    private JLabel labelName = new JLabel("Name of Contest");
    private JLabel labelPath = new JLabel("Path");
    private JLabel labelAccepted = new JLabel("             ");
    private JTextField textContest = new JTextField(ContestConfig.CONTEST_NAME_PREFIX);
    private JTextField textName = new JTextField("" + Calendar.getInstance().get(Calendar.YEAR));
    private JTextField textPath = new JTextField(System.getProperty("user.dir"));
    private JButton buttonBrowse = new JButton("Browse");
    private JButton buttonOK = new JButton("Ok");
    private JButton buttonCancel = new JButton("Cancel");
    private JCheckBox checkLoad = new JCheckBox("Load new Contest", true);
    private JCheckBox examples = new JCheckBox("Generate examples", true);
    private ContestConfig config;
    private ContestFolderFilter validator;

    private boolean createExamples;
    private boolean makeDefault;

    public DialogNewContest(JFrame father) {
        super(father, "New Contest ", true);

        validator = new ContestFolderFilter(false);
        cleanResult();

        initComponents();

        this.textName.setText("01");
        if (father != null)
            this.setLocation(father.getX() + (father.getWidth() - getWidth()) / 2,
                    father.getY() + (father.getHeight() - getHeight()) / 2);
        this.setResizable(false);

    }

    /**
     * Enable the Dialog and create the ContestConfig to return it so it can be created.
     *
     * @return a new configuration which points to the contest that the user wants to set as the default one.
     */
    public ContestConfig doTheJob() {
        cleanResult();
        setVisible(true);

        return config;
    }

    private void cleanResult() {
        config = null;
        makeDefault = false;
        createExamples = false;
    }

    private void initComponents() {
        buttonOK.addActionListener(this);
        buttonCancel.addActionListener(this);
        buttonBrowse.addActionListener(this);
        textName.addKeyListener(this);
        addEscapeKey();

        textContest.setEditable(false);
        textContest.setEnabled(false);
        textPath.setEditable(false);

        GroupLayout centerLayout = new GroupLayout(centerPanel);
        centerPanel.setLayout(centerLayout);
        centerLayout.setAutoCreateContainerGaps(true);
        centerLayout.setAutoCreateGaps(true);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(BorderLayout.CENTER, centerPanel);
        getContentPane().add(BorderLayout.SOUTH, southPanel);

        GroupLayout southLayout = new GroupLayout(southPanel);
        southPanel.setLayout(new FlowLayout());
        southLayout.setAutoCreateContainerGaps(true);
        southLayout.setAutoCreateGaps(true);
        southPanel.add(this.buttonOK);
        southPanel.add(this.buttonCancel);

        centerLayout.setHorizontalGroup(
                centerLayout.createSequentialGroup().
                        addGroup(centerLayout.createParallelGroup().
                                addComponent(labelName).
                                addComponent(labelPath).
                                addComponent(checkLoad)).
                        addGroup(centerLayout.createParallelGroup().
                                addGroup(centerLayout.createSequentialGroup().
                                        addComponent(textContest).addComponent(textName).addComponent(labelAccepted)).
                                addGroup(centerLayout.createSequentialGroup().
                                        addComponent(textPath).addComponent(buttonBrowse)).
                                addGroup(centerLayout.createSequentialGroup().
                                        addComponent(examples)))

        );

        centerLayout.setVerticalGroup(
                centerLayout.createSequentialGroup().
                        addGroup(centerLayout.createParallelGroup().
                                addComponent(labelName).
                                addComponent(textContest).
                                addComponent(textName).
                                addComponent(labelAccepted)).
                        addGroup(centerLayout.createParallelGroup().
                                addComponent(labelPath).
                                addComponent(textPath).
                                addComponent(buttonBrowse)).
                        addGroup(centerLayout.createParallelGroup().
                                addComponent(checkLoad).addComponent(examples)));

        textContest.setMaximumSize(new Dimension(70, 26));
        textName.setMaximumSize(new Dimension(255, 26));
        textPath.setMaximumSize(new Dimension(330, 26));
        this.setMinimumSize(new Dimension(600, 190));
    }

    public void actionPerformed(ActionEvent ev) {
        if (ev.getSource().equals(this.buttonOK)) {
            String contestFolderName = ContestConfig.CONTEST_NAME_PREFIX+ textName.getText();
            String contestFolderRoot = textPath.getText();
            String contestPath = ContestConfig.getContestPath(contestFolderRoot, contestFolderName);

            if (validator.validate(contestPath)) {
                config = ContestConfig.buildNewConfigFile(contestFolderRoot, contestFolderName);
                createExamples = examples.isSelected() ? Boolean.TRUE : Boolean.FALSE;
                makeDefault = checkLoad.isSelected() ? Boolean.TRUE : Boolean.FALSE;

                dispose();
            } else {
                logger.warn("The contest [" + contestPath + "] already exists or is not valid. Please select another one.");
                Message.printErr(this, "The contest [" + contestPath + "] already exists or is not valid. Please select another one.");
            }

        } else if (ev.getSource().equals(this.buttonBrowse)) {
            JFileChooser fc = new JFileChooser();
            fc.setApproveButtonText("Open Contest");
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
                textPath.setText(fc.getSelectedFile().getAbsolutePath());

        } else if (ev.getSource().equals(this.buttonCancel)){
            logger.info("Canceled by user.");
            cleanResult();
            this.dispose();
        }
    }

    public void keyTyped(KeyEvent e) {

    }

    public void keyPressed(KeyEvent e) {
        if (e.getSource().equals(this.textName)) {
            StringBuilder name = new StringBuilder(textName.getText());

            for (int i = name.length() - 1; i >= 0; i--) {
                if (!Character.isLetterOrDigit(name.charAt(i)) && name.charAt(i) != '(' && name.charAt(i) != ')') {
                    name.deleteCharAt(i);
                    textName.setText(name.toString());
                }
            }
        }
    }

    public void keyReleased(KeyEvent e) {
    }

    private void addEscapeKey() {
        KeyStroke escape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);

        AbstractAction escapeAction = new AbstractAction() {
            private static final long serialVersionUID = 8L;

            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        };

        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escape, "ESCAPE");
        getRootPane().getActionMap().put("ESCAPE", escapeAction);
    }

    public ContestConfig getContestConfig() {
        return config;
    }

    public boolean isCreateExamples() {
        return createExamples;
    }

    public boolean isMakeDefault() {
        return makeDefault;
    }
}

