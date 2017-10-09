
package alifec.core.contest;

import alifec.core.contest.oponentInfo.OpponentInfo;
import alifec.core.contest.oponentInfo.OpponentInfoManager;
import alifec.core.contest.oponentInfo.OpponentReportLine;
import alifec.core.contest.tournament.Tournament;
import alifec.core.contest.tournament.TournamentManager;
import alifec.core.exception.*;
import alifec.core.persistence.ContestConfig;
import alifec.core.persistence.ZipHelper;
import alifec.core.simulation.Agar;
import alifec.core.simulation.Environment;
import alifec.core.simulation.nutrients.Nutrient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


public class Contest {

    private Logger logger = LogManager.getLogger(getClass());

    private Environment environment;
    /**
     * manager of tournaments
     */
    private TournamentManager tournaments;

    /**
     * info of all opponents
     */
    private OpponentInfoManager opponentsInfo;

    private ContestConfig config;

    public Contest(ContestConfig config) throws CreateContestException {
        try {
            this.config = config;
            opponentsInfo = new OpponentInfoManager(config);
            environment = new Environment(config);
            tournaments = new TournamentManager(config);

            //create new and empty tournament
            tournaments.newTournament(environment.getNames());

            try {
                opponentsInfo.read();
            } catch (IOException e) {
                logger.info("Could not load competitors file. A new file will be created: "
                        + e.getMessage());
            }

            Enumeration<Integer> ids = environment.getOps().elements();
            while (ids.hasMoreElements()) {
                Integer i = ids.nextElement();
                opponentsInfo.add(environment.getName(i), environment.getAuthor(i), environment.getAffiliation(i));
            }
        } catch (CreateTournamentException e) {
            throw new CreateContestException("Error creating the contest: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new CreateContestException("Error creating the contest: Can load opponents information, please check the log for further details.", e);
        }
    }


    public Hashtable<String, Integer> getNutrients() {

        Hashtable<String, Integer> nutri = new Hashtable<>();
        String nutrientsPath = config.getNutrientsFilePath();

        try {
            FileReader fr = new FileReader(nutrientsPath);
            BufferedReader in = new BufferedReader(fr);
            String line;

            while ((line = in.readLine()) != null) {
                line = line.trim();
                if (!line.equals("")) {
                    Integer i = new Integer(line);

                    for (Nutrient n : Agar.nutrient) {
                        if (i == n.getID())
                            nutri.put(n.toString(), n.getID());
                    }

                }
            }
            fr.close();
        } catch (IOException e) {
            logger.error("File not Found: " + nutrientsPath, e);
        }

        return nutri;
    }


    /**
     * Reload the configuration. If the config is not valid then it is discarded.
     *
     * @return
     * @throws IOException
     */

    public boolean reloadConfig() throws IOException {
        try {
            config = ContestConfig.buildFromFile(config.getPath());
            return true;
        } catch (ConfigFileException ex) {
            logger.error(ex.getMessage(), ex);
            return false;
        }
    }


    /**
     * update the config file into the proyect.
     *
     * @param path  url of contest
     * @param name  name of contest
     * @param mode  mode of contest(programmer or competition)
     * @param pause default pause between battles
     * @return true if is successfully
     */
    public boolean updateConfigFile(String path, String name, int mode, int pause) {
        config.setPath(path);
        config.setContestName(name);
        config.setMode(mode);
        config.setPauseBetweenBattles(pause);

        try {
            config.save();
        } catch (ConfigFileException e) {
            logger.error("Error updating config file: " + e.getConfig(), e);
            return false;
        }
        return true;
    }

    public void setMode(int mode) throws IOException {
        this.config.setMode(mode);
        this.tournaments.setMode(mode);

    }

    public TournamentManager getTournamentManager() {
        return tournaments;
    }

    public Environment getEnvironment() {
        return environment;
    }


    public int getMode() {
        return config.getMode();
    }

    public String getName() {
        return config.getContestName();
    }

    public int getTimeWait() {
        return config.getPauseBetweenBattles();
    }

    /**
     * @return information
     * @throws CreateRankingException if can not create the ranking
     */
    public List<OpponentReportLine> getInfo() throws CreateRankingException {
        Hashtable<String, Integer> ranking = tournaments.getRanking();
        List<OpponentReportLine> info = new ArrayList<>();

        Tournament t = tournaments.lastElement();
        Hashtable<String, Float> accumulated = t.getAccumulatedEnergy();

        for (OpponentInfo oi : opponentsInfo.getOpponents()) {
            boolean hasRanking = ranking.containsKey(oi.getName());
            boolean hasAccumulated = accumulated.containsKey(oi.getName());

            info.add(new OpponentReportLine(oi.getName(),
                    oi.getAuthor(),
                    oi.getAffiliation(),
                    hasRanking ? ranking.get(oi.getName()) : 0,
                    hasAccumulated ? accumulated.get(oi.getName()) : 0.0f));
        }

        // sort by accumulated points
        Collections.sort(info);

        //The biggest first
        Collections.reverse(info);

        return info;
    }

    /**
     * It generates a zip file of the source code of the participants.
     * The destination file is "Contest-name" / Backup / backup-MMaahhmmss.zip
     * where MM: Month, yy: year, hh: hour, mm: minutes and ss: seconds today.
     *
     * @return true if is successfully
     */
    public boolean createBackUp() {

        //generate the back up...
        try {
            ZipHelper.zipContest(config);
            return true;
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
            return false;
        }
    }

    public boolean needRestore() {
        return tournaments.size() != 0 && tournaments.lastElement().hasBackUpFile();
    }

    public String getPath() {
        return config.getPath();
    }

    public ContestConfig getConfig() {
        return config;
    }
}

