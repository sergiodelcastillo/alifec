/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

package view.contest;

import controller.java.battle.BattleRun;
import controller.java.contest.Contest;
import controller.java.contest.ContestMode;
import data.java.AllFilter;
import data.java.Config;
import data.java.Log;
import exceptions.*;
import view.Properties;
import view.battle.BattleView;
import view.components.FrameView;
import view.tournament.TournamentView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;

public class ContestView extends FrameView implements ActionListener {
    private static final long serialVersionUID = 0L;

    private TournamentView tournamentView;
    private BattleView battleView;
    private MessageView messageView = new MessageView();

    private Contest contest;

    private JMenuItem newContest;
    private JMenuItem openContest;

    private JMenuItem quit;
    private JMenuItem report;
    private JMenuItem preferences;
    private JMenuItem help;
    private JMenuItem about;
    private JRadioButtonMenuItem programmerMode;
    private JRadioButtonMenuItem competitionMode;


    public ContestView() {
        setTitle("A Life Contest " + Properties.getInstance().getVersionValue());

        if (!checkContest()) {
            Log.println("The application will close because the contest can not start.");
            System.exit(0);
        }

        ArrayList<BattleRun> restore = initContest();

        initComponents(restore);


        pack();
        setMinimumSize(new Dimension(getWidth(), getHeight()));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        //TODO ver esto.
        //  openContest.setEnabled(false);
        setVisible(true);


    }

    private ArrayList<BattleRun> initContest() {
        ArrayList<BattleRun> list = null;
        try {
            contest = new Contest();
        } catch (ContestException e) {
            Log.printlnAndSave("Error when creating a contest", e);
            System.exit(0);
        }

        try {
            contest.initLastTournament();
        } catch (HasConflictException e) {
            try {
                list = contest.getBattlesRemaining();

                if (list.size() > 0) {
                    SolveConflictView solver = new SolveConflictView(contest, list);

                    if (!solver.isRestored()) {
                        list.clear();  // ensure empty backup.
                        contest.newTournament();
                    }
                } else {
                    contest.newTournament();
                }

            } catch (FailResolveConflictException ex) {
                Message.printErr(null, ex.getMessage());
                System.exit(0);
            }
        }

        // If have back up file
        if (contest.needRestore()) {
            setMessage("Restore Ok");
        } else {
            setMessage("Load Ok");
        }

        return list;
    }

    private boolean checkContest() {
        if (Config.existsDefaultConfig()) {
            Config c = Config.getInstance();

            if (c == null) {
                Log.println("Loading the config file " + Config.getAbsoluteDefaultConfig()+ " [FAIL]");
                Log.println("Please delete or edit this file and run the application again.");
                return false;
            }

            if (!Contest.existContest()) {
                Log.println("The config file:" + Config.getAbsoluteDefaultConfig() + " is bad.");
                Log.println("Please delete or edit this file and run the application again.");
                return false;
            }
            return true;
        } else {
            String[] contests = AllFilter.listContests();

            if (contests.length == 0) {
                CreateContestView dialog = new CreateContestView(null);

                return dialog.getStatus();
            } else {
                //select the contest to use.
                Log.println("Select a contest");
                String contestName = Message.printChoose(contests, "Select the Default Contest");

                if (contestName.equals("")) {
                    Log.println("Canceled by user.");
                    return false;
                }

                try {
                    Config.update(contestName);
                    Log.println("Using " + contestName);
                    return true;

                } catch (CreateConfigException e) {
                    Log.printlnAndSave("Create config [FAIL]", e);
                    return false;
                }
            }
        }
    }
    public boolean reload() {
        if(contest != null)
            getContest().getEnvironment().end();

        if(!Config.readConfig(Config.getAbsoluteDefaultConfig()))
            return false;

        ArrayList<BattleRun> restore = initContest();
        initComponents(restore);

        return true;
    }

    private void initComponents(ArrayList<BattleRun> restore) {
        this.tournamentView = new TournamentView(this);
        this.battleView = new BattleView(this, restore);
        this.setJMenuBar(createMenu());

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (contest.isCompetitionMode())
                    if (!contest.createBackUp())
                        System.out.println("Error: cannot create the backup file");
            }
        });

        getContentPane().removeAll();
        getContentPane().add(messageView, BorderLayout.SOUTH);
        getContentPane().add(battleView, BorderLayout.EAST);
        getContentPane().add(tournamentView, BorderLayout.CENTER);
        setTitle(contest.getName());

        this.repaint();
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

        newContest.setEnabled(false);
        openContest.setEnabled(false);

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

        if (contest.getMode() == ContestMode.PROGRAMMER_MODE) {
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
            CreateContestView dialog = new CreateContestView(this);

            if (dialog.getStatus()) {
               reload();
            }
        } else if (e.getSource().equals(openContest)) {
            new OpenContestView(this);
        } else if (e.getSource().equals(quit)) {
            if (contest.getMode() == ContestMode.COMPETITION_MODE)
                if (!contest.createBackUp())
                    System.out.println("Error: cannot create the backup file");

            System.exit(EXIT_ON_CLOSE);
        } else if (e.getSource().equals(programmerMode)) {
            try {
                contest.setMode(ContestMode.PROGRAMMER_MODE);
            } catch (IOException ex) {
                // this will never happen.
                Log.printlnAndSave("", ex);
                Message.printErr(this, "Error: can not save the battles");
            }
        } else if (e.getSource().equals(competitionMode)) {
            try {
                contest.setMode(ContestMode.COMPETITION_MODE);
            } catch (IOException ex) {
                Log.printlnAndSave("", ex);
                Message.printErr(this, "Error: can not save the battles");
            }
        } else if (e.getSource().equals(report)) {
            try {
                new RankingView(this, contest.getRanking());
            } catch (CreateRankingException ex) {
                Message.printErr(this, ex.getMessage());
            }
        } else if (e.getSource().equals(preferences)) {
            new PreferencesView(this);
        } else if (e.getSource().equals(help)) {
            Message.printErr(this, "Not supported yet");
        } else if (e.getSource().equals(about)) {
            new AboutView(this);
        }
    }

    public Contest getContest() {
        return contest;
    }

    public TournamentView getTournamentView() {
        return tournamentView;
    }

    public BattleView getBattleView() {
        return battleView;
    }

    public void setMessage(String t) {
        messageView.setText(t);
    }

    @Override
    public void repaint() {
        super.repaint();
        this.battleView.repaint();
        this.tournamentView.repaint();
    }

    public void renameContestTo(String aName) throws ContestException, CreateConfigException {
        contest.renameTo(aName);
        setTitle(aName);
    }
}
