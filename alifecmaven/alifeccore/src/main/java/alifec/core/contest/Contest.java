/**
 * @author Sergio
 * mail@: sergio.jose.delcastillo@gmail.com
 */

package alifec.core.contest;

import alifec.core.contest.oponentInfo.OpponentInfo;
import alifec.core.contest.oponentInfo.OpponentInfoManager;
import alifec.core.contest.oponentInfo.OpponentReportLine;
import alifec.core.contest.tournament.Tournament;
import alifec.core.contest.tournament.TournamentManager;
import alifec.core.exception.*;
import alifec.core.simulation.Agar;
import alifec.core.simulation.Environment;
import alifec.core.simulation.nutrients.Nutrient;
import org.apache.log4j.Logger;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;


public class Contest {

    private Logger logger = Logger.getLogger(getClass());

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

    private ContestConfig config;

    public Contest(ContestConfig config) throws IOException, CreateTournamentException, CreateContestException {
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
        ContestConfig configTmp = ContestConfig.buildFromFile(config.getPath());

        if (configTmp.isValid()) {
            config = configTmp;
            return true;
        }

        return false;
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
        } catch (SaveContestConfigException e) {
            logger.error("Error updating config file: " + e.getConfig(), e);
            return false;
        }
        return true;
    }

    public void updateNutrient(int[] nutrients) throws IOException {
        //todo: improve it
        String url = config.getNutrientsFilePath();

        new File(url).renameTo(new File(url + "_backup"));

        File newNutrient = new File(url);
        PrintWriter pw = new PrintWriter(newNutrient);

        for (int nutriID : nutrients)
            pw.println(nutriID);

        pw.close();

        new File(url + "_backup").delete();
    }

    public void setMode(int mode) throws IOException {
        this.config.setMode(mode);
        this.tournaments.setMode(mode);

    }


    /*
      *************************************************************************
      * Metodos Gets																				*
      * *************************************************************************
      */

    public TournamentManager getTournamentManager() {
        return tournaments;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public String getMOsPath() {
        return config.getMOsPath();
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
     *
     * @return information
     * @throws CreateRankingException if can not create the ranking
     */
    public List<OpponentReportLine> getInfo() throws CreateRankingException {
        Hashtable<String, Integer> ranking = tournaments.getRanking();
        List<OpponentReportLine> info = new ArrayList<>();

        Tournament t = tournaments.lastElement();
        Hashtable<String, Float> acumulated = t.getAccumulatedEnergy();

        for (OpponentInfo oi : opponentsInfo.getOpponents()) {
            boolean hayRanking = ranking.containsKey(oi.getName());
            boolean hayAcumulated = acumulated.containsKey(oi.getName());

            info.add(new OpponentReportLine(oi.getName(),
                    oi.getAuthor(),
                    oi.getAffiliation(),
                    hayRanking ? ranking.get(oi.getName()) : 0,
                    hayAcumulated ? acumulated.get(oi.getName()) : 0.0f));
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
            ZipHelper.createZip(config);
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

