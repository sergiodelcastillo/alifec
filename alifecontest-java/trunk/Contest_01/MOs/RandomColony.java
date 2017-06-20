// =======================================================
// Generated by alifecontest-java application.
//   @author Sergio Del Castillo
//   @e-mail:sergiodelcastillo@ymail.com
// =======================================================
package lib.MOs;

import lib.Microorganism;
import lib.Movement;

public class RandomColony extends Microorganism{

    public void move(Movement mov) {
       // you should do this source code to not throw exceptions ;)
       try {
         myStrategy(mov);
       } catch(Exception ex){
          System.out.println(":(");
          ex.printStackTrace();
          mov.dx = 0;
          mov.dy = 0;
       }
    }

    public boolean mitosis() {
       try {
         return false;
       } catch(Exception ex){
          System.out.println(":(");
          ex.printStackTrace();
          return false;
       }
    }

    public String getName() {
        return "Random_java";
    }

    public String getAuthor() {
        return "Author";
    }

	public String getAffiliation() {
		return "UTN-FRSF";
	}

   private void myStrategy(Movement mov) {
        mov.dx = new java.util.Random().nextInt(3)-1;
		  mov.dy = new java.util.Random().nextInt(3)-1;
   }
}

