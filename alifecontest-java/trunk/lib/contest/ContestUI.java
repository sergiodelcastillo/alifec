/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

package lib.contest;

import exceptions.CreateContestException;
import exceptions.CreateTournamentException;
import lib.battles.BattleUI;
import lib.tournament.TournamentUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ContestUI extends JFrame implements ActionListener {
    private static final long serialVersionUID = 0L;
    /**
     * Interface grafica que representa los encuentros realizados y
     * el que se esta realizando.
     */
    private TournamentUI tUI;

    /**
     * Interfe grafica que permite administrar las batallas
     * que se van a realizar.
     */
    private BattleUI battleUI;

    /**
     * Manejador del Contexto, usa la clase Contest para realizar las
     * operaciones a nivel de constest
     */
    private Contest contest;

    /**
     * Panel de mensages!.. se informan los eventos que van sucediendo.
     */
    private MessagePanel messagePanel = new MessagePanel();

    private JMenuItem newContest;
    private JMenuItem openContest;

    private JMenuItem quit;
    private JMenuItem report;
    private JMenuItem preferences;
    private JMenuItem help;
    private JMenuItem about;
    private JRadioButtonMenuItem programmerMode;
    private JRadioButtonMenuItem competitionMode;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ContestUI();
            }
        });
    }

    public ContestUI() {
        super("Alifecontest-java 0.02");
        setLookAndLanguage();

        if (loadContest()) {
            // If have back up file
            if (contest.needRestore()) {
                setMessage("Restore Ok");
            } else {
                setMessage("Load Ok");
            }

            initComponents();
            pack();
            setMinimumSize(new Dimension(getWidth(), getHeight()));
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            openContest.setEnabled(false);
            setVisible(true);
        } else {
            System.exit(0);
        }
    }

    private boolean loadContest() {
        String path;

        try {
            path = new File(System.getProperty("user.dir")).getCanonicalPath();
            System.out.println("\nLoading contest\nPath: " + path);
        } catch (IOException ex) {
            return false;
        }

        try {
            // poner la direccion absoluta de donde se debe cargar el contest!
            if (!Contest.existConfig(path)) {
                Vector<String> list = Contest.listContest(path);
                String name;

                if (list.isEmpty()) {
                    Vector<Object> results = new Vector<Object>();
                    new DialogNewContest(null, results);

                    if (results.isEmpty())
                        return false;

                    name = (String) results.elementAt(0);

                    Contest.createContest((String) results.elementAt(1), name, ((Boolean) results.elementAt(3)));

                    if (!results.lastElement().equals(Boolean.TRUE))
                        return false;

                } else {
                    name = Message.printChoose(list.toArray());
                    if (name.equalsIgnoreCase(""))
                        return false;
                }
                if (!Contest.createConfig(path, name)) {
                    Message.printErr(null, "Can't create the config file... Can't continue");
                    return false;
                }
            }

            contest = new Contest(path);
        } catch (IOException ex) {
            Message.printErr(null, "Error de lectura...");
            return false;
        } catch (CreateTournamentException ex) {
            Message.printErr(null, ex.getMessage());
            return false;
        } catch (CreateContestException ex) {
            Message.printErr(null, ex.getMessage());
            return false;
        }
        return true;
    }


    private boolean reloadContest(String path, String name) {
        try {
            if (!Contest.existConfig(path)) {
                if (!Contest.createConfig(path, name)) {
                    Message.printErr(null, "Can't create the config file... Can't continue");
                    return false;
                }
            } else {
                contest.updateConfig(path, name, contest.getMode(), contest.getTimeWait());
            }
            contest = new Contest(path);
        } catch (IOException ex) {
            Message.printErr(null, "Error de lectura...");
            return false;
        } catch (CreateTournamentException ex) {
            Message.printErr(null, ex.getMessage());
            return false;
        } catch (CreateContestException ex) {
            Message.printErr(null, ex.getMessage());
            return false;
        }
        return true;
    }

    private void initComponents() {
        try {
            this.tUI = new TournamentUI(this);
            this.battleUI = new BattleUI(this, contest.needRestore());
            this.setJMenuBar(createMenu());
            this.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    if (contest.getMode() == Contest.COMPETITION_MODE)
                        if (!contest.createBackUp())
                            System.out.println("Error: cannot create the backup file");
                }
            });

            getContentPane().add(messagePanel, BorderLayout.SOUTH);
            getContentPane().add(battleUI, BorderLayout.EAST);
            getContentPane().add(tUI, BorderLayout.CENTER);
            setTitle(contest.getName());
        } catch (IOException ex) {
            System.out.println("Error: init components");
        }
    }

    public boolean reloadConfig() throws IOException {
        boolean status = contest.reloadConfig();
        setTitle(contest.getName());
        return status;
    }

    public void reloadComponents() {
        try {

            this.tUI = new TournamentUI(this);
            this.battleUI = new BattleUI(this, contest.needRestore());
            setMenuBar(getMenuBar());
            getContentPane().removeAll();
            getContentPane().add(messagePanel, BorderLayout.SOUTH);
            getContentPane().add(battleUI, BorderLayout.EAST);
            getContentPane().add(tUI, BorderLayout.CENTER);
            setTitle(contest.getName());
            this.repaint();
        } catch (IOException ex) {
            System.out.println("Error: reload components");
        }
    }

    public JMenuBar createMenu() {
        JMenuBar menu = new JMenuBar();
        JMenu menuFile = new JMenu("File");
        JMenu menuTools = new JMenu("Tools");
        JMenu menuHelp = new JMenu("Help");

        createMenuFile(menuFile);
        createMenuTools(menuTools);
        createMenuHelp(menuHelp);

        menuFile.setMnemonic(KeyEvent.VK_F);
        menuTools.setMnemonic(KeyEvent.VK_T);
        menuHelp.setMnemonic(KeyEvent.VK_H);

        menu.add(menuFile);
        menu.add(menuTools);
        menu.add(menuHelp);

        return menu;
    }

    public void createMenuFile(JMenu menu) {
        newContest = new JMenuItem("New Contest");
        openContest = new JMenuItem("Open Contest");
//		  newTournament = new JMenuItem("New Tournament");
//		  closeTournament = new JMenuItem("Close Tournament");
//		  saveAll = new JMenuItem("Save all");
        quit = new JMenuItem("Quit");

        newContest.addActionListener(this);
        openContest.addActionListener(this);
        //      newTournament.addActionListener(this);
        //      closeTournament.addActionListener(this);
        //      saveAll.addActionListener(this);
        quit.addActionListener(this);

//        saveAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
        newContest.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        openContest.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
//        newTournament.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_DOWN_MASK));
//        closeTournament.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
        quit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_DOWN_MASK));

