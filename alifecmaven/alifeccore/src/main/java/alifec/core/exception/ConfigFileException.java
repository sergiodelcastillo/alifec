package alifec.core.exception;

import alifec.core.persistence.config.ContestConfig;

/**
 * Created by Sergio Del Castillo on 26/09/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class ConfigFileException extends Exception {
    private static final long serialVersionUID = 0L;

    public ConfigFileException(String text, Throwable ex) {
        super(text, ex);
    }

}

