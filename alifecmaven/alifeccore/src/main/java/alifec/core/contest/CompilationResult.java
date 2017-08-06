package alifec.core.contest;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by Sergio Del Castillo on 05/08/17.
 */
public class CompilationResult {
    private List<String> javaMessages;
    private List<String> cppMessages;
    private boolean cppErrors;
    private boolean javaErrors;

    public CompilationResult() {
        javaMessages = new ArrayList<String>();
        cppMessages = new ArrayList<String>();
        cppErrors = false;
        javaErrors = false;
    }

    public void logJavaError(String message) {
        javaMessages.add(message);
        javaErrors = true;
    }

    public void logCppError(String message) {
        cppMessages.add(message);
        cppErrors = true;
    }

    public boolean haveErrors() {
        return cppErrors || javaErrors;
    }

    public List<String> getJavaMessages() {
        return javaMessages;
    }

    public List<String> getCppMessages() {
        return cppMessages;
    }
}
