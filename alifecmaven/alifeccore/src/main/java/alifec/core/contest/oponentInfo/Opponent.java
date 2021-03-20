package alifec.core.contest.oponentInfo;

import alifec.core.exception.OpponentException;
import alifec.core.persistence.OpponentFileManager;
import alifec.core.persistence.config.ContestConfig;
import alifec.core.simulation.Competitor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;


public class Opponent {
    private Logger logger = LogManager.getLogger(getClass());

    private List<OpponentInfo> opponents = new ArrayList<>();
    private boolean isCompetitionMode;
    private OpponentFileManager persistence;

    public Opponent(String competitorsFile, boolean isCompetitionMode) throws OpponentException {
        this.isCompetitionMode = isCompetitionMode;
        try {
            this.persistence = new OpponentFileManager(competitorsFile, isCompetitionMode);

            if (isCompetitionMode) {
                opponents.addAll(persistence.readAll());
            }
        } catch (Throwable t) {
            throw new OpponentException("Opponent's list can not be loaded.", t);
        }
    }

    public List<OpponentInfo> getOpponents() {
        return this.opponents;
    }

    public void addMissing(List<Competitor> list) throws OpponentException {
        try {
            for (Competitor comp : list) {
                if (!opponents.contains(comp.getInfo()))
                    opponents.add(comp.getInfo());
            }

            if (isCompetitionMode) {
                persistence.saveAll(opponents);
            }
        } catch (Throwable t) {
            throw new OpponentException("Opponent's list can not be saved.", t);
        }
    }
}
