package alifec.core.exception;

import alifec.core.contest.ContestConfig;

/**
 * Created by Sergio Del Castillo on 15/08/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class SaveContestConfigException extends Exception {
    private final ContestConfig config;

    public SaveContestConfigException(String s, ContestConfig contestConfig) {
        super(s);
        this.config = contestConfig;
    }

    public ContestConfig getConfig() {
        return config;
    }
}
