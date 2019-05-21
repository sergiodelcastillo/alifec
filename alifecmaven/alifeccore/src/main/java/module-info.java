/**
 * Created by Sergio Del Castillo on 20/05/19.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
module alifec.core {
    requires org.apache.logging.log4j;
    requires org.apache.logging.log4j.core;
    requires org.apache.logging.log4j.iostreams;

    requires java.compiler;

    exports alifec.core.compilation;
    exports alifec.core.contest;
    exports alifec.core.contest.oponentInfo;
    exports alifec.core.event;
    exports alifec.core.event.impl;
    exports alifec.core.exception;
    exports alifec.core.persistence;
    exports alifec.core.persistence.config;
    exports alifec.core.persistence.custom;
    exports alifec.core.persistence.dto;
    exports alifec.core.simulation;
    exports alifec.core.simulation.MOs;
    exports alifec.core.simulation.rules;
    exports alifec.core.simulation.nutrient;
    exports alifec.core.simulation.nutrient.function;
    exports alifec.core.validation;

}