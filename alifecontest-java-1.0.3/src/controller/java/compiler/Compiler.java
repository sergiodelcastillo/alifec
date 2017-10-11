package controller.java.compiler;

import data.java.AllFilter;
import data.java.Config;
import data.java.Log;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Created by Sergio Del Castillo
 * User: yeyo
 * Date: Jun 12, 2010
 * Time: 12:27:25 AM
 * Email: yeyo@druidalabs.com
 */
public class Compiler {
    public static enum Type {
        RELEASE("release"),
        DEBUG("debug");

        private final String text;

        Type(String text) {
            this.text = text;
        }

        private String getText() {
            return this.text;
        }
    }

    private static String WINDOWS = "Windows";
    private static String LINUX = "Linux";


    private static Compiler instance;

    private static void usage() {
        System.out.println("\nUso: java -jar compiler.jar MO" +
                "\n\tEjemplo: " +
                "\n\t\t java -jar compiler.jar MO1.h" +
                "\n\t\t java -jar compiler.jar -java" +
                "\n\t\t java -jar compiler.jar MO2.java");
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            usage();
            return;
        }

        String mo = Config.getInstance().getAbsoluteMOsFolder() + File.separator + args[0];

        File f = new File(mo);

        if (!"-java".equalsIgnoreCase(args[0].trim()) && !f.exists()) {
            System.out.println("El archivo: " + args[0] + " no existe.");
            usage();
            return;
        }

        Compiler.getInstance().cleanBinaries(false);

        if ("-java".equalsIgnoreCase(args[0].trim())) {
            System.out.println("Compiling all files *.java " + (Compiler.getInstance().compileJava(true) ? " [OK]" : ""));
        } else if (mo.endsWith(".java")) {
            System.out.println("Compiling " + f.getAbsolutePath() + (Compiler.getInstance().compileJava(true, f.getAbsolutePath()) ? " [OK]" : ""));
        } else if (mo.endsWith(".h")) {
            System.out.println("Compiling " + f.getAbsolutePath() + (Compiler.getInstance().makeCppLibrary(true, Compiler.Type.DEBUG) ? " [OK]" : ""));
        } else {
            System.out.println("El archivo: " + args[0] + " debe terminar con .java o .h.");
            usage();
        }

