package alifec.core.compilation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sergio Del Castillo on 05/08/17.
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class CompilationResult {
    private List<String> javaMessages;
    private List<String> cppMessages;
    private boolean cppErrors;
    private boolean javaErrors;

    public CompilationResult() {
        javaMessages = new ArrayList<>();
        cppMessages = new ArrayList<>();
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
