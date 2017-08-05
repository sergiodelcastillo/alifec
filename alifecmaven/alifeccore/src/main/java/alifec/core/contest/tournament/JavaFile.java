/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */
package alifec.core.contest.tournament;

import java.io.File;

public class JavaFile extends javax.swing.filechooser.FileFilter {
    @Override
    public boolean accept(File f) {
        return f.isDirectory() || ((f.getName().length() - ".java".length()) == f.getName().indexOf(".java"));
    }


    public String getDescription() {
        return ".java";
    }
}
