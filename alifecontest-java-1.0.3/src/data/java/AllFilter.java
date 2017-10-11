/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

package data.java;

import java.io.*;

public class AllFilter {

    public static String[] listCppFiles(String path) {
        File file = new File(path);
        String[] cppColonies = file.list(new CppFilter());

        if (cppColonies == null) {
            cppColonies = new String[0];
        }

        return cppColonies;
    }

    /**
     * List all files in the folder path that end with .h
     *
     * @return list of name of classes
     */
    public static String[] listCppColonies() {
        String[] cppColonies = listCppFiles(Config.getInstance().getAbsoluteMOsFolder());

        for (int i = 0; i < cppColonies.length; i++) {
            cppColonies[i] = CppFilter.getColonyName(cppColonies[i]);
        }

        return cppColonies;
    }


    /**
     * List all file that end with .java it the folder path without the extension .java
     */
    public static String[] listJavaColonies() {
        File file = new File(Config.getInstance().getAbsoluteMOsFolder());
        String[] javaColonies = file.list(new JavaFilter());

        if (javaColonies == null) {
            javaColonies = new String[0];
        }
        for (int i = 0; i < javaColonies.length; i++) {
            javaColonies[i] = javaColonies[i].replace(".java", "");
        }
        return javaColonies;
    }

    public static String[] listTournaments() {
        File file = new File(Config.getInstance().getAbsoluteContestName());
        String[] tournaments = file.list(new TournamentFilter());

        if (tournaments == null)
            tournaments = new String[0];

        return tournaments;
    }

    public static String[] listContests() {
        String[] contest = null;

        if(Config.getInstance() != null)
            contest = new File(Config.getInstance().getAbsoluteContestPath()).list(new ContestFilter());

        if (contest == null)
            contest = new String[0];

        return contest;
    }

    public static File[] listAllFiles(String path) {
        return new File(path).listFiles(new AbstractFilter() {
            @Override
            public boolean valid(File dir, String name) {
                return true;
            }
        });
    }
}
