package view.tournament;

import controller.java.tournament.TournamentManager;
import exceptions.CreateRankingException;
import exceptions.CreateTournamentException;
import view.contest.ContestView;
import view.contest.Message;
import view.contest.RankingView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by IntelliJ IDEA.
 * User: yeyo
 * Email: yeyo@druidalabs.com
 * Date: Oct 22, 2010
 * Time: 1:27:42 AM
 */
public class TournamentHandler implements ActionListener {
    private TournamentView father;

    public TournamentHandler(TournamentView father) {
        this.father = father;
    }

    public void actionPerformed(ActionEvent e) {
        if (father.isPrevTournament(e.getSource())) {
            father.previousView();
        } else if (father.isNextTournament(e.getSource())) {
            father.nextView();
        } else if (father.isRanking(e.getSource())) {
            try {
                ContestView root = father.getFather();
                new RankingView(root, root.getContest().getRanking());
            } catch (CreateRankingException ex) {
                Message.printErr(father, ex.getMessage());
            }
        } else if (father.isAddTournament(e.getSource())) {
            String txt = "Are you sure you want to add a new tournament?";

            if (JOptionPane.OK_OPTION == Message.printYesNoCancel(father.getFather(), txt)) {
                try {
                    father.addTournament();
                } catch (CreateTournamentException ex) {
                    Message.printErr(father.getFather(), "Can't create a new Tournament");
                }
            }

        } else if (father.isDelTournament(e.getSource())) {
            TournamentManager manager = father.getTournamentManager();
            int selected = manager.getSelectedIndex();
            String txt = "Are you sure you want to delete " + manager.getSelected().getName() + "?";

            if (JOptionPane.OK_OPTION == Message.printYesNoCancel(father, txt)) {
                if (manager.getSize() == 0) {
                    Message.printErr(father, "The are not tournament to be removed.");
                    return;
                }
                if (manager.getSize() == 1) {
                    Message.printErr(father, "You can't delete the tournament. The application requires at least one tournament for the contest");
                    return;
                }

                if (!father.deleteTournament(selected)) {
                    Message.printErr(father, "The tournament can't be removed");
                }
            }
        }
    }
}
