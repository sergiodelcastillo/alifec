/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */
package data.java;

import java.io.File;
import java.io.FilenameFilter;

public class TournamentFilter extends AbstractFilter {

    public boolean accept(File dir, String name) {
        File tournamentFile = new File(dir.getAbsolutePath() + File.separator + name);

        return name.startsWith("Tournament-") &&
                tournamentFile.isDirectory() &&
                tournamentFile.canRead() &&
                tournamentFile.canWrite();
    }

    @Override
    public boolean valid(File dir, String name) {
        File tournamentFile = new File(dir.getAbsolutePath() + File.separator + name);

        return name.startsWith("Tournament-") && tournamentFile.isDirectory();
    }
}

