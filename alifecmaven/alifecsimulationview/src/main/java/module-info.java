/**
 * Created by Sergio Del Castillo on 20/05/19.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
module alifec.simulation.view {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;

    requires org.apache.logging.log4j;
    requires org.apache.logging.log4j.core;
    requires org.apache.logging.log4j.iostreams;

    requires alifec.core;

    exports alifec.simulation.main;
    exports alifec.simulation.controller;
    exports alifec.simulation.util;

}