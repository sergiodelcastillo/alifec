% borrar todos los compilados viejos %
@cd util
@java -jar util.jar clean

@echo Compilando la aplicacion
@java -jar util.jar compile

@echo ejecutando
@ cd ..
@java  -Djava.library.path="%cd%\lib\MOs" lib.contest.ContestUI

% borrar los compilados %
@ cd util
@java -jar Util.jar clean
@ cd ..
