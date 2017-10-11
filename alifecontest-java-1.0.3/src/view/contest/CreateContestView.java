/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

package view.contest;

import controller.java.Validator;
import controller.java.contest.Contest;
import data.java.Config;
import data.java.Log;
import exceptions.CreateConfigException;
import exceptions.ContestException;
import view.components.DialogView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.Calendar;

public class CreateContestView extends DialogView implements ActionListener, KeyListener {
    private static final long serialVersionUID = 0L;
    private JPanel centerPanel = new JPanel();
    private JPanel southPanel = new JPanel();
    private JLabel labelName = new JLabel("Name of Contest");
    private JLabel labelPath = new JLabel("Path");
    private JTextField textContest = new JTextField("Contest-");
    private JTextField textName = new JTextField("" + Calendar.getInstance().get(Calendar.YEAR));
    private JTextField textPath = new JTextField(Config.getAbsoluteDefaultPath());
    private JButton buttonOK = new JButton("Ok");
    private JButton buttonCancel = new JButton("Cancel");
    private JCheckBox examples = new JCheckBox("Generate examples", true);

    private boolean status = false;

    public CreateContestView(JFrame father) {
        super(father, "New Contest ");

        initComponents();

        this.textName.setText("01");
        this.setLocationRelativeTo(father);
        this.setResizable(false);
        this.setVisible(true);
    }

    public boolean exists(String name) {
        return new File(textPath.getText() + File.separator + "Contest-" + name).exists();
    }

    private void initComponents() {
        buttonOK.addActionListener(this);
        buttonCancel.addActionListener(this);
        textName.addKeyListener(this);

        textContest.setEditable(false);
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
                                addComponent(examples)).
                        addGroup(centerLayout.createParallelGroup().
                                addGroup(centerLayout.createSequentialGroup().
                                        addComponent(textContest).addComponent(textName)).
                                addComponent(textPath)));

        centerLayout.setVerticalGroup(
                centerLayout.createSequentialGroup().
                        addGroup(centerLayout.createParallelGroup().
                                addComponent(labelName).
                                addComponent(textContest).
                                addComponent(textName)).
                        addGroup(centerLayout.createParallelGroup().
                                addComponent(labelPath).
                                addComponent(textPath)).
                        addComponent(examples));

        textContest.setMaximumSize(new Dimension(70, 26));
        textName.setMaximumSize(new Dimension(255, 26));
        textPath.setMaximumSize(new Dimension(330, 26));
        this.setMinimumSize(new Dimension(600, 190));
    }

    public void actionPerformed(ActionEvent event) {
        if (event.getSource().equals(this.buttonOK)) {
            if (exists(textName.getText())) {
                Message.printErr(this, "Contest-" + textName.getText() + " folder already exists");
                return;
            }

            String tmp ="Contest-" + textName.getText();

            if (Validator.validateContestName(tmp)) {
                try {
                    Contest.create(textPath.getText(), tmp, examples.isSelected());
                    Log.println("Creating contest [OK]");

                    Config.update(tmp);
                    Log.println("Creating config [OK]");
                    status = true;
                    dispose();
                } catch (ContestException e) {
                    Log.printlnAndSave(e.getMessage(), e.getCause());
                    Message.printErr(this, "Cant create the Contest");
                    status = false;
                } catch (CreateConfigException e) {
                    Log.printlnAndSave(e.getMessage(), e.getCause());
                    Message.printErr(this, "Cant create the Config");
                    status = false;
                }
            } else {
                Message.printErr(this, "The name of contest must contains only letters or digits");
            }


        } else if (event.getSource().equals(this.buttonCancel)) {
            this.dispose();
            status = false;
        }
    }

    public void keyTyped(KeyEvent e) {

    }

    public void keyPressed(KeyEvent e) {
        if (e.getSource().equals(this.textName)) {
            StringBuffer name = new StringBuffer(textName.getText());

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

    public boolean getStatus() {
        return status;
    }
}

