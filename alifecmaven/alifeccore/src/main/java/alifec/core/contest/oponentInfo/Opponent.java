package alifec.core.contest.oponentInfo;

import alifec.core.exception.OpponentException;
import alifec.core.persistence.OpponentFileManager;
import alifec.core.persistence.config.ContestConfig;
import alifec.core.simulation.Competitor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Opponent {
    private Logger logger = LogManager.getLogger(getClass());

    private List<OpponentInfo> opponents = new ArrayList<>();
    private ContestConfig config;
    private OpponentFileManager persistence;

    public Opponent(ContestConfig config) throws OpponentException {
        this.config = config;

        try {
            this.persistence = new OpponentFileManager(config.getCompetitorsFile(), config.isCompetitionMode());

            if (config.isCompetitionMode()) {
                opponents.addAll(persistence.readAll());
            }
        } catch (Throwable t) {
            throw new OpponentException("Opponent's list can not be loaded.", t);
        }
    }

    public List<OpponentInfo> getOpponents() {
        return this.opponents;
    }

    public void addMissing(List<Competitor> list) {
        for (Competitor comp : list) {
            if (!opponents.contains(comp.getInfo()))
                opponents.add(comp.getInfo());
        }

        if (config.isCompetitionMode()) {
            persistence.saveAll(opponents);
        }
    }
}
