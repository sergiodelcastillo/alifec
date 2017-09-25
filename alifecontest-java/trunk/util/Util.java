import java.io.*;
import java.util.Vector;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

public class Util {

   private void usage() {
      System.out.println("Uso: ");
      System.out.println("\tjava -jar Util.jar OPCION [PARAMETROS]");
      System.out.println("\nOPCIONES:");
      System.out.println("- MO: complila los MOs (C/C++ y Java). Este comando se " +
                         "puede utilizar cuando se codifica un MO para chequear la sintaxis." +
                         "\n\tEjemplo: " +
                         "\n\t\t java -jar Util.jar MO Contest_02/MOs/mimoc.h"+
                         "\n\t\t java -jar Util.jar MO Contest_02/MOs/mimocpp.h"+
                         "\n\t\t java -jar Util.jar MO Contest_02/MOs/mijmo.java"+
                         "\n\t Aclaración: Contest_02/MOs/ es en este caso es el directorio " +
                         "donde estan los codigos funtes de los MOs");
      System.out.println("\n- clean: elimina todos los archivos temporales" +
                         "\n\tEjemplo: " +
                         "\n\t\t java -jar Util.jar clean");
      
      System.out.println("\n- compile: compila el entorno" +
                         "\n\tEjemplo: " +
                         "\n\t\t java -jar Util.jar compile");

   }

   boolean isJavaCode(String code) {
      return code != null && code.endsWith(".java");
   }

   boolean isCppCode(String code) {
      return code != null && code.endsWith(".h");
   }

   private void compileMOs(String name) {
      File f = new File(name);

      if(!f.exists() ){
         System.out.println("Error: el archivo " + name + " no existe");
         return;
      }

      if(!f.canRead() ){
         System.out.println("Error: el archivo " + name + " no tiene permiso de lectura");
         return;
      }

      if(!f.isFile()){
         System.out.println("Error: " + name + " no es un archivo");
         return;
      }
              
      if(isJavaCode(name)) {
         compileJavaMOs(name);
      } else if(isCppCode(name)) {
         compileMOsCpp(name);
      } else {
         System.out.println("Error: debe pasar un archivo con formato .h o .java");
         return;
      }
      // cleaning
      if(name.contains(File.separator + "")) {
         name = name.substring(0, name.lastIndexOf(File.separator + ""));
      }

      clean(name);
   }

   public enum code {
      OK, ERR, COMPILER, FILE;
   }

   public static void main(String[] args) {
      Util util = new Util();
//      System.setProperty("user.dir", new File(System.getProperty("user.dir")).getParentFile().getAbsolutePath());
      
      if (args.length == 0) {
         util.usage();
      } else if (args[0].equalsIgnoreCase("compile")) {
         util.compileApp();
      } else if (args[0].equalsIgnoreCase("MO")) {
         if (args.length < 2) {
            util.usage();
            return;
         }
         util.compileMOs(args[1]);

      } else if(args[0].equalsIgnoreCase("clean")) {
         Util.clean(System.getProperty("user.dir") + File.separator + "..");
      } else {
         util.usage();
      }
   }

   public code compileJavaApp(String javaCode) {
      JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
      String source = new File(System.getProperty("user.dir")).getParentFile().getAbsolutePath();

      if (javac == null) {
         return code.COMPILER;
      }
   
      if (javac.run(null, null, null, "-sourcepath", source, source + File.separator + javaCode ) == 0) {      
         return code.OK;
      }

      return code.ERR;
   }
   public code compileJavaMOs(String javaCode) {
      JavaCompiler javac = ToolProvider.getSystemJavaCompiler();
      String source = new File(System.getProperty("user.dir")).getParentFile().getAbsolutePath();
      if (javac == null) {
         return code.COMPILER;
      }
   
      if (javac.run(null, null, null,"-sourcepath", source , javaCode ) == 0) {      
         return code.OK;
      }

      return code.ERR;
   }

   public void compileApp() {
      String path = "lib" + File.separator + "contest" + File.separator + "ContestUI.java";

      switch (compileJavaApp(path)) {
         case ERR:
            System.out.println("Error de compilacion");
            break;
         case COMPILER:
            System.out.println("No se puede encontrar el compilador");
            break;
         case FILE:
            System.out.println("Error en el archivo");
            break;
      }
   }

   public void compileMOsJava(String javaCode) {
      switch (compileJavaApp(javaCode)) {
         case ERR:
            System.out.println("Error de compilacion..");
            break;
         case COMPILER:
            System.out.println("No se puede encontrar el compilador");
            break;
         case FILE:
            System.out.println("Error en el archivo");
            break;
      }
   }

