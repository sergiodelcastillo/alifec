// =======================================================
// Generated by alifecontest-java application.
//   @author Sergio Del Castillo
//   @e-mail:sergiodelcastillo@ymail.com
// =======================================================
package lib.MOs;

import java.awt.Point;
import lib.Defs;
import lib.Microorganism;
import lib.Petri;
import lib.Movement;

public class Tactica1 extends Microorganism{
   Petri petri = Petri.getInstance();
   public void move(Movement mov) {
      mov.dx = mov.dy = 0;

      //if I can attack you --> I kill you :o
      for(int i = -1; i <= 1; i++){
         for(int j = -1; j <= 1; j++){
            Point p = new Point(pos.x+i, pos.y+j);

            if(petri.inDish(p) &&
               petri.canCompite(pos, p) &&
               ene > petri.getEnergy(p.x, p.y)){
               mov.dx = i;
               mov.dy = j;
               return;
            }
         }
      }
      // if I can't attack -->   Seek the maximun of Relative Nutrient!!
      for(int i = -1; i <= 1; i++){
        for(int j = -1; j <= 1; j++){
           Point current = new Point (pos.x+i, pos.y+j);
           Point best = new Point(pos.x+mov.dx,pos.y+mov.dy);

           if(petri.inDish(current) &&
              petri.getNutrient(best.x, best.y) < petri.getNutrient(current.x, current.y)){
               mov.dx = i;
               mov.dy = j;
           }
         }
      }
   }

   public boolean mitosis() {
      return (this.ene > Defs.E_INITIAL*2);
   }

   public String getName() {
      return "Tactica1_java";
   }

   public String getAuthor() {
      return "Tactica1";
   }

	public String getAffiliation() {
		return "UTN-FRSF";
	}
}

