#!/bin/bash


#mvn clean install -DskipTests
mvn clean install 
mvn exec:exec -pl alifeccontestview 

