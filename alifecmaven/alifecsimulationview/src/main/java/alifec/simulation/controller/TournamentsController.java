package alifec.simulation.controller;

import javafx.event.ActionEvent;

/**
 * Created by Sergio Del Castillo on 16/06/18.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class TournamentsController {

    public void previousTournament(ActionEvent event) {
        System.out.println("prev tournament");
    }

    public void nextTournament(ActionEvent event) {
        System.out.println("next tournament");
    }

    public void deleteTournament(ActionEvent event) {
        System.out.println("delete tournament");
    }

    public void addTournament(ActionEvent event) {
        System.out.println("create new tournament");
    }
}