//		  newTournament.setEnabled(false);
        //	  closeTournament.setEnabled(false);
//		  saveAll.setEnabled(false);

        menu.add(newContest);
        menu.add(openContest);
        menu.addSeparator();
        //	  menu.add(newTournament);
//		  menu.add(closeTournament);
        //	  menu.addSeparator();
        //	  menu.add(saveAll);
        //	  menu.addSeparator();
        menu.add(quit);
    }

    public void createMenuTools(JMenu menu) {
        report = new JMenuItem("Report");
        preferences = new JMenuItem("Preferences");
        programmerMode = new JRadioButtonMenuItem("Programmer mode");
        competitionMode = new JRadioButtonMenuItem("Competition mode");
        ButtonGroup group = new ButtonGroup();
        group.add(programmerMode);
        group.add(competitionMode);

        if (contest.getMode() == Contest.PROGRAMMER_MODE) {
            programmerMode.setSelected(true);
        } else {
            competitionMode.setSelected(true);
        }

        preferences.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_MASK));
        report.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_MASK));

        programmerMode.addActionListener(this);
        competitionMode.addActionListener(this);
        report.addActionListener(this);
        preferences.addActionListener(this);

        menu.add(programmerMode);
        menu.add(competitionMode);
        menu.addSeparator();
        menu.add(report);
        menu.addSeparator();
        menu.add(preferences);
    }

    public void createMenuHelp(JMenu menu) {
        help = new JMenuItem("Help");
        about = new JMenuItem("About");

        help.setAccelerator(KeyStroke.getKeyStroke("F1"));
        about.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, InputEvent.CTRL_MASK));

        help.addActionListener(this);
        about.addActionListener(this);

        menu.add(help);
        menu.add(about);
    }


    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(newContest)) {
            Vector<Object> result = new Vector<Object>();
            new DialogNewContest(this, result);

            if (result.size() == 4) {
                String name = (String) result.elementAt(0);
                String path = (String) result.elementAt(1);
                Boolean load = (Boolean) result.elementAt(2);
                Boolean examples = (Boolean) result.elementAt(3);

                if (Contest.createContest(path, name, examples)) {
                    if (load) {
                        this.reloadContest(path, name);
                        this.reloadComponents();
                    }
                } else {
                    Message.printErr(this, "Can' t create the contest: " + name);
                }
            }
        } else if (e.getSource().equals(openContest)) {
            JFileChooser fc = new JFileChooser();
            fc.setApproveButtonText("Open Contest");
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                this.reloadContest(fc.getCurrentDirectory().getAbsolutePath(),
                        fc.getSelectedFile().getName());
                this.reloadComponents();
            }
        } else if (e.getSource().equals(quit)) {
            if (contest.getMode() == Contest.COMPETITION_MODE)
                if (!contest.createBackUp())
                    System.out.println("Error: cannot create the backup file");

            System.exit(EXIT_ON_CLOSE);
        } else if (e.getSource().equals(programmerMode)) {
            contest.setMode(Contest.PROGRAMMER_MODE);
        } else if (e.getSource().equals(competitionMode)) {
            contest.setMode(Contest.COMPETITION_MODE);
        } else if (e.getSource().equals(report)) {
            new ContestReport(this);
        } else if (e.getSource().equals(preferences)) {
            try {
                new DialogPreferences(this);
            } catch (IOException ex) {
                Message.printErr(this, "Failed to change the configuration.");
            }
        } else if (e.getSource().equals(help)) {
            Message.printErr(this, "not Soported yet");
        } else if (e.getSource().equals(about)) {
            new DialogAbout(this);
        }
    }
    /*
         *********************************************************************
         * Metodos get y set!!
         * *********************************************************************
         */

    public synchronized Contest getContest() {
        return contest;
    }

    public synchronized TournamentUI getTournamentUI() {
        return tUI;
    }

    public synchronized BattleUI getBattleUI() {
        return battleUI;
    }

    public void setMessage(String t) {
        messagePanel.setText(t);
    }


    @Override
    public synchronized void repaint() {
        super.repaint();
        this.battleUI.repaint();
        this.tUI.repaint();
    }

    private void setLookAndLanguage() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            JFrame.setDefaultLookAndFeelDecorated(true);
            JDialog.setDefaultLookAndFeelDecorated(true);

            //setear idiomas!!!
            JFileChooser.setDefaultLocale(Locale.ENGLISH);
            JOptionPane.setDefaultLocale(Locale.ENGLISH);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ContestUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(ContestUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(ContestUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(ContestUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
