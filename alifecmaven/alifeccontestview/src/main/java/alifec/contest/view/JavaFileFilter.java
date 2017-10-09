package alifec.contest.view;

import java.io.File;

public class JavaFileFilter extends javax.swing.filechooser.FileFilter {
    @Override
    public boolean accept(File f) {
        return f.isDirectory() || ((f.getName().length() - ".java".length()) == f.getName().indexOf(".java"));
    }


    public String getDescription() {
        return ".java";
    }
}
