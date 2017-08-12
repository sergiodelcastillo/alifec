// =======================================================
// Generated by alifecontest-java application.
//   @author Sergio
//   @e-mail:sergiodelcastillo@ymail.com
// =======================================================

package alifec.core.simulation.MOs;

import java.awt.Point;

import alifec.core.simulation.Defs;
import alifec.core.simulation.Microorganism;
import alifec.core.simulation.Movement;
import alifec.core.simulation.Petri;

public class Tactica2 extends Microorganism {

    public void move(Movement mov) {
        Point p = new Point();
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                try {
                    p.x = pos.x + i;
                    p.y = pos.y + j;
                    if (Petri.getInstance().inDish(p) &&
                            Petri.getInstance().canCompete(pos, p)) {
                        if (Petri.getInstance().getEnergy(p.x, p.y) < ene - Defs.LESS_MOVE) {
                            mov.dx = i;
                            mov.dy = j;
                            return;
                        }
                    }
                } catch (NullPointerException ex) {
                }
            }
        }
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                try {
                    p.x = pos.x + i;
                    p.y = pos.y + j;
                    if (Petri.getInstance().inDish(p)) {
                        // I like 1.4  --> you can change it!
                        if (Petri.getInstance().getNutrient(pos.x, pos.y) * 1.4 <
                                Petri.getInstance().getNutrient(p.x, p.y)) {
                            mov.dx = i;
                            mov.dy = j;
                            return;
                        }
                    }
                } catch (NullPointerException ex) {
                }
            }
        }
    }

    public boolean mitosis() {
        return false;
    }

    public String getName() {
        return "Tactica2_java";
    }

    public String getAuthor() {
        return "Tactica2";
    }

    public String getAffiliation() {
        return "UTN-FRSF";
    }
}

