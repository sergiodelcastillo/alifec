#!/bin/bash

cd ..
java --module-path lib/ -m alifec.simulation.view/alifec.simulation.main.ALifeContestMain

# --add-modules javafx.base,javafx.graphics,alifec.simulation.view,alifec.core
# --add-reads javafx.base=ALL-UNNAMED
# --add-reads javafx.graphics=ALL-UNNAMED
