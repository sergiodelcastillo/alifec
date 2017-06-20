####################################################################################################
##    Página Oficial:            www.alifecontest.sourceforge.net 
##    Documentación detallada:   dentro del proyecto, en la carpeta "doc", el archivo "readme.pdf" 
##    Autor: Sergio Del Castillo
##    Consultas a: http://lists.sourceforge.net/mailman/listinfo/alifecontest-microorganisms
####################################################################################################

A continuación se detallan los pasos básicos para instalar el Contest:
 
## Instalación: Pasos Generales
1. Descomprimir el archivo alifecontest-java-0.02.zip.
2. Instalar maquina virtual y entorno de desarrollo java de SUN.
3. Instalar compilador de C/C++ (gcc y g++).
4. Iniciar el entorno.

## Usuarios de Windows
1. Bajar e instalar JDK (1*) ( versión ≥ 1,6 )

2. Bajar e instalar MINGW (2*) de la página oficial. Pero se lo puede descargar más fácil del siguiente
     link (3*) . El archivo que hay que descargar se llama MinGW-5.1.4.exe y se encuentra en la sección
     Automated MinGW Installer. Dentro de la instalación, en el tercer paso se pide TILDAR los com-
     ponentes a instalar, se deben tildar: g++ compiler, Java compiler y MinGW Make.

3. Configurar variable de entorno PATH de windows:para modificar el PATH se debe acceder a las
     propiedades del Sistema utilizando alguna de las alternativas:
        a) Vista Clásica: Inicio → Panel de Control → Sistema.
        b) Vista por Categorías: Rendimiento y Mantenimiento → Sistema
     Luego en el tab de Opciones Avanzadas, hacer click en Variables de Entorno, buscar PATH en
     la sección Variables del Sistema y hacer click en modificar para editarlo.
     Se debe agregar al inicio el directorio donde se encuentran instalados los archivos binarios de java
     JDK, normalmente se encuentran en C:\Archivos de Programa\java\jdk1.6.XXX\bin. A MINGW, 
     se lo encuentra generalmente en C:\Mingw\bin, también agregarlo al PATH.
     Por ejemplo:
     Directorio de java: C:\Archivos de programa\Java\jdk1.6.0_16\bin
     Directorio de MINGW: C:\MinGW\bin
     Variable PATH: C:\msys\1.0\bin;
     Luego de modificar el PATH queda: C:\Mingw\bin;C:\Archivos de programa\Java\jdk1.6.0_16\bin;C:\msys\1.0\bin;

4. Ejecutar en consola o hacer doble click en run.bat.

## Usuarios de GNU/Linux
Bajar e instalar Java Development Kit (JDK)
Instalar g++
Ejecutar en consola sh run.sh
 
## Descripción detallada para GNU/Linux Ubuntu:

Instalar el paquete de java para desarrolladores (Java Development Kit):
   sudo apt-get install sun-java-jdk

Instalar el compilador de C++:
   sudo apt-get install g++

Instalar el compilador de GNU de java que tiene las librerías Nativas para conectar C/C++ con Java:
   sudo apt-get install gcj gij

Iniciar el entorno:
   sh run.sh

Links:
1*) Instalador JDK: http://java.sun.com/javase/downloads/index.jsp
2*) Pagina Oficial de MINGW: http://www.mingw.org/wiki/HOWTO_Install_the_MinGW_GCC_Compiler_Suite
3*) Instalador de MINGW: http://sourceforge.net/projects/mingw/files/
