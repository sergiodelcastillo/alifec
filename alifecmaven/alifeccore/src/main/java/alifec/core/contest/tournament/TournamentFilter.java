/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */
package alifec.core.contest.tournament;

import java.io.File;
import java.io.FilenameFilter;

public class TournamentFilter implements FilenameFilter {
    public static final String TOURNAMENT_PREFIX = "tournament-";

    public boolean accept(File dir, String name) {
        return name.toLowerCase().indexOf(TOURNAMENT_PREFIX) == 0 &&
                !new File(dir.getAbsolutePath() + File.separator + name).isFile();
    }
}

