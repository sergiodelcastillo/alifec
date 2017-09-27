package alifec.core.exception;

import alifec.core.persistence.ContestConfig;

/**
 * Created by Sergio Del Castillo on 26/09/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class ConfigFileException extends Exception {

    private static final long serialVersionUID = 0L;

    public ContestConfig getConfig() {
        return config;
    }

    private final ContestConfig config;


    public ConfigFileException(String s, ContestConfig config) {
        this(s, null, config);
    }

    public ConfigFileException(String s, Throwable cause, ContestConfig config) {
        super(s, cause);
        this.config = config;

    }
}

