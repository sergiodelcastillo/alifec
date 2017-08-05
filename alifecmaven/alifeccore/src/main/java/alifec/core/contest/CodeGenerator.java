/**
 * @author Sergio Del castillo
 * @email: sergio.jose.delcastillo@gmail.com
 */

package alifec.core.contest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


/*@Todo: create an examples in a folder and just copy the files to an specific contest
 *@Todo  instead of creating them with java code
*/
public class CodeGenerator {

    private CodeGenerator() {
    }


    public static boolean generateExamples(String path) {
        return generateRandomColonyJava(path) &&
                generateMovxColonyJava(path) &&
                generateTactica1ColonyJava(path) &&
                generateTactica2ColonyJava(path) &&
                generateMovxColonyCpp(path);
    }

    private static boolean save(String path, String sourceCode) {
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(path));

            pw.println(sourceCode);
            pw.close();

        } catch (IOException ex) {
            return false;
        }
        return true;
    }

    private static boolean generateRandomColonyJava(String url) {
        StringBuffer source = new StringBuffer();

        source.append("// =======================================================\n");
        source.append("// Generated by alifecontest-java application.\n");
        source.append("//   @author Sergio Del Castillo\n");
        source.append("//   @e-mail:sergiodelcastillo@ymail.com\n");
        source.append("// =======================================================\n");
        source.append("package lib.MOs;\n\n");
        source.append("import lib.Microorganism;\n");
        source.append("import lib.Movement;\n\n");
        source.append("public class RandomColony extends Microorganism{\n\n");
        source.append("    public void move(Movement mov) {\n");
        source.append("       // you should do this source code to not throw exceptions ;)\n");
        source.append("       try {\n");
        source.append("         myStrategy(mov);\n");
        source.append("       } catch(Exception ex){\n");
        source.append("          System.out.println(\":(\");\n");
        source.append("          ex.printStackTrace();\n");
        source.append("          mov.dx = 0;\n");
        source.append("          mov.dy = 0;\n");
        source.append("       }\n");
        source.append("    }\n\n");
        source.append("    public boolean mitosis() {\n");
        source.append("       try {\n");
        source.append("         return false;\n");
        source.append("       } catch(Exception ex){\n");
        source.append("          System.out.println(\":(\");\n");
        source.append("          ex.printStackTrace();\n");
        source.append("          return false;\n");
        source.append("       }\n");
        source.append("    }\n\n");
        source.append("    public String getName() {\n");
        source.append("        return \"Random_java\";\n");
        source.append("    }\n\n");
        source.append("    public String getAuthor() {\n");
        source.append("        return \"Author\";\n");
        source.append("    }\n\n");
        source.append("	public String getAffiliation() {\n");
        source.append("		return \"UTN-FRSF\";\n");
        source.append("	}\n\n");
        source.append("   private void myStrategy(Movement mov) {\n");
        source.append("        mov.dx = new java.util.Random().nextInt(3)-1;\n");
        source.append("		  mov.dy = new java.util.Random().nextInt(3)-1;\n");
        source.append("   }\n");
        source.append("}\n");
        
        return save(url + File.separator + "RandomColony.java", source.toString());
    }

    private static boolean generateMovxColonyJava(String url) {
        StringBuffer source = new StringBuffer();

        source.append("// =======================================================\n");
        source.append("// Generated by alifecontest-java application.\n");
        source.append("//   @author Sergio Del Castillo\n");
        source.append("//   @e-mail:sergiodelcastillo@ymail.com\n");
        source.append("// =======================================================\n\n");
        source.append("package lib.MOs;\n");
        source.append("\n");
        source.append("import lib.Microorganism;\n");
        source.append("import lib.Movement;\n\n");
        source.append("public class Movx extends Microorganism{\n\n");
        source.append("     // relative movement.\n");
        source.append("     public void move(Movement mov){\n");
        source.append("		mov.dx = new java.util.Random().nextInt(3)-1;\n");
        source.append("      mov.dy = 0;\n");
        source.append("    }\n\n");
        source.append("    public boolean mitosis() {\n");
        source.append("       //never is duplicated\n");
        source.append("        return false;\n");
        source.append("    }\n\n");
        source.append("    public String getName() {\n");
        source.append("      return \"MovX_java\";\n");
        source.append("   }\n\n");
        source.append("    public String getAuthor() {\n");
        source.append("        return \"Author\";\n");
        source.append("    }\n\n");
        source.append("	public String getAffiliation() {\n");
        source.append("		return \"UTN-FRSF\";\n");
        source.append("	}\n}\n");

        return save(url + File.separator + "Movx.java", source.toString());
    }

    private static boolean generateTactica1ColonyJava(String url) {
        StringBuffer source = new StringBuffer();
        source.append("// =======================================================\n");
        source.append("// Generated by alifecontest-java application.\n");
        source.append("//   @author Sergio Del Castillo\n");
        source.append("//   @e-mail:sergiodelcastillo@ymail.com\n");
        source.append("// =======================================================\n");
        source.append("package lib.MOs;\n\n");
        source.append("import java.awt.Point;\n");
        source.append("import lib.Defs;\n");
        source.append("import lib.Microorganism;\n");
        source.append("import lib.Petri;\n");
        source.append("import lib.Movement;\n\n");
        source.append("public class Tactica1 extends Microorganism{\n");
        source.append("   Petri petri = Petri.getInstance();\n");
        source.append("   public void move(Movement mov) {\n");
        source.append("      mov.dx = mov.dy = 0;\n\n");
        source.append("      //if I can attack you --> I kill you :o\n");
        source.append("      for(int i = -1; i <= 1; i++){\n");
        source.append("         for(int j = -1; j <= 1; j++){\n");
        source.append("            Point p = new Point(pos.x+i, pos.y+j);\n\n");
        source.append("            if(petri.inDish(p) &&\n");
        source.append("               petri.canCompite(pos, p) &&\n");
        source.append("               ene > petri.getEnergy(p.x, p.y)){\n");
        source.append("               mov.dx = i;\n");
        source.append("               mov.dy = j;\n");
        source.append("               return;\n");
        source.append("            }\n");
        source.append("         }\n");
        source.append("      }\n");
        source.append("      // if I can't attack -->   Seek the maximun of Relative Nutrient!!\n");
        source.append("      for(int i = -1; i <= 1; i++){\n");
        source.append("        for(int j = -1; j <= 1; j++){\n");
        source.append("           Point current = new Point (pos.x+i, pos.y+j);\n");
        source.append("           Point best = new Point(pos.x+mov.dx,pos.y+mov.dy);\n\n");
        source.append("           if(petri.inDish(current) &&\n");
        source.append("              petri.getNutrient(best.x, best.y) < petri.getNutrient(current.x, current.y)){\n");
        source.append("               mov.dx = i;\n");
        source.append("               mov.dy = j;\n");
        source.append("           }\n");
        source.append("         }\n");
        source.append("      }\n");
        source.append("   }\n\n");
        source.append("   public boolean mitosis() {\n");
        source.append("      return (this.ene > Defs.E_INITIAL*2);\n");
        source.append("   }\n\n");
        source.append("   public String getName() {\n");
        source.append("      return \"Tactica1_java\";\n");
        source.append("   }\n\n");
        source.append("   public String getAuthor() {\n");
        source.append("      return \"Tactica1\";\n");
        source.append("   }\n\n");
        source.append("	public String getAffiliation() {\n");
        source.append("		return \"UTN-FRSF\";\n");
        source.append("	}\n}\n");

        return save(url + File.separator + "Tactica1.java", source.toString());
    }

    private static boolean generateTactica2ColonyJava(String url) {
        StringBuffer source = new StringBuffer();

        source.append("// =======================================================\n");
        source.append("// Generated by alifecontest-java application.\n");
        source.append("//   @author Sergio\n");
        source.append("//   @e-mail:sergiodelcastillo@ymail.com\n");
        source.append("// =======================================================\n\n");
        source.append("package lib.MOs;\n\n");
        source.append("import java.awt.Point;\n");
        source.append("import lib.Defs;\n");
        source.append("import lib.Microorganism;\n");
        source.append("import lib.Movement;\n");
        source.append("import lib.Petri;\n\n");
        source.append("public class Tactica2 extends Microorganism{\n\n");
        source.append("    public void move(Movement mov) {\n");
        source.append("        Point p = new Point();\n");
        source.append("        for(int i = -1; i <= 1; i++){\n");
        source.append("           for(int j = -1; j <= 1; j++){\n");
        source.append("              try{\n");
        source.append("                  p.x = pos.x+i;\n");
        source.append("                  p.y = pos.y+j;\n");
        source.append("                 if(Petri.getInstance().inDish(p) &&\n");
        source.append("                    Petri.getInstance().canCompite(pos, p)){\n");
        source.append("                     if(Petri.getInstance().getEnergy(p.x, p.y) < ene-Defs.LESS_MOVE){\n");
        source.append("                        mov.dx = i;\n");
        source.append("                        mov.dy = j;\n");
        source.append("                        return;\n");
        source.append("                     }\n");
        source.append("                 }\n");
        source.append("              } catch(NullPointerException ex){ }\n");
        source.append("           }\n");
        source.append("       }\n");
        source.append("       for(int i = -1; i <= 1; i++){\n");
        source.append("         for(int j = -1; j <= 1; j++){\n");
        source.append("           try{\n");
        source.append("               p.x = pos.x+i;\n");
        source.append("               p.y = pos.y+j;\n");
        source.append("               if(Petri.getInstance().inDish(p)){\n");
        source.append("                  // I like 1.4  --> you can change it!\n");
        source.append("                 if(Petri.getInstance().getNutrient(pos.x, pos.y)*1.4 <\n");
        source.append("                    Petri.getInstance().getNutrient(p.x,p.y)){  \n");
        source.append("                     mov.dx = i;\n");
        source.append("                      mov.dy = j;\n");
        source.append("                      return;\n");
        source.append("                   }\n");
        source.append("                 }\n");
        source.append("              } catch(NullPointerException ex){}\n");
        source.append("           }\n");
        source.append("       }\n");
        source.append("    }\n\n");
        source.append("    public boolean mitosis() {\n");
        source.append("      return false;\n");
        source.append("    }\n\n");
        source.append("    public String getName() {\n");
        source.append("        return \"Tactica2_java\";\n");
        source.append("    }\n\n");
        source.append("    public String getAuthor() {\n");
        source.append("        return \"Tactica2\";\n");
        source.append("    }\n\n");
        source.append("	  public String getAffiliation() {\n");
        source.append("		   return \"UTN-FRSF\";\n");
        source.append("	  }\n}\n");

        return save(url + File.separator + "Tactica2.java", source.toString());
    }

    public static boolean generateMovxColonyCpp(String url) {
        StringBuffer source = new StringBuffer();

        source.append("// =======================================================\n");
        source.append("// Generated by alifecontest-java application.\n");
        source.append("//   @author Sergio Del Castillo\n");
        source.append("//   @e-mail:sergiodelcastillo@ymail.com\n");
        source.append("// =======================================================\n\n");
        source.append("#ifndef MOVX_H\n");
        source.append("#define MOVX_H\n\n");
        source.append("#include<cstdlib>\n");
        source.append("#include <iostream>\n");
        source.append("using namespace std;\n\n");
        source.append("#include \"Microorganism.h\"\n");
        source.append("#include \"Petri.h\"\n\n");
        source.append("class Movx: public Microorganism{\n");
        source.append("	public:\n");
        source.append("		Movx(){\n");
        source.append("			srand(time(NULL));\n");
        source.append("		}\n");
        source.append("		void move(Movement &mov) {\n");
        source.append("			mov.dx = (rand() % 3) -1;\n");
        source.append("			mov.dy = 0;\n");
        source.append("		};\n\n");
        source.append("		bool mitosis(){\n");
        source.append("			return false;\n");
        source.append("		};\n\n");
        source.append("		string getName(){\n");
        source.append("			return \"Movx_cpp\";\n");
        source.append("		};\n\n");
        source.append("		string getAuthor(){\n");
        source.append("			return \"Author\";\n");
        source.append("		};\n\n");
        source.append("		string getAffiliation(){\n");
        source.append("			return \"UTN-FRSF\";\n");
        source.append("		};\n");
        source.append("};\n\n");
        source.append("#endif\n\n");

        return save(url + File.separator + "movx.h", source.toString());
    }

}
