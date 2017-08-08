/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

package alifec.contest.view;

import alifec.core.contest.*;
import alifec.core.exception.CreateContestException;
import alifec.core.exception.CreateTournamentException;
import alifec.core.exception.TournamentCorruptedException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
    private java.util.List<String> excluded = new ArrayList<>();

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
            setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
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

            //Find the absolute path to load the contest
            if (!ContestHelper.existConfigFile(path)) {
                java.util.List<String> list = ContestHelper.listContest(path);
                String name;

                if (list.isEmpty()) {
                    Vector<Object> results = new Vector<>();
                    new DialogNewContest(null, results);

                    if (results.isEmpty())
                        return false;

                    name = (String) results.elementAt(0);

                    Contest.createContestFolder((String) results.elementAt(1), name, ((Boolean) results.elementAt(3)));

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

            createContest(path);
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

    public void createContest(String path) throws CreateTournamentException, IOException, CreateContestException {
        ContestConfig config = ContestConfig.build(path);
        if (!config.isValid())
            throw new CreateContestException("Bad config file. You could delete the following file:\n"
                    + path + File.separator + ContestConfig.CONFIG_FILE);

        CompilationResult result = CompileHelper.compileMOs(config.getMOsPath());

        //todo: improve it .. it will show as dialogs windows as errors it have but it should show one dialog with all errors.
        if (result.haveErrors()) {
            for (String error : result.getJavaMessages()) {
                Message.printErr(null, error);
            }
            for (String error : result.getCppMessages()) {
                Message.printErr(null, error);
            }
        }

        try {
            //load everything
            UnsuccessfulColonies uColonies = ContestHelper.findFinishedUnsuccessful(config);

            if (uColonies.isUnsuccessful()) {
                UnsuccessfulColoniesSolverUI solver = new UnsuccessfulColoniesSolverUI(this, uColonies.getColonyA(), uColonies.getColonyB());
                solver.setVisible(true);

                for (String c : contest.getEnvironment().getNames()) {
                    contest.getTournamentManager().lastElement().addColony(c);
                }
            }
            //create an instance of the contest
            contest = new Contest(config);

            //remove the colony which was excluded from the Dialog "UnsuccessfulColoniesSolverUI".
            for (String excludedColony : excluded) {
                contest.getEnvironment().delete(excludedColony);
            }

            //delete backup file
            ContestHelper.deleteBattleBackupFile(config, uColonies);

        } catch (TournamentCorruptedException e) {
            //TODO: ver que mostrar acá.
            System.out.println(e.getMessage());
            Message.printErr(null, e.getMessage());
            System.exit(0);
        }


    }

    private boolean reloadContest(String path, String name) {
        try {
            if (!ContestHelper.existConfigFile(path)) {
                if (!Contest.createConfig(path, name)) {
                    Message.printErr(null, "Can't create the config file... Can't continue");
                    return false;
                }
            } else {
                contest.updateConfig(path, name, contest.getMode(), contest.getTimeWait());
            }

            createContest(path);

        } catch (IOException ex) {
            Message.printErr(null, "IO Exception: " + ex.getMessage());
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
                    if (contest.getMode() == ContestConfig.COMPETITION_MODE)
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

    private JMenuBar createMenu() {
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
        quit = new JMenuItem("Quit");

        newContest.addActionListener(this);
        openContest.addActionListener(this);
        quit.addActionListener(this);


        newContest.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        openContest.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        quit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_DOWN_MASK));


        menu.add(newContest);
        menu.add(openContest);
        menu.addSeparator();
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

        if (contest.getMode() == ContestConfig.PROGRAMMER_MODE) {
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
            Vector<Object> result = new Vector<>();
            new DialogNewContest(this, result);

            if (result.size() == 4) {
                String name = (String) result.elementAt(0);
                String path = (String) result.elementAt(1);
                Boolean load = (Boolean) result.elementAt(2);
                Boolean examples = (Boolean) result.elementAt(3);

                if (Contest.createContestFolder(path, name, examples)) {
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
            if (contest.getMode() == ContestConfig.COMPETITION_MODE)
                if (!contest.createBackUp())
                    System.out.println("Error: cannot create the backup file");

            System.exit(EXIT_ON_CLOSE);
        } else if (e.getSource().equals(programmerMode)) {
            contest.setMode(ContestConfig.PROGRAMMER_MODE);
        } else if (e.getSource().equals(competitionMode)) {
            contest.setMode(ContestConfig.COMPETITION_MODE);
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

            //set languages
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

    public void excludeColony(String colonyName) {
        excluded.add(colonyName);
    }
}
