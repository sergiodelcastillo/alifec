
package alifec.contest.view;

import alifec.core.compilation.CompilationResult;
import alifec.core.compilation.CompileHelper;
import alifec.core.contest.Battle;
import alifec.core.contest.Contest;
import alifec.core.exception.*;
import alifec.core.persistence.ContestFileManager;
import alifec.core.persistence.config.ContestConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;


public class ContestUI extends JFrame implements ActionListener {

    Logger logger = LogManager.getLogger(getClass());

    private static final long serialVersionUID = 0L;

    private TournamentUI tUI;

    private BattleUI battleUI;

    private Contest contest;

    private MessagePanel messagePanel = new MessagePanel();

    private JMenuItem newContest;
    private JMenuItem setDefaultContest;

    private JMenuItem quit;
    private JMenuItem report;
    private JMenuItem preferences;
    private JMenuItem help;
    private JMenuItem about;
    private JRadioButtonMenuItem programmerMode;
    private JRadioButtonMenuItem competitionMode;
    private java.util.List<String> excluded = new ArrayList<>();

    public static void main(String[] args) {
        configLogging();
        SwingUtilities.invokeLater(ContestUI::new);
    }

    private static void configLogging() {
        if (System.getProperty("log4j.configurationFile") == null) {
            System.setProperty("log4j.configurationFile", "file:app/log4j2.xml");
        }
    }