   /**
    * Compila todos los MOs C++ y genera una libreria dinamica
    * llamada libcppcolonies.dll o libcppcolonies.so de acuerdo al
    * sistema operativo. La librería se almacena en lib/MOs
    */
   private boolean compileCpp(String path) {
      try {

         String os = System.getProperty("os.name").toLowerCase();
         String[] console = {""};

         if (os.contains("linux")) {
            String com = "g++ -o \"" + path + File.separator+
                    "libcppcolonies.so\" -fPIC -Wall -shared -lm -I../lib -I\"" +
                    path + "\"  ../lib/lib_CppColony.cpp";

            console = new String[]{"/bin/bash", "-c", com};
         } else if (os.contains("windows")) {
            String com = "g++ -o \""+  path + File.separator + 
                    "libcppcolonies.dll\" -Wl,--add-stdcall-alias -Wall -shared -lm -I../lib -I\"" +
                    path + "\"  ../lib/lib_CppColony.cpp";

            console = new String[]{"cmd.exe", "/C", com};
         }

         Process p = Runtime.getRuntime().exec(console);
         p.waitFor();
        
         BufferedReader reader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
         String line;
         while( (line=reader.readLine()) != null) {
            System.out.println(line);
         } 
         
         p.getErrorStream().close();
         p.getInputStream().close();
         p.getOutputStream().close();
         
         return p.exitValue() == 0;
      } catch (IOException ex) {
         return false;
      } catch (InterruptedException e) {
         Thread.currentThread().interrupt();
         return false;
      }
   }

   private void compileMOsCpp(String p) {
      if (updateTournamentCpp(p) && updateIncludes(p)) {

         String path = getMOsPath(p);
         if (path.equals("")) {
            System.out.println("Error: Invalid Path.");
            return;
         }

        compileCpp(path);

      } else {
         System.out.println("Updating tournamets and includes [FAIL]");
      }
   }

   /**
    * Crea el archivo Tournament.cpp con los nombres que se
    * pasan como argumento.
    */
   public String getMOsPath(String path) {
      File f = new File(path);
      if (f.exists()) {
         if (f.isDirectory()) {
            return "";
         }
         try {
            return f.getParentFile().getCanonicalPath();
         } catch(java.io.IOException ex){
         }
      }
      return "";
   }

   private boolean updateTournamentCpp(String p) {
      String path = getMOsPath(p);
      if (path.equals("")) {
         return false;
      }

      Vector<String> names = AllFilter.list_names_cpp(p);
      File env = new File(".." + File.separator + "lib" + File.separator + "Environment.cpp");
      try {
         PrintWriter pw = new PrintWriter(env);

         pw.println("");
         pw.println("bool Environment::addColony(string name, int id){");
         pw.println("	CppColony<Microorganism> *mo = NULL;");
         pw.println("");

         for (String name : names) {
            pw.println("	if(name == \"" + name + "\"){");
            pw.println("	   mo = (CppColony < Microorganism > *) new CppColony< " + name + " >(id);");
            pw.println("   }");
         }

         pw.println("");
         pw.println("	colonies.push_back(mo);");
         pw.println("");
         pw.println("   return mo != NULL;");
         pw.println("}");
         pw.println("");
         pw.close();
      } catch (java.io.FileNotFoundException ex) {
         return false;
      }
      return true;
   }

   /**
    * Crea el archivo includemmos.h con los nombres que se
    * pasan como argumento:
    */
   private boolean updateIncludes(String p) {
      String path = getMOsPath(p);
      if (path.equals("")) {
         return false;
      }

      Vector<String> files = AllFilter.list_file_cpp(path);

      File includes = new File(".." + File.separator + "lib" + File.separator + "includemos.h");
      try {
         PrintWriter pw = new PrintWriter(includes);

         for (String n : files) {
            pw.println("#include \"" + n + "\"");
         }
         pw.close();
      } catch (java.io.FileNotFoundException ex) {
         return false;
      }
      return true;
   }
   

   public static void clean(String path) {
      File f = new File(path);
      File[] list = f.listFiles();
	
      for(File line : list) {
         if (line.isFile()) {
            if (line.getName().startsWith("Util")) {
               continue;
            }
            if (line.getName().endsWith(".class") || // compilados de java
                    line.getName().endsWith(".so") || // libreria dinamica en linux
                    line.getName().endsWith(".o") || // por las dudas nomas
                    line.getName().endsWith(".dll") || // libreria dinamica de windows
                    line.getName().endsWith("~")) {      // archivos temporales..
               line.delete();
            }
         } else {
            clean(line.getAbsolutePath());
         }
      }

   }
}
