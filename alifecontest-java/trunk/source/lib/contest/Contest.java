/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

package lib.contest;

import exceptions.CompilerException;
import exceptions.CreateContestException;
import exceptions.CreateRankingException;
import exceptions.CreateTournamentException;
import lib.Agar;
import lib.AllFilter;
import lib.Environment;
import lib.nutrients.Nutrient;
import lib.oponentInfo.OponentInfo;
import lib.oponentInfo.OponentInfoManager;
import lib.tournament.Tournament;
import lib.tournament.TournamentManager;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Contest {

    /**
     * List of nutrients that are used in contest.
     * generic form: "nutrient_id"
     * name : is the nutrient name used in each tournament
     */
    public static final String NUTRIENTS_FILE = "nutrients";

    /**
     * Folder of source colonies.
     */
    public static String MOS_FOLDER = "MOs";

    /**
     * Folder of reports.
     */
    public static String REPORT_FOLDER = "Report";

    /**
     * Log Folder.
     */
    public static final String LOG_FOLDER = "Log";

    /**
     * Configuration file.
     */
    public static final String CONFIG_FILE = "config";

    /**
     * back up file
     */
    public static final String BACKUP_FOLDER = "Backup";

    public static final int PROGRAMMER_MODE = 0;
    public static final int COMPETITION_MODE = 1;


    /**
     * Environment ...
     */
    private Environment environment;
    /**
     * manager of tournaments
     */
    private TournamentManager tournaments;

    /**
     * info of all oponents
     */
    private OponentInfoManager oponentsInfo;

    /**
     * Absolute path of Contest.
     */
    private String PATH = "";
    /**
     * name of contest
     */
    private String NAME = "";

    /**
     * mode of contest:
     * programmer (mode = 0): the competitor should use this mode.
     * competition(mode = 1): reserved to compite.
     */
    private int mode = 0;

    /**
     * time (in Milliseconds) between battles.
     */
    private int pauseBetweenBattles = 5;

    public Contest(String path) throws IOException, CreateTournamentException, CreateContestException {
        init(path);
    }

    private void init(String path) throws IOException, CreateTournamentException, CreateContestException {
        if (!loadConfig(path))
            throw new CreateContestException("Bad config file. You could delete the following file:\n"
                    + path + File.separator + CONFIG_FILE);

        System.out.println("Name: " + NAME);

        // compile all MOs
        compile();
        oponentsInfo = new OponentInfoManager(path + File.separator + NAME);
        environment = new Environment(getMOsPath());
        tournaments = new TournamentManager(PATH + File.separator + NAME, mode);

        initTournament();

        oponentsInfo.read();
        Enumeration<Integer> ids = environment.getOps().elements();

        while (ids.hasMoreElements()) {
            Integer i = ids.nextElement();
            oponentsInfo.add(environment.getName(i), environment.getAuthor(i), environment.getAffiliation(i));
        }
    }

    void initTournament() {
        if (tournaments.lastElement() != null && tournaments.lastElement().hasBackUpFile() && mode == COMPETITION_MODE) {
            Vector<String> conflict = new Vector<String>();

            if (!get_conflict(conflict)) {
                System.out.println("Fail loading conflict. Cant continue");
                System.exit(0);
            }
            if (conflict.size() == 0) {
                for (String c : environment.getNames()) {
                    tournaments.lastElement().addColony(c);
                }
            } else {
                String c1 = conflict.elementAt(0);
                String c2 = conflict.elementAt(1);
                SolveConflictUI solver = new SolveConflictUI(this, c1, c2);
                solver.setVisible(true);
            }
        } else {
            try {
                tournaments.newTournament(environment.getNames());
            } catch (CreateTournamentException ex) {
                System.out.println("Cant create a new tournament");
            }
        }
    }

    public void delete_backup() {
        String p = tournaments.lastElement().getBattleManager().getPath();

        if (new File(p + File.separator + "battles_backup.csv").delete()) {
            System.out.println("Delete back up file [OK]");
        } else {
            System.out.println("Delete back up file [FAIL]");
        }
        try {
            tournaments.newTournament(environment.getNames());
            System.out.println("Creating new Tournament [OK]");
        } catch (CreateTournamentException ex) {
            System.out.println("Creating new Tournament [FAIL]");
        }
    }

    boolean get_conflict(Vector<String> c) {
        Vector<String[]> battles = new Vector<String[]>();
        Vector<String[]> backup = new Vector<String[]>();
        String t = PATH + File.separator + NAME + File.separator + tournaments.lastElement().NAME;

        load_file(t + File.separator + "battles.csv", battles);
        load_file(t + File.separator + "battles_backup.csv", backup);

        for (String[] l : battles) {
            if (!compare_line(l, backup.firstElement()))
                return false;
            backup.remove(0);
        }
        if (backup.size() > 0) {
            c.addElement(backup.elementAt(0)[0]);
            c.addElement(backup.elementAt(0)[1]);
        }
        return true;
    }

    private boolean compare_line(String[] a, String b[]) {
        if (a.length != b.length) return false;

        for (int i = 0; i < a.length; i++) {
            if (!a[i].toLowerCase().equalsIgnoreCase(b[i].toLowerCase()))
                return false;
        }
        return true;
    }

    private void load_file(String n, Vector<String[]> b) {
        BufferedReader f = null;
        String line;

        try {
            f = new BufferedReader(new FileReader(n));

            while ((line = f.readLine()) != null) {
                String[] tmp = line.split(",");
                if (tmp.length == 3) {
                    String tmp2[] = new String[3];

                    tmp2[0] = tmp[0];
                    tmp2[1] = tmp[1];
                    tmp2[2] = tmp[2];

                    b.addElement(tmp2);
                }
            }

        } catch (IOException ex) {
            System.out.println("Cant find battles.csv and battles_backup.csv");
            System.out.println("path: " + n);
        } finally {
            try {
                if (f != null) {
                    f.close();
                }
            } catch (IOException ignored) {
            }
        }
    }

    /**
     * Compile .java and .cpp files
     * java files: are compiled and saved in the lib/MOs folder
     * C/Cpp files: are build a unique library(cppcolonies.so/.dll) and saved in the lib/MOs folder.
     * @return true if successfully
     */
    private boolean compile() {
        System.out.println("\nCompile JAVA Files");
        try {
            for (File f : AllFilter.get_files_java(getMOsPath())) {

                if (compileMOsJava(f.getParent(), f.getName())) {
                    System.out.println(f.getAbsolutePath() + " [OK]");
                } else {
                    System.err.println(f.getAbsolutePath() + " [FAIL]");
                    Message.printErr(null, "Could not compile " + f.getName() + ". For more details use make");
                }
            }
        } catch (exceptions.CompilerException ex) {
            Message.printErr(null, ex.getMessage());
        }

        System.out.println("\nCompile C++ Files");
        System.out.print("Update C++ Files: ");

        if (updateTournamentCpp() && updateIncludes()) {
            System.out.println("[OK]");
            System.out.print("Create libcppcolonies ");

            if (compileAllMOsCpp(getMOsPath())) {
                System.out.println("[OK]");
            } else {
                System.out.println("[FAIL]");
                Message.printErr(null, "Could not compile one o more microorganisms of C++. For more details use make");
            }

        } else
            System.out.println("[FAIL]");

        return true;
    }

    /**
     * this method use the native compiler(Javac) to compile the java codes.
     * The compiled codes are saved in the lib/MOs folder
     * @param path absolute path of C/C++ microorganism
     * @param name of destination library
     * @return true if the compilation is successfully
     * @throws exceptions.CompilerException if can not find the C/C++ compiler
     */
    boolean compileMOsJava(String path, String name) throws CompilerException {
        try {
            JavaCompiler javac = ToolProvider.getSystemJavaCompiler();

            if (javac == null)
                throw new CompilerException();

            File out = LogPrintter.getErrorFile(path, name);
            DataOutputStream err = new DataOutputStream(
                    new BufferedOutputStream(
                            new FileOutputStream(out)));
            int s = javac.run(null, null, err, "-d", getMOsPath(), path + File.separator + name);

            if (s == 0)
                out.deleteOnExit();

            return s == 0;
        } catch (java.io.FileNotFoundException ex) {
            System.out.println("Fail to compile: " + path + File.separator + name + ". File not found.");
            return false;
        }

    }

    /**
     * Compila todos los MOs C++ y genera una libreria dinamica
     * llamada libcppcolonies.dll o libcppcolonies.so de acuerdo al
     * sistema operativo. La librería se almacena en lib/MOs
     * @param mospath URL of Microorganism sources
     * @return true if is successfully
     */
    private boolean compileAllMOsCpp(String mospath) {
        try {

            String os = System.getProperty("os.name").toLowerCase();
            String[] console = {""};

            if (os.contains("linux")) {
                String com = "g++ -o "+getMOsPath()+"/lib/MOs/libcppcolonies.so -fPIC -Wall -shared -lm -Icpp -I\"" +
                        mospath + "\"  cpp/lib_CppColony.cpp";

                console = new String[]{"/bin/bash", "-c", com};
            } else if (os.contains("windows")) {
                String com = "g++ -o "+getMOsPath()+"/lib/MOs/libcppcolonies.dll -Wl,--add-stdcall-alias -Wall -shared -lm -Icpp -I\"" +
                        mospath + "\"  cpp/lib_CppColony.cpp";

                console = new String[]{"cmd.exe", "/C", com};
            }

            Process p = Runtime.getRuntime().exec(console);
            p.waitFor();

            p.getErrorStream().close();
            p.getInputStream().close();
            p.getOutputStream().close();

            return p.exitValue() == 0;
        } catch (IOException ex) {
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * Crea el archivo Tournament.cpp con los nombres que se
     * pasan como argumento.
     * @return true if is successfully
     */
    private boolean updateTournamentCpp() {
        Vector<String> names = AllFilter.list_names_cpp(getMOsPath());
        File env = new File(getMOsPath()+"/lib/MOs/"+ File.separator + "Environment.cpp");
        try {
            if(!env.exists())
                env.createNewFile();

            PrintWriter pw = new PrintWriter(env);

            pw.println("");
            pw.println("bool Environment::addColony(string name, int id){");
            pw.println("	CppColony<Microorganism> *mo = NULL;");
            pw.println("");

            for (String name : names) {
                pw.println("	if(name == \"" + name + "\"){");
                pw.println("	   mo = (CppColony < Microorganism > *) new CppColony< " + name + " >(id);");
                pw.println("   }");
            }

            pw.println("");
            pw.println("	colonies.push_back(mo);");
            pw.println("");
            pw.println("   return mo != NULL;");
            pw.println("}");
            pw.println("");
            pw.close();
        } catch (java.io.FileNotFoundException ex) {
            return false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Crea el archivo includemmos.h con los nombres que se
     * pasan como argumento:
     * @return true if  is successfully
     */
    private boolean updateIncludes() {
        Vector<String> files = AllFilter.list_file_cpp(getMOsPath());

        File includes = new File(getMOsPath()+"/lib/MOs/"+ File.separator + "includemos.h");
        try {

            if(!includes.exists())
                includes.createNewFile();

            PrintWriter pw = new PrintWriter(includes);

            for (String n : files) {
                pw.println("#include \"" + n + "\"");
            }
            pw.close();
        } catch (java.io.FileNotFoundException ex) {
            return false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public static boolean existContest(String path, String name) {
        if (path == null || path.equals("")) {
            System.out.println("contest.existContest: null path");
            return false;
        }
        if (name == null || name.equals("")) {
            System.out.println("contest.existContest: null name");
            return false;
        }

        String contestName = path + File.separator + name;
        String MOsFolder = contestName + File.separator + MOS_FOLDER;
        String ReportFolder = contestName + File.separator + REPORT_FOLDER;
        String NutrientFile = contestName + File.separator + NUTRIENTS_FILE;

        return new File(contestName).exists() &&
                new File(MOsFolder).exists() &&
                new File(ReportFolder).exists() &&
                new File(NutrientFile).exists();
    }

    public static boolean existConfig(String path) {
        if (path == null || path.equals("")) {
            System.out.println("contest.existConfig: null path");
            return false;
        }

        String[] list = new File(path).list();

        for (String names : list) {
            if (names.equals(CONFIG_FILE)) {
                return true;
            }
        }
        return false;
    }

    public Hashtable<String, Integer> getNutrients() {

        Hashtable<String, Integer> nutri = new Hashtable<String, Integer>();
        String url = PATH + File.separator + NAME + File.separator + NUTRIENTS_FILE;

        try {
            FileReader fr = new FileReader(url);
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
            System.out.println("File not Found: " + url);
        }

        return nutri;
    }

    /* private boolean loadConfig(String path) throws IOException {
        // read the config File!
        FileReader fr = new FileReader(path + File.separator + CONFIG_FILE);
        BufferedReader br = new BufferedReader(fr);
        String line;

        while ((line = br.readLine()) != null) {
            if (!line.equalsIgnoreCase("")) {
                String[] lineSplit = line.split("=");
                if (lineSplit.length == 2) {
                    if (lineSplit[1].equalsIgnoreCase("."))
                        set(lineSplit[0], path);
                    else
                        set(lineSplit[0], lineSplit[1]);
                }
            }
        }
        return validate_config();
    }*/

    /**
     * Read the config File in the project.
     *
     * @param path path to read the config
     * @return true if is successfully
     * @throws IOException if can not find the config file
     */
    private boolean loadConfig(String path) throws IOException {
        Properties property = new Properties();
        InputStream is = new FileInputStream(path + File.separator + CONFIG_FILE);

        property.load(is);

        for (Object object : property.keySet()) {
            this.set(object.toString(), property.getProperty(object.toString()));
        }
        return validate_config();
    }

    boolean reloadConfig() throws IOException {
        return loadConfig(PATH);
    }

    private boolean validate_config() {
        if (pauseBetweenBattles < 0) {
            return false;
        }

        if (mode < 0 || mode > 1) {
            return false;
        }

        if (NAME.equalsIgnoreCase("")) {
            return false;
        }

        if (PATH.equalsIgnoreCase("")) {
            return false;
        }

        File f = new File(PATH);
        if (!f.exists())
            return false;

        f = new File(PATH + File.separator + NAME);

        return f.exists();
    }

    public static boolean createConfig(String path, String contestName) {
        Properties property = new Properties();
        property.setProperty("url", ".");
        property.setProperty("name", "" + contestName);
        property.setProperty("mode", "" + "0");
        property.setProperty("pause between battles", "100");

        try {
            property.store(new FileWriter(path + File.separator + CONFIG_FILE),
                    "Configuration File\n Warning: do not modify this file");
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public static Vector<String> listContest(String path) {
        String list[] = new File(path).list(new ContestFilter());
        Vector<String> results = new Vector<String>();
        for (String name : list) {
            if (existContest(path, name))
                results.addElement(name);

        }
        return results;
    }

    public static boolean createContest(String path, String name, boolean examples) {
        PrintWriter pw;
        try {
            String contestName = path + File.separator + name;
            String MOsFolder = contestName + File.separator + MOS_FOLDER;
            String ReportFolder = contestName + File.separator + REPORT_FOLDER;
            new File(contestName).mkdir();
            new File(MOsFolder).mkdir();
            new File(ReportFolder).mkdir();
            File contestFile = new File(contestName + File.separator + NUTRIENTS_FILE);
            pw = new PrintWriter(contestFile);

            for (Nutrient n : Agar.nutrient)
                pw.println(n.getID());

            pw.close();

            // create examples ...!!
            if (examples)
                CodeGenerator.generateExamples(MOsFolder);

        } catch (IOException ex) {
            Logger.getLogger(Contest.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    public static String createContest(String path, boolean examples) throws FileNotFoundException {
        Calendar c = new GregorianCalendar();
        String name = "Contest" + Integer.toString(c.get(Calendar.YEAR));

        createContest(path, name, examples);
        return name;
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
    public boolean updateConfig(String path, String name, int mode, int pause) {
        Properties property = new Properties();
        property.setProperty("url", path);
        property.setProperty("name", name);
        property.setProperty("mode", "" + mode);
        property.setProperty("pause between battles", "" + pause);

        try {
            property.store(new FileWriter(this.PATH + File.separator + CONFIG_FILE),
                    "Configuration File\n Warning: do not modify this file");
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    public void updateNutrient(int[] nutrients) throws IOException {
        String url = PATH + File.separator + NAME + File.separator + NUTRIENTS_FILE;

        new File(url).renameTo(new File(url + "_backup"));

        File newNutrient = new File(url);
        PrintWriter pw = new PrintWriter(newNutrient);

        for (int nutriID : nutrients)
            pw.println(nutriID);

        pw.close();

        new File(url + "_backup").delete();
    }

    public void setMode(int mode) {
        this.mode = mode;
        this.tournaments.setMode(mode);

    }

    boolean set(String type, String option) {
        type = type.trim().toLowerCase();
        option = option.trim();

        if (type.equals("") || option.equals(""))
            return false;

        if (type.equals("url")) {
            try {
                PATH = new File(option).getCanonicalPath();
            } catch (IOException ex) {
                return false;
            }
        } else if (type.equals("mos")) {
//			MOS_FOLDER = option;
            return false;
        } else if (type.equals("name")) {
            NAME = option;
        } else if (type.equals("mode")) {
            try {
                mode = Integer.parseInt(option);
            } catch (java.lang.NumberFormatException ex) {
                return false;
            }
        } else if (type.equals("pause between battles")) {
            try {
                pauseBetweenBattles = Integer.parseInt(option);
            } catch (java.lang.NumberFormatException ex) {
                return false;
            }
        }
        return true;
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

    String getMOsPath() {
        return this.PATH + File.separator + NAME + File.separator + MOS_FOLDER;
    }

    public String getReportPath() {
        return this.PATH + File.separator + NAME + File.separator + REPORT_FOLDER;
    }

    public int getMode() {
        return mode;
    }

    public String getPath() {
        return PATH;
    }

    public String getName() {
        return NAME;
    }

    public int getTimeWait() {
        return pauseBetweenBattles;
    }

    /**
     * name,author,affilation, acumulatedPoints, lastEnergy
     *
     * @return information
     * @throws exceptions.CreateRankingException if can not create the ranking
     */
    public Vector<Vector<Object>> getInfo() throws CreateRankingException {
        Hashtable<String, Integer> ranking = tournaments.getRanking();
        Vector<Vector<Object>> info = new Vector<Vector<Object>>();

        Tournament t = tournaments.lastElement();
        Hashtable<String, Float> acumulated = t.getAcumulatedEnergy();

        for (OponentInfo oi : oponentsInfo.getOponents()) {
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
     * @return true if is successfully
     */
    public boolean createBackUp() {
        File f = new File(NAME + File.separator + BACKUP_FOLDER);
        ArrayList<String[]> files = new ArrayList<String[]>();
        Stack<File> stack = new Stack<File>();
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
        } catch (java.io.IOException ex) {
            return false;
        }
    }

    public boolean needRestore() {
        return tournaments.size() != 0 && tournaments.lastElement().hasBackUpFile();
    }
}

