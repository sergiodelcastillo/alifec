package alifec.core.exception;

import alifec.core.persistence.config.ContestConfig;

/**
 * Created by Sergio Del Castillo on 23/06/18.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class ConfigFileWriteException extends ConfigFileException {

    private final ContestConfig config;

    public ConfigFileWriteException(String text, Throwable ex, ContestConfig config) {
        super(text, ex);
        this.config = config;
    }

    public ContestConfig getConfig() {
        return config;
    }
}
