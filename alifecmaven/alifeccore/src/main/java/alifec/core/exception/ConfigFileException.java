package alifec.core.exception;

import alifec.core.persistence.config.ContestConfig;

/**
 * Created by Sergio Del Castillo on 26/09/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class ConfigFileException extends Exception {
    public enum Status {
        DEFAULT_PATH_ERROR,
        CONFIG_FILE_DOES_NOT_EXISTS,
        CONFIG_FILE_READ_ISSUE,
        /*CONTEST_DOES_NOT_EXISTS,*/
        SAVE_ISSUE
    }


    private final Status state;

    private static final long serialVersionUID = 0L;


    private final ContestConfig config;

    public ConfigFileException(String s, Status state) {
        this(s, null, null, state);
    }

    public ConfigFileException(String s, ContestConfig config, Status state) {
        this(s, null, config, state);
    }

    public ConfigFileException(String s, Throwable cause, Status state) {
        this(s, cause, null, state);
    }

    public ConfigFileException(String s, Throwable cause, ContestConfig config, Status state) {
        super(s, cause);
        this.config = config;
        this.state = state;

    }

    public ContestConfig getConfig() {
        return config;
    }

    public Status getState() {
        return state;
    }
}

