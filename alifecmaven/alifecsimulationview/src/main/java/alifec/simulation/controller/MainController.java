package alifec.simulation.controller;

import java.util.ResourceBundle;

/**
 * Created by Sergio Del Castillo on 14/06/18.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public interface MainController {
    ResourceBundle getBundle();

    void savePreferences();

    void createReportTxt();

    void createReportCsv();
}
