/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */
package alifec.core.persistence.filter;

import alifec.core.contest.ContestConfig;

import java.io.File;
import java.io.FilenameFilter;

public class TournamentFilter implements FilenameFilter {

    public boolean accept(File dir, String name) {
        return name.indexOf(ContestConfig.TOURNAMENT_PREFIX) == 0 &&
                new File(dir.getAbsolutePath() + File.separator + name).isDirectory();
    }
}