        Compiler.getInstance().cleanBinaries(false);
    }

    private Compiler() {
    }

    public String[] getParameters(String path) {
        ArrayList<String> list = new ArrayList<String>();

        File f = new File(path);

        if (f.exists()) {
            listJavaFileRec(f, list);
        }
        int ds = 2;
        String[] res = new String[list.size() + ds];
        res[0] = "-d";
        res[1] = Config.getInstance().getAbsoluteBinMOsFolder();

        for (int i = 0; i < list.size(); i++) {
            res[i + ds] = list.get(i);
        }

        return res;
    }

    public void listJavaFileRec(File file, ArrayList<String> list) {
        if (file.isFile()) {
            if (file.getName().endsWith(".java")) {
                list.add(file.getAbsolutePath());
            }
        } else {
            for (File f : file.listFiles()) {
                listJavaFileRec(f, list);
            }
        }
    }

    public static Compiler getInstance() {
        if (instance == null) {
            instance = new Compiler();
        }
        return instance;
    }

    public boolean compileJava() {
        return compileJava(false);
    }

    public boolean compileJava(boolean standardOutput) {
        return compileJava(getParameters(Config.getInstance().getAbsoluteMOsFolder()), standardOutput);
    }

    public boolean compileJava(boolean standardOutput, String javaClass) {
        String[] parameter = new String[3];

        parameter[0] = "-d";
        parameter[1] = Config.getInstance().getAbsoluteBinMOsFolder();
        parameter[2] = javaClass;

        return compileJava(parameter, standardOutput);
    }

    private boolean compileJava(String[] argument, boolean standardOutput) {
        try {
            JavaCompiler javac = ToolProvider.getSystemJavaCompiler();

            if (javac == null) {
                Log.printlnAndSave("Cant find the javac compiler.");
                return false;
            }

            OutputStream err;
            File out = null;

            if (standardOutput) {
                err = System.err;
            } else {
                out = new File(Log.getAbsoluteLogFile());

                //TODO ver esto..!!
                if (!out.exists()) {
                    out.getParentFile().mkdirs();
                    out.createNewFile();
                }
                err = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(out)));
            }
            int s = javac.run(null, null, err, argument);

            if (s == 0 && out != null) {
                out.deleteOnExit();
            }

            err.close();

            return s == 0;
        } catch (java.io.IOException ex) {
            Log.printlnAndSave("error", ex);
            return false;
        }
    }

    public boolean makeCppLibrary(Type type) {
        return makeCppLibrary(false, type);
    }

    /**
     * .
     * Create the cpp library.
     *
     * @return true if was successful
     */
    public boolean makeCppLibrary(boolean standardOutput, Type type) {
        return createIncludes() &&
                createCppColonyCpp() &&
                compileLibCpp(Config.getInstance().getAbsoluteContestName(), Config.getInstance().getAbsoluteBinLibCppFolder(), standardOutput, type);
    }

    /**
     * Compile the cpp library
     *
     * @param contestName the name of the contest
     * @param bin         the destination folder
     * @return true if was successful
     */
    private boolean compileLibCpp(String contestName, String bin, boolean standardOutput, Type type) {
        try {
            String command = getCommand(contestName, bin, type);

            if (command == null) return false;
            System.out.println(command);

            Process p = Runtime.getRuntime().exec(command);
            //wait for the command
            p.waitFor();

            String line;
            StringBuilder buffer = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            while ((line = reader.readLine()) != null) {
                buffer.append(line).append('\n');
            }
            reader.close();
            p.getErrorStream().close();
            p.getInputStream().close();
            p.getOutputStream().close();


            if (p.exitValue() != 0) {
                if (standardOutput) {
                    System.err.println(buffer.toString());
                } else {
                    Log.save(buffer.toString());
                }
            }

            return p.exitValue() == 0;
        } catch (IOException ex) {
            Log.save("Error while creating Cpp library", ex);
            return false;
        } catch (InterruptedException ex) {
            Log.save("Error while creating Cpp library", ex);
            return false;
        } catch (IllegalArgumentException ex) {
            Log.save("Illegal argument", ex);
            return false;
        }
    }

    private String getCommand(String contestName, String bin, Type type) throws IOException {
        Properties properties = new Properties();
        properties.load(new FileReader("src/controller/java/compiler/config.properties"));

        String osName = System.getProperty("os.name");
        String osString = null;

        if (osName.equals(LINUX)) {
            osString = "linux";
        } else if (osName.contains(WINDOWS)) {
            osString = "windows";
        }

        if (osString == null) {
            Log.printlnAndSave("The OS=" + osName + " is unknown.");
            return null;
        }

        String line = properties.getProperty("compile." + osString);

        line = line.replace("{compiler." + osString + "}", properties.getProperty("compiler." + osString));
        line = line.replace("{bin." + osString + "}", properties.getProperty("bin." + osString));
        line = line.replace("{options." + osString + "}", properties.getProperty("options." + osString));
        line = line.replace("{include." + osString + "}", properties.getProperty("include." + osString));
        line = line.replace("{src." + osString + "}", properties.getProperty("src." + osString));

        line = line.replace("{option}", properties.getProperty("options." + type.getText()));

        if (properties.getProperty("java.home." + osString) == null || properties.getProperty("java.home." + osString).trim().equals("")) {
            String systemJavaHome = System.getProperty("java.home");
            File file = new File(systemJavaHome);

            if (!file.exists() || systemJavaHome == null || systemJavaHome.trim().equals("")) {
                throw new IllegalArgumentException("Invalid configuration of java.home");
            }

            line = line.replace("{java.home." + osString + "}", file.getParentFile().getAbsolutePath());
        } else {
            line = line.replace("{java.home." + osString + "}", properties.getProperty("java.home." + osString));
        }

        return line.replace("{contest.name}", contestName).replace("{bin}", bin);
    }

    /**
     * Create the file src/controller/cpp/Environment.cpp with the source code to include the cpp MOs.
     *
     * @return true if was successful.
     */
    private boolean createCppColonyCpp() {
        String[] names = AllFilter.listCppColonies();
        File env = new File("src" + File.separator + "controller" + File.separator + "cpp" + File.separator + "CppColony.cpp");
        try {
            PrintWriter pw = new PrintWriter(env);


            pw.println("template <class MO > CppColony<Microorganism>* CppColony<MO>::createInstance(string type, int id){");
            pw.println("    CppColony<Microorganism> *mo = NULL;");
            pw.println("");


            for (String name : names) {
                pw.println("   if(type == \"" + name + "\"){");
                pw.println("       mo = (CppColony < Microorganism > *) new CppColony< " + name + ">(id);");
                pw.println("   }");
            }

            pw.println("");
            pw.println("    return mo;");
            pw.println("}");
            pw.println("");

            pw.close();
        } catch (java.io.FileNotFoundException ex) {
            Log.save("Error updating CppColony.cpp", ex);
            return false;
        }
        return true;
    }

    /**
     * Create the file src/controller/cpp/includes.cpp with the source code to include  the cpp MOs.
     *
     * @return true if  is successfully
     */
    public boolean createIncludes() {
        return createIncludes(Config.getInstance().getAbsoluteMOsFolder());
    }

    /**
     * Create the file src/controller/cpp/includes.cpp with the source code to include  the cpp MOs.
     *
     * @return true if  is successfully
     */
    private boolean createIncludes(String path) {
        String[] files = AllFilter.listCppFiles(path);
        File includes = new File("src" + File.separator + "controller" + File.separator + "cpp" + File.separator + "includemos.h");

        try {
            PrintWriter pw = new PrintWriter(includes);

            for (String n : files) {
                pw.println("#include \"" + n + "\"");
            }
            pw.close();
        } catch (java.io.FileNotFoundException ex) {
            Log.save("Error updating includes.cpp", ex);
            return false;
        }
        return true;
    }

    public void cleanBinaries() {
        cleanBinaries(Config.getInstance().getAbsoluteBinLibCppFolder(), Config.MOS_FOLDER, true);
    }

    public void cleanBinaries(boolean print) {
        cleanBinaries(Config.getInstance().getAbsoluteBinLibCppFolder(), Config.MOS_FOLDER, print);
    }

    private void cleanBinaries(String path, String ignore, boolean print) {
        File f = new File(path);
        if (!f.exists())
            return;

        if (f.isFile()) {
            if (print) {
                Log.println("deleting = " + path + " ? " + (f.delete() ? "OK" : "fail"));
            } else {
                f.delete();
            }

        } else {

            for (File list : f.listFiles()) {
                cleanBinaries(list.getAbsolutePath(), ignore, print);
            }
            if (f.isDirectory() && f.getName().equals(ignore)) {
                return;
            }
            if (print) {
                Log.println("deleting = " + path + " ? " + (f.delete() ? "OK" : "fail"));
            } else {
                f.delete();
            }

        }
    }
}
