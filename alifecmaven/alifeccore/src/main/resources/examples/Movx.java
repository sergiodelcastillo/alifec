// =======================================================
// Generated by alifecontest-java application.
//   @author Sergio Del Castillo
//   @e-mail:sergiodelcastillo@ymail.com
// =======================================================

package MOs;

import alifec.core.simulation.Microorganism;
import alifec.core.simulation.Movement;

public class Movx extends Microorganism {

    // relative movement.
    public void move(Movement mov) {
        mov.dx = new java.util.Random().nextInt(3) - 1;
        mov.dy = 0;
    }

    public boolean mitosis() {
        //do not duplicate
        return false;
    }

    public String getName() {
        return "MovX_java";
    }

    public String getAuthor() {
        return "Author";
    }

    public String getAffiliation() {
        return "UTN-FRSF";
    }
}
