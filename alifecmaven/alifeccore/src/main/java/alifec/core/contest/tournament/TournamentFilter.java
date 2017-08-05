/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */
package alifec.core.contest.tournament;

import java.io.File;
import java.io.FilenameFilter;

public class TournamentFilter implements FilenameFilter {

    public boolean accept(File dir, String name) {
        return name.toLowerCase().indexOf("tournament-") == 0 &&
                !new File(dir.getAbsolutePath() + File.separator + name).isFile();
    }
}

