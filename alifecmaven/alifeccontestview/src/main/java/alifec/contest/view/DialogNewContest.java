/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

package alifec.contest.view;

import alifec.core.contest.ContestConfig;
import alifec.core.contest.ContestFolderFilter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.Calendar;
import java.util.Vector;

public class DialogNewContest extends JDialog implements ActionListener, KeyListener {
    private static final long serialVersionUID = 0L;
    private JPanel centerPanel = new JPanel();
    private JPanel southPanel = new JPanel();
    private JLabel labelName = new JLabel("Name of Contest");
    private JLabel labelPath = new JLabel("Path");
    private JLabel labelAccepted = new JLabel("             ");
    private JTextField textContest = new JTextField(ContestFolderFilter.CONTEST_PREFIX);
    private JTextField textName = new JTextField("" + Calendar.getInstance().get(Calendar.YEAR));
    private JTextField textPath = new JTextField(System.getProperty("user.dir"));
    private JButton buttonBrowse = new JButton("Browse");
    private JButton buttonOK = new JButton("Ok");
    private JButton buttonCancel = new JButton("Cancel");
    private JCheckBox checkLoad = new JCheckBox("Load new Contest", true);
    private JCheckBox examples = new JCheckBox("Generate examples", true);

    private Vector<Object> result;

    public DialogNewContest(JFrame father, final Vector<Object> url) {
        super(father, "New Contest ", true);
        this.result = url;

        initComponents();

        this.textName.setText("01");
        if (father != null)
            this.setLocation(father.getX() + (father.getWidth() - getWidth()) / 2,
                    father.getY() + (father.getHeight() - getHeight()) / 2);
        this.setResizable(false);
        this.setVisible(true);
    }

    public boolean exists(String name) {
        //TODO: Usar el path del ContestConfig!!
        return new File(textPath.getText() + File.separator + ContestFolderFilter.CONTEST_PREFIX + name).exists();
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

            if (!exists(textName.getText())) {
                if (validate(textName.getText())) {
                    //TODO: usar contestconfig
                    result.addElement(ContestFolderFilter.CONTEST_PREFIX + textName.getText());
                    result.addElement(textPath.getText());
                    result.addElement(checkLoad.isSelected() ? Boolean.TRUE : Boolean.FALSE);
                    result.addElement(examples.isSelected() ? Boolean.TRUE : Boolean.FALSE);
                    dispose();
                } else {
                    Message.printErr(this, "The name of contest must be only letter or digit");
                }
            } else {
                Message.printErr(this, ContestFolderFilter.CONTEST_PREFIX + textName.getText() + " folder already exists");
            }

        } else if (ev.getSource().equals(this.buttonBrowse)) {
            JFileChooser fc = new JFileChooser();
            fc.setApproveButtonText("Open Contest");
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
                textPath.setText(fc.getSelectedFile().getAbsolutePath());

        } else if (ev.getSource().equals(this.buttonCancel)) {
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

    private boolean validate(String name) {
        if (name == null || name.equalsIgnoreCase(""))
            return false;

        for (Character c : name.toCharArray()) {
            if (!Character.isLetter(c) && !Character.isDigit(c) && c != '_')
                return false;
        }
        return true;
    }
}