    public ContestUI() {
        super("Alifecontest-java 0.02");

        logSystemProperties();

        logger.info("Starting App");
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
            setVisible(true);
        } else {
            System.exit(0);
        }
    }

    private void logSystemProperties() {
        if (logger.isTraceEnabled()) {
            logger.trace("System Properties:");
            Properties p = System.getProperties();
            Enumeration keys = p.keys();
            while (keys.hasMoreElements()) {
                String key = (String) keys.nextElement();
                String value = (String) p.get(key);
                logger.trace(" < " + key + ": " + value + " >");
            }
        }
    }

    private boolean loadContest() {
        String path = ContestConfig.getDefaultPath();

        if (path == null) return false;

        ContestConfig config = null;

        try {
            //Perform the best effort to load a contest.
            config = new ContestConfig(path);

        } catch (ConfigFileException ex) {
            if (!(ex.getCause() instanceof FileNotFoundException) &&
                    !(ex.getCause() instanceof ValidationException)) {
                logger.error(ex.getMessage(), ex);
                if (!Message.printYesNoCancel(null, ex.getMessage() +
                        ". Select 'Yes' to overwrite the existing config file.")) {
                    return false;
                }
            }
        }

        try {
            if (config == null) {
                java.util.List<String> list = ContestFileManager.listContest(path);

                if (list.isEmpty()) {
                    config = createNewContest(null);

                    if (config == null) return false;

                } else {
                    String name = list.get(0);

                    if (list.size() > 1) {
                        name = Message.printChoose(list.toArray());

                        if (name.isEmpty()) {
                            logger.warn("Canceled by user. The system will stop.");
                            return false;
                        }
                    }

                    config = new ContestConfig(path, name);
                    config.save();
                }
            }
            createContest(config);
        } catch (TournamentException | CreateContestException | ConfigFileException ex) {
            logger.error(ex.getMessage(), ex);
            Message.printErr(null, ex.getMessage());
            return false;
        }

        return true;
    }

    private ContestConfig createNewContest(ContestUI contestUI) {
        DialogNewContest dialogNewContest = new DialogNewContest(contestUI);
        ContestConfig config = dialogNewContest.doTheJob();
        boolean makeDefault;

        try {
            if (config == null) {
                return null;
            }

            makeDefault = dialogNewContest.isMakeDefault();

            ContestFileManager.buildNewContestFolder(config, dialogNewContest.isCreateExamples());

            if (makeDefault) {
                boolean status = setDefaultContest(config);

                if (!status) return null;
            }
        } catch (CreateContestFolderException e) {
            Message.printErr(this, "Failed to create the contest " + config.toString());
            logger.error("Failed to create the contest " + config.toString());
            logger.error(e.getMessage(), e);
            return null;
        }

        return config;
    }

    public void createContest(ContestConfig config) throws TournamentException, CreateContestException {
        if (config == null)
            throw new CreateContestException("The config file is null");


        CompileHelper compiler = new CompileHelper(config);
        CompilationResult result = compiler.compileMOs();

        //todo: improve it .. it will show as dialogs windows as errors it have but it should show one dialog with all errors.
        if (result.haveErrors()) {
            for (String error : result.getJavaMessages()) {
                Message.printErr(null, error);
            }
            for (String error : result.getCppMessages()) {
                Message.printErr(null, error);
            }
        }

        //create an instance of the contest
        contest = new Contest(config);

        Battle failed = contest.getUnsuccessfulBattle();

        if (failed != null) {
            UnsuccessfulColoniesSolverUI solver = new UnsuccessfulColoniesSolverUI(this, failed.getFirstColony(), failed.getSecondColony());
            solver.setVisible(true);

            //remove the colony which was excluded from the Dialog "UnsuccessfulColoniesSolverUI".
            for (String excludedColony : excluded) {
                contest.getEnvironment().delete(excludedColony);
            }
        }


        //deleteFromBattlesFile backup file

        //TODO: it seems that it is not necesary:
        // todo TournamentFileManager.deleteBattleBackupFile(config, uColonies);


    }

    private boolean setDefaultContest(String path, String name) {
        // The new contest file will be saved but it will be loaded after the restart of the application.
        // Now the application will continue without any changes at runtime.
        ContestConfig config = new ContestConfig(path, name);

        try {
            config.validate();

        } catch (ValidationException ex) {
            logger.error(ex.getMessage(), ex);
            logger.error(config.toString());
            Message.printErr(this, ex.getMessage());
            return false;
        }
        return setDefaultContest(config);
    }

    /**
     * @param config
     * @return
     */
    private boolean setDefaultContest(ContestConfig config) {
        try {
            // The new contest file will be saved but it will be loaded after the restart of the application.
            // Now the application will continue without any changes at runtime.

            config.save();

            logger.info("The contest file was updated as follows: " + config.toString());

        } catch (ConfigFileException ex) {
            logger.error("Can not create the contest file: " + ex.getConfig().toString(), ex);
            Message.printErr(null, "Can not create the contest file: " + ex.getConfig().toString());
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
                            logger.error("Cannot create the backup file");
                }
            });

            getContentPane().add(messagePanel, BorderLayout.SOUTH);
            getContentPane().add(battleUI, BorderLayout.EAST);
            getContentPane().add(tUI, BorderLayout.CENTER);
            setTitle(contest.getName());
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public boolean reloadConfig() throws IOException {
        boolean status = contest.reloadConfig();
        setTitle(contest.getName());
        return status;
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
        setDefaultContest = new JMenuItem("Set Default Contest");
        quit = new JMenuItem("Quit");

        newContest.addActionListener(this);
        setDefaultContest.addActionListener(this);
        quit.addActionListener(this);


        newContest.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        setDefaultContest.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        quit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_DOWN_MASK));


        menu.add(newContest);
        menu.add(setDefaultContest);
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
            createNewContest(this);
            requestRestart();
        } else if (e.getSource().equals(setDefaultContest)) {
            JFileChooser fc = new JFileChooser();
            fc.setApproveButtonText("Set default contest");
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                String contestPath = fc.getCurrentDirectory().getAbsolutePath();
                String fileName = fc.getSelectedFile().getName();
                if (this.setDefaultContest(contestPath, fileName)) {
                    requestRestart();
                }
            }
        } else if (e.getSource().equals(quit)) {
            if (contest.getMode() == ContestConfig.COMPETITION_MODE)
                if (!contest.createBackUp())
                    logger.error("Cannot create the backup file");

            System.exit(EXIT_ON_CLOSE);
        } else if (e.getSource().equals(programmerMode)) {
            try {
                contest.setMode(ContestConfig.PROGRAMMER_MODE);
            } catch (IOException ex) {
                logger.error("Failed to set the contest mode: " + ex.getMessage(), ex);
                Message.printErr(this, "Failed to set the contest mode: " + ex.getMessage());
            }
        } else if (e.getSource().equals(competitionMode)) {
            try {
                contest.setMode(ContestConfig.COMPETITION_MODE);
            } catch (IOException ex) {
                logger.error("Failed to set the contest mode: " + ex.getMessage(), ex);
                Message.printErr(this, "Failed to set the contest mode: " + ex.getMessage());
            }
        } else if (e.getSource().equals(report)) {
            new ContestReportUI(this);
        } else if (e.getSource().equals(preferences)) {
            try {
                new DialogPreferences(this);
            } catch (IOException ex) {
                logger.error(ex.getMessage(), ex);
                Message.printErr(this, "Failed to change the configuration.");
            }
        } else if (e.getSource().equals(help)) {
            Message.printErr(this, "not Soported yet");
        } else if (e.getSource().equals(about)) {
            new DialogAbout(this);
        }
    }

    private void requestRestart() {
        logger.warn("Please restart the application in order to load the new changes.");
        Message.printInfo(this, "Please restart the application in order to load the new changes.");
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
        } catch (ClassNotFoundException |
                InstantiationException |
                IllegalAccessException |
                UnsupportedLookAndFeelException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public void excludeColony(String colonyName) {
        excluded.add(colonyName);
    }
}
