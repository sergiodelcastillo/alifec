package alifec.core.exception;

import alifec.core.persistence.config.CompileConfig;


/**
 * Created by Sergio Del Castillo on 17/10/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class CompileConfigException extends Exception {

    private static final long serialVersionUID = 0L;
    private final CompileConfig config;

    public CompileConfigException(String s, CompileConfig config) {
        this(s, null, config);
    }


    public CompileConfigException(String s, Throwable cause, CompileConfig config) {
        super(s, cause);
        this.config = config;

    }

    public CompileConfig getCompileConfig() {
        return config;
    }
}
