/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

package controller.java.contest;


import controller.java.Agar;
import controller.java.Environment;
import controller.java.battle.BattleManager;
import controller.java.battle.BattleRun;
import controller.java.compiler.Compiler;
import controller.java.oponentInfo.OpponentInfoManager;
import controller.java.tournament.TournamentManager;
import data.java.AllFilter;
import data.java.Config;
import data.java.Defs;
import data.java.Log;
import data.java.contest.RankingDTO;
import exceptions.*;

import java.io.*;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class Contest {


    public static enum Report {
        TXT, CSV;

    }

    /**
     * Environment ...
     */
    private Environment environment;

    /**
     * manager of tournaments
     */
    private TournamentManager tournaments;

    /**
     * info of all opponents
     */
    private OpponentInfoManager opponentsInfo;

    /**
     * name of contest
     */
    private String name;
    /**
     * mode of contest:
     * programmer (mode = 0): the competitor should use this mode.
     * competition(mode = 1): reserved to compete.
     */
    private int mode = 0;

    public Contest() throws ContestException {

        if (!reloadConfig()) {
            throw new ContestException("Can not read the config file");
        }

        compile();

        try {
            opponentsInfo = new OpponentInfoManager();
            environment = new Environment();
            tournaments = new TournamentManager(mode, true);
            opponentsInfo.load();
            opponentsInfo.add(environment.getColonies(), mode);

        } catch (CreateOpponentInfoException e) {
            throw new ContestException("Create the contest [FAIL]", e.getCause());
        } catch (CreateEnvironmentException e) {
            throw new ContestException("Create the contest [FAIL]", e.getCause());
        } catch (LoadNutrientsException e) {
            throw new ContestException("Create the contest [FAIL]", e.getCause());
        } catch (LoadOpponentInfoException e) {
            throw new ContestException("Create the contest [FAIL]", e.getCause());
        } catch (LoadTournamentException e) {
            throw new ContestException("Create the contest [FAIL]", e.getCause());
        } catch (SaveOpponentInfoException e) {
            throw new ContestException("Create the contest [FAIL]", e.getCause());
        }

    }


    /**
     * Compile the C/C++ and Java implementations.
     */
    private void compile() {
        Compiler.getInstance().cleanBinaries();

        if (AllFilter.listJavaColonies().length > 0) {
            Log.println("Contest name: " + name + "\nCompile JAVA Files");

            // compile java MOs
            if (Compiler.getInstance().compileJava()) {
                Log.println("Compile [OK]");
            } else {
                Log.println("Compile [FAIL]");
            }
        }

        if (AllFilter.listCppColonies().length > 0) {
            Log.println("\nCompile C++ Files\nUpdate C++ Files: ");

            // compile cpp MOs
            if (Compiler.getInstance().makeCppLibrary(mode == ContestMode.COMPETITION_MODE ? Compiler.Type.RELEASE : Compiler.Type.DEBUG)) {
                Log.println("Updating includes and making libcppcolonies [OK]");
            } else
                Log.println("Updating includes and making libcppcolonies [FAIL]");
        }
    }

    /**
     * Create a new tournament.
     *
     * @throws HasConflictException if the last tournament was crashed.
     */
    public void initLastTournament() throws HasConflictException {
        if (tournaments.getSize() > 0) {
            if (tournaments.getLastTournament().hasBackUpFile() && isCompetitionMode()) {
                Log.printlnAndSave("The last tournament has a conflict. You need restore it to continue");
                throw new HasConflictException("The last tournament has a conflict. You need restore it to continue");
            }
        }

        newTournament();
    }

    public void newTournament() {
        try {
            tournaments.newTournament(environment.getNames());
            Log.println("Creating new Tournament [OK]");
        } catch (CreateTournamentException ex) {
            Log.printlnAndSave(ex.getMessage(), ex);
        }
    }

    /**
     * @return
     * @throws FailResolveConflictException
     */
    public ArrayList<BattleRun> getBattlesRemaining() throws FailResolveConflictException {
        String name = tournaments.getLastTournament().getName();
        ArrayList<BattleRun> backup;
        ArrayList<BattleRun> battles;

        try {
            battles = BattleManager.loadBattleRun(Config.getInstance().getAbsoluteBattleFile(name));
            backup = BattleManager.loadBattleRun(Config.getInstance().getAbsoluteBattleBackUp(name));
        } catch (IOException ex) {
            String log = "The application can not access to the backup files to resolve the conflict in the last tournament and will be closed";
            Log.println(log);
            Log.save(log, ex);
            throw new FailResolveConflictException(log);
        }

        normalizeBattlesWithBackUp(battles, backup);

        if (backup.equals(battles)) {
            backup.clear(); //empty remaining...
            tournaments.getLastTournament().deleteBackUp();
        } else {
            if (backup.size() < battles.size()) {
                String log = "The application can not resolve the conflict in the last tournament and will be closed because there are corrupt lines.";
                Log.printlnAndSave(log);
                throw new FailResolveConflictException(log);
            }

            for (int index = 0; index < battles.size(); index++) {
                if (!backup.get(0).equals(battles.get(index))) {
                    String log = "The application can not resolve the conflict in the last tournament and will be closed because there are corrupt lines.";
                    Log.printlnAndSave(log);
                    throw new FailResolveConflictException(log);
                } else {
                    backup.remove(0);
                }
            }
        }

        return backup;
    }

    /**
     * Remove old battles that was run.
     *
     * @param battles
     * @param backup
     */
    private void normalizeBattlesWithBackUp(ArrayList<BattleRun> battles, ArrayList<BattleRun> backup) {
        if (backup.isEmpty() || battles.isEmpty()) return;

        BattleRun b = backup.get(0);
        ArrayList<BattleRun> toDel = new ArrayList<BattleRun>();

        for (int i = 0; i < battles.size(); i++) {
            if (b.equals(battles.get(i))) {
                break;
            }
            toDel.add(battles.get(i));
        }

        battles.removeAll(toDel);
    }

    public void addColonyIds(ArrayList<BattleRun> backup) {
        ArrayList<BattleRun> toDel = new ArrayList<BattleRun>();

        for (int i = 0; i < backup.size(); i++) {
            if (!environment.addIds(backup.get(i))) {
                Log.println("The battle: " + backup.get(i) + " can not be restored.");
                toDel.add(backup.get(i));
            }
        }
        backup.removeAll(toDel);
    }

    public boolean deleteBackup() {
        if (tournaments.getLastTournament().getBattleManager().deleteBackUp()) {
            Log.println("Delete back up file [OK]");
            return true;
        } else {
            Log.println("Delete back up file [FAIL]");
            return false;
        }
    }

    public static boolean existContest() {
        File contest = new File(Config.getInstance().getAbsoluteContestName());
        File mos = new File(Config.getInstance().getAbsoluteMOsFolder());
        File report = new File(Config.getInstance().getAbsoluteReportFolder());
        File nutrients = new File(Config.getInstance().getAbsoluteNutrient());

        return isValid(contest, mos, report, nutrients);
    }

    private static boolean isValid(File contest, File mos, File report, File nut) {
        return contest.exists() &&
                contest.isDirectory() &&
                contest.canRead() &&
                contest.canWrite() &&
                mos.exists() &&
                mos.isDirectory() &&
                mos.canRead() &&
                mos.canWrite() &&
                report.exists() &&
                report.isDirectory() &&
                report.canRead() &&
                report.canWrite() &&
                nut.exists() &&
                nut.isFile() &&
                nut.canRead() &&
                nut.canWrite();
    }


    public static boolean existContest(String path) {
        File contest = new File(path);
        File mos = new File(path + File.separator + Config.MOS_FOLDER);
        File report = new File(path + File.separator + Config.REPORT_FOLDER);
        File nutrients = new File(path + File.separator + Config.NUTRIENTS_FILE);

        return isValid(contest, mos, report, nutrients);
    }

    /**
     * Read the config File in the project.
     *
     * @return true if is successfully
     * @throws IOException if can not find the config file
     */
    public boolean reloadConfig() {

        if (Config.getInstance() == null) {
            return false;
        }
        this.name = Config.getInstance().getContestName();
        this.mode = Config.getInstance().getMode();
        return true;
    }

    /**
     * Create the structure of the contest
     *
     * @param path     the absolute path of the contest
     * @param name     the name of the new contest
     * @param examples generate examples if examples is true
     * @return if was successful
     */
    public static void create(String path, String name, boolean examples) throws ContestException {
        try {
            String folder = path + File.separator + name;

            if (!new File(folder).mkdir()) {
                throw new ContestException("Cant create the contest");
            }

            if (!new File(folder + File.separator + Config.MOS_FOLDER).mkdir()) {
                throw new ContestException("Cant create the contest");
            }
            if (!new File(folder + File.separator + Config.REPORT_FOLDER).mkdir()) {
                throw new ContestException("Cant create the contest");
            }


            Agar.createNutrientFile(folder + File.separator + Config.NUTRIENTS_FILE);

            if (examples)
                CodeGenerator.generateExamples(folder + File.separator + Config.MOS_FOLDER);

        } catch (IOException ex) {
            throw new ContestException("cant create the contest", ex);
        }
    }

    public void saveNutrients(ArrayList<String> nutrients) throws LoadNutrientsException, SaveNutrientsException {
        this.environment.getAgar().saveByName(nutrients);
        this.environment.getAgar().load();
    }


    /**
     * name,author,affiliation, accumulatedPoints, lastEnergy
     *
     * @return information
     * @throws exceptions.CreateRankingException
     *          if can not create the ranking
     */
    public ArrayList<RankingDTO> getRanking() throws CreateRankingException {
        ArrayList<RankingDTO> ranking = tournaments.getRanking();

        opponentsInfo.addInfo(ranking);

        return ranking;
    }

    /**
     * It generates a zip file of the source code of the participants.
     * The destination file is "Contest-name" / Backup / backup-MMaahhmmss.zip
     * where MM: Month, yy: year, hh: hour, mm: minutes and ss: seconds today.
     *
     * @return true if is successfully
     */
    public boolean createBackUp() {
        File folder = new File(Config.getInstance().getAbsoluteBackUpFolder());
        ArrayList<String[]> files = new ArrayList<String[]>();
        Deque<File> stack = new ArrayDeque<File>();

        if (!folder.exists()) {
            if (!folder.mkdirs()) {
                return false;
            }
        }

        stack.add(new File(Config.getInstance().getAbsoluteMOsFolder()));

        while (!stack.isEmpty()) {
            File file = stack.pop();
            for (File tmp : AllFilter.listAllFiles(file.getAbsolutePath())) {
                if (tmp.isFile()) {
                    String path = tmp.getAbsolutePath();
                    String name = path.replace(Config.getInstance().getAbsoluteMOsFolder(), "");
                    files.add(new String[]{path, name});
                } else {
                    stack.push(tmp);
                }
            }
        }

        //generate the back up...
        try {
            addToZip(files);
            return true;
        } catch (java.io.IOException ex) {
            return false;
        }
    }

    private void addToZip(ArrayList<String[]> des) throws IOException {
        FileOutputStream destination = new FileOutputStream(Config.getInstance().getAbsoluteMOsBackUpFile());
        ZipOutputStream zo = new ZipOutputStream(new BufferedOutputStream(destination));

        for (String[] file : des) {
            FileInputStream im = new FileInputStream(file[0]);
            ZipEntry ze = new ZipEntry(file[1]);
            zo.putNextEntry(ze);

            //load and write with the buffer of source to source.zip
            BufferedInputStream origin = new BufferedInputStream(im, 500);
            byte[] data = new byte[500];
            int count;
            while ((count = origin.read(data, 0, 500)) != -1) {
                zo.write(data, 0, count);
            }
            origin.close();
        }
        zo.close();
    }

    /**
     * Generate a report file
     *
     * @param type    the report type, can be TXT or CSV
     * @param ranking the ranking table generated by contest.getRanking
     * @throws IOException
     */
    public void createReport(Report type, ArrayList<RankingDTO> ranking) throws IOException {
        switch (type) {
            case TXT:
                createTxtReport(ranking);
                break;
            case CSV:
                createCsvReport(ranking);
                break;
        }
    }

    /**
     * Create CSV report
     *
     * @param ranking the ranking table
     * @throws IOException
     */
    private void createCsvReport(ArrayList<RankingDTO> ranking) throws IOException {
        FileWriter writer = new FileWriter(Config.getInstance().getAbsoluteReportCsv(), false);
        BufferedWriter buffer = new BufferedWriter(writer);
        buffer.append("Contest name:,");
        buffer.append(name);
        buffer.append('\n');

        buffer.append("Last Tournament:,");
        buffer.append(tournaments.getLastTournament().getName());
        buffer.append('\n');
        buffer.append("NAME,AUTHOR,AFFILIATION,POINTS,");
        buffer.append('\n');

        for (RankingDTO r : ranking) {
            buffer.append(r.toCSV());
            buffer.append('\n');
        }

        buffer.close();
        writer.close();
    }

    public void renameTo(String newName) throws ContestException, CreateConfigException {
        File from = new File(Config.getInstance().getAbsoluteContestName(name));

        if (!from.exists() || !from.isDirectory()) {
            throw new ContestException("Directory does not exist: " + from.getAbsolutePath());
        }

        File to = new File(Config.getInstance().getAbsoluteContestName(newName));

        if (name.equals(newName) || to.exists()) {
            throw new ContestException("The contest " + newName + " exists.");
        }

        if (!from.renameTo(to)) {
            throw new ContestException("Can not rename \"" + from.getAbsolutePath() + "\" to \"" + to.getAbsolutePath() + "\".");
        }

        Config config = Config.getInstance();

        setName(newName);
        Config.update(config.getAbsoluteContestPath(), newName, config.getMode(), config.getPauseBetweenBattles());
        Config.readConfig(config.getAbsoluteContestPath() + File.separator + Config.CONFIG_FILE);

        try {
            tournaments.update();
        } catch (IOException e) {
            Log.printlnAndSave("Update error", e);
        }
    }


    /**
     * Create TXT report
     *
     * @param ranking the ranking table
     * @throws IOException
     */

    private void createTxtReport(ArrayList<RankingDTO> ranking) throws IOException {
        FileWriter writer = new FileWriter(Config.getInstance().getAbsoluteReportTxt(), false);
        BufferedWriter buffer = new BufferedWriter(writer);
        buffer.append("Contest name: ");
        buffer.append(name);
        buffer.append('\n');

        buffer.append("Last Tournament: ");
        buffer.append(tournaments.getLastTournament().getName());
        buffer.append("\n\n");
        buffer.append(RankingDTO.getTxtSignature(Defs.MAX_LENGTH));
        buffer.append('\n');

        for (RankingDTO r : ranking) {
            buffer.append(r.toTxt());
            buffer.append('\n');
        }

        buffer.close();
        writer.close();
    }


    /*
    *************************************************************************
    * Metodos Gets																				*
    * *************************************************************************
    */


    public TournamentManager getTournamentManager() {
        return tournaments;
    }

    public void setMode(int mode) throws IOException {
        this.mode = mode;
        this.tournaments.setMode(mode);

    }

    public void setName(String name) {
        this.name = name;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public int getMode() {
        return mode;
    }

    public String getName() {
        return name;
    }

    public boolean needRestore() {
        return tournaments.getSize() != 0 && tournaments.getLastTournament().hasBackUpFile();
    }

    public boolean isCompetitionMode() {
        return this.mode == ContestMode.COMPETITION_MODE;
    }

    public OpponentInfoManager getOpponentsInfo() {
        return opponentsInfo;
    }
}

