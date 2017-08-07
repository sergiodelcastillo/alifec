/**
 * @author Sergio
 * mail@: sergio.jose.delcastillo@gmail.com
 */

package alifec.core.contest;

import alifec.core.contest.oponentInfo.OpponentInfo;
import alifec.core.contest.oponentInfo.OpponentInfoManager;
import alifec.core.contest.tournament.Tournament;
import alifec.core.contest.tournament.TournamentManager;
import alifec.core.exception.CompilerException;
import alifec.core.exception.CreateContestException;
import alifec.core.exception.CreateRankingException;
import alifec.core.exception.CreateTournamentException;
import alifec.core.simulation.Agar;
import alifec.core.simulation.AllFilter;
import alifec.core.simulation.Environment;
import alifec.core.simulation.nutrients.Nutrient;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Contest {

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
        tournaments = new TournamentManager(config.getContestPath(), config.getMode());

        //create new and empty tournament
        tournaments.newTournament(environment.getNames());

        opponentsInfo.read();
        Enumeration<Integer> ids = environment.getOps().elements();

        while (ids.hasMoreElements()) {
            Integer i = ids.nextElement();
            opponentsInfo.add(environment.getName(i), environment.getAuthor(i), environment.getAffiliation(i));
        }
    }

    /**
     * Compile .java and .cpp files
     * java files: are compiled and saved in the lib/MOs folder
     * C/Cpp files: are build a unique library(cppcolonies.so/.dll) and saved in the lib/MOs folder.
     *
     * @return true if successfully
     */
    public CompilationResult compileMOs(String MOsPath) {
        System.out.println("\nCompile JAVA Files");
        CompilationResult result = new CompilationResult();
        try {
            for (File f : AllFilter.getFilesJava(MOsPath)) {

                if (compileMOsJava(f.getParent(), f.getName())) {
                    System.out.println(f.getAbsolutePath() + " [OK]");
                } else {
                    System.err.println(f.getAbsolutePath() + " [FAIL]");
                    result.logJavaError("Could not compileMOs " + f.getName() + ". For more details use make");
                    //Message.printErr(null, "Could not compileMOs " + f.getName() + ". For more details use make");
                }
            }
        } catch (CompilerException ex) {
            result.logJavaError(ex.getMessage());
            //Message.printErr(null, ex.getMessage());
        }

        System.out.println("\nCompile C++ Files");
        System.out.print("Update C++ Files: ");

        if (updateTournamentCpp(MOsPath) && updateIncludes(MOsPath)) {
            System.out.println("[OK]");
            System.out.print("Create libcppcolonies ");

            if (compileAllMOsCpp(getMOsPath())) {
                System.out.println("[OK]");
            } else {
                System.out.println("[FAIL]");
                result.logCppError("Could not compileMOs one o more microorganisms of C++. For more details use make");
                //Message.printErr(null, "Could not compileMOs one o more microorganisms of C++. For more details use make");
            }

        } else
            System.out.println("[FAIL]");

        return result;
    }

    /**
     * this method use the native compiler(Javac) to compileMOs the java codes.
     * The compiled codes are saved in the lib/MOs folder
     *
     * @param path absolute path of C/C++ microorganism
     * @param name of destination library
     * @return true if the compilation is successfully
     * @throws CompilerException if can not find the C/C++ compiler
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
        } catch (FileNotFoundException ex) {
            System.out.println("Fail to compileMOs: " + path + File.separator + name + ". File not found.");
            return false;
        }

    }

    /**
     * Compila todos los MOs C++ y genera una libreria dinamica
     * llamada libcppcolonies.dll o libcppcolonies.so de acuerdo al
     * sistema operativo. La librería se almacena en lib/MOs
     *
     * @param mospath URL of Microorganism sources
     * @return true if is successfully
     */
    private boolean compileAllMOsCpp(String mospath) {
        //@Todo Use a file with the pattern instead of hiring it in the source code
        try {

            String os = System.getProperty("os.name").toLowerCase();
            String[] console = {""};

            if (os.contains("linux")) {
                String com = "g++ -o " + getMOsPath() + "/lib/MOs/libcppcolonies.so -fPIC -Wall -shared -lm -Icpp -I\"" +
                        mospath + "\"  cpp/lib_CppColony.cpp";

                console = new String[]{"/bin/bash", "-c", com};
            } else if (os.contains("windows")) {
                String com = "g++ -o " + getMOsPath() + "/lib/MOs/libcppcolonies.dll -Wl,--add-stdcall-alias -Wall -shared -lm -Icpp -I\"" +
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
     *
     * @return true if is successfully
     */
    private boolean updateTournamentCpp(String MOsPath) {
        Vector<String> names = AllFilter.listNamesCpp(MOsPath);
        File env = new File(MOsPath + "/lib/MOs/" + File.separator + "Environment.cpp");
        try {
            if (!env.exists())
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
        } catch (FileNotFoundException ex) {
            return false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Crea el archivo includemmos.h con los nombres que se
     * pasan como argumento:
     *
     * @return true if  is successfully
     */
    private boolean updateIncludes(String MOsPath) {
        Vector<String> files = AllFilter.list_file_cpp(MOsPath);

        File includes = new File(MOsPath + "/lib/MOs/" + File.separator + "includemos.h");
        try {

            if (!includes.exists())
                includes.createNewFile();

            PrintWriter pw = new PrintWriter(includes);

            for (String n : files) {
                pw.println("#include \"" + n + "\"");
            }
            pw.close();
        } catch (FileNotFoundException ex) {
            return false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }




    public Hashtable<String, Integer> getNutrients() {

        Hashtable<String, Integer> nutri = new Hashtable<>();
        String url = config.getContestPath() + File.separator + ContestConfig.NUTRIENTS_FILE;

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


    /**
     * Reload the configuration. If the config is not valid then it is discarded.
     *
     * @return
     * @throws IOException
     */

    public boolean reloadConfig() throws IOException {
        ContestConfig configTmp = ContestConfig.build(config.getPath());

        if (configTmp.isValid()) {
            config = configTmp;
            return true;
        }

        return false;
    }


    public static boolean createConfig(String path, String contestName) {
        Properties property = new Properties();
        property.setProperty("url", ".");
        property.setProperty("name", "" + contestName);
        property.setProperty("mode", "" + "0");
        property.setProperty("pause between battles", "100");

        try {
            property.store(new FileWriter(path + File.separator + ContestConfig.CONFIG_FILE),
                    "Configuration File\n Warning: do not modify this file");
        } catch (IOException e) {
            return false;
        }
        return true;
    }


    public static boolean createContestFolder(String path, String name, boolean examples) {
        PrintWriter pw;
        try {
            String contestName = path + File.separator + name;
            String MOsFolder = contestName + File.separator + ContestConfig.MOS_FOLDER;
            String ReportFolder = contestName + File.separator + ContestConfig.REPORT_FOLDER;
            new File(contestName).mkdir();
            new File(MOsFolder).mkdir();
            new File(ReportFolder).mkdir();
            File contestFile = new File(contestName + File.separator + ContestConfig.NUTRIENTS_FILE);
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

 /*   public static String createContestFolder(String path, boolean examples) throws FileNotFoundException {
        Calendar c = new GregorianCalendar();
        String name = "Contest" + Integer.toString(c.get(Calendar.YEAR));

        createContestFolder(path, name, examples);
        return name;
    }*/

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
            property.store(new FileWriter(config.getConfigFilePath()),
                    "Configuration File\n Warning: do not modify this file");
        } catch (IOException e) {
            return false;
        }

        return true;
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

