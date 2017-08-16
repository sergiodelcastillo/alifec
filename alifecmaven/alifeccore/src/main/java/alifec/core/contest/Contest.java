/**
 * @author Sergio
 * mail@: sergio.jose.delcastillo@gmail.com
 */

package alifec.core.contest;

import alifec.core.contest.oponentInfo.OpponentInfo;
import alifec.core.contest.oponentInfo.OpponentInfoManager;
import alifec.core.contest.tournament.Tournament;
import alifec.core.contest.tournament.TournamentManager;
import alifec.core.exception.*;
import alifec.core.simulation.Agar;
import alifec.core.simulation.AllFilter;
import alifec.core.simulation.Environment;
import alifec.core.simulation.nutrients.Nutrient;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;


public class Contest {

    static Logger logger = org.apache.log4j.Logger.getLogger(Contest.class);

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
        opponentsInfo = new OpponentInfoManager(config.getContestPath());
        environment = new Environment(config.getMOsPath());
        tournaments = new TournamentManager(config);

        //create new and empty tournament
        tournaments.newTournament(environment.getNames());

        opponentsInfo.read();
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
            logger.warn("File not Found: " + nutrientsPath, e);
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

    public static boolean createContestFolder(ContestConfig config, boolean examples) {

        try {
            ContestHelper.buildNewContestFolder(config);

            // create examples ...!!
            if (examples)
                createExamples(config.getMOsPath());

        } catch (CreateContestFolderException e) {
            logger.error("Cant not create the ContestFolder");
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private static void createExamples(String MOsFolder) {
        try {
            //TODO: poner una constante...
            File source = new File(Contest.class.getClass().getResource("/examples/").toURI());

            Files.walk(source.toPath()).forEach(path -> {
                try {
                    File target = new File(MOsFolder + File.separator + path.getFileName());

                    if (new File(path.toUri()).isFile())
                        Files.copy(path, target.toPath());

                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            });
        } catch (URISyntaxException | IOException ex) {
            logger.error(ex.getMessage(), ex);
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
            return config.save();
        } catch (SaveContestConfigException e) {
            logger.error("Error updating config file: " + e.getConfig(), e);
        }
        return false;
    }

    public void updateNutrient(int[] nutrients) throws IOException {
        String url = config.getNutrientsFilePath();

        new File(url).renameTo(new File(url + "_backup"));

        File newNutrient = new File(url);
        PrintWriter pw = new PrintWriter(newNutrient);

        for (int nutriID : nutrients)
            pw.println(nutriID);

        pw.close();

        new File(url + "_backup").delete();
    }

    public void setMode(int mode) {
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

    public String getReportPath() {

        return config.getReportPath();
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
     * name,author,affilation, acumulatedPoints, lastEnergy
     *
     * @return information
     * @throws CreateRankingException if can not create the ranking
     */
    public Vector<Vector<Object>> getInfo() throws CreateRankingException {
        Hashtable<String, Integer> ranking = tournaments.getRanking();
        Vector<Vector<Object>> info = new Vector<>();

        Tournament t = tournaments.lastElement();
        Hashtable<String, Float> acumulated = t.getAccumulatedEnergy();

        for (OpponentInfo oi : opponentsInfo.getOpponents()) {
            boolean hayRanking = ranking.containsKey(oi.getName());
            boolean hayAcumulated = acumulated.containsKey(oi.getName());

            Vector<Object> tmp = oi.toVector();
            tmp.addElement(hayRanking ? ranking.get(oi.getName()) : Integer.valueOf(0));
            tmp.addElement(hayAcumulated ? acumulated.get(oi.getName()) : new Float(0));

            info.addElement(tmp);
        }

        // ordenar por el indice 3 (puntos acumulados)!!
        for (int i = 0; i < info.size() - 1; i++) {
            for (int j = i + 1; j < info.size(); j++) {
                Integer a = ((Integer) info.elementAt(i).elementAt(3));
                Integer b = (Integer) info.elementAt(j).elementAt(3);
                if (a < b) {
                    Vector<Object> tmp = info.elementAt(j);
                    info.set(j, info.elementAt(i));
                    info.set(i, tmp);
                }
            }
        }

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
        File f = new File(config.getBackupPath());
        ArrayList<String[]> files = new ArrayList<>();
        Stack<File> stack = new Stack<>();
        String zipname;

        if (!f.exists())
            if (!f.mkdirs())
                return false;

        zipname = f.getAbsolutePath() + File.separator + "backup-";
        zipname += new SimpleDateFormat("yyyyMMdd-hhmmss").format(new Date());
        zipname += ".zip";

        stack.add(new File(getMOsPath()));

        while (!stack.isEmpty()) {
            File file = stack.pop();
            for (File tmp : file.listFiles(new AllFilter(""))) {
                if (tmp.isFile()) {
                    String path = tmp.getAbsolutePath();
                    String name = path.replace(getMOsPath(), "");
                    files.add(new String[]{path, name});
                } else {
                    stack.push(tmp);
                }
            }
        }


        //generate the back up...
        try {
            Compressor.addToZip(zipname, files);
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    public boolean needRestore() {
        return tournaments.size() != 0 && tournaments.lastElement().hasBackUpFile();
    }

    public String getPath() {
        return config.getPath();
    }
}

