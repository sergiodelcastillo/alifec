#!/bin/bash

cd ..
MODULE_PATH=$(find lib -name '*.jar' -printf '%p:' | sed 's/:$//')

java -p $MODULE_PATH -m alifec.simulation.view/alifec.simulation.main.ALifeContestMain