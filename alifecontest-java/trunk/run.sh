#!/bin/bash

## borrar todos los compilados viejos
cd util
 java -jar util.jar clean

## compilar la aplicacion
java -jar util.jar compile

echo "ejecutando"
cd ..
java  -Djava.library.path="$(pwd)/lib/MOs" lib.ContestUI

## borrar los compilados y temporales
cd util
 java -jar util.jar clean
cd ..
