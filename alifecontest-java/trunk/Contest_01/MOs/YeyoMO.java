// =======================================================
// Generated by alifecontest-java application.
//   @author Sergio Del Castillo
//   @e-mail:sergiodelcastillo@ymail.com
// =======================================================
package lib.MOs;

import java.awt.*;
import java.util.*;

import lib.Defs;
import lib.Microorganism;
import lib.Petri;
import lib.Movement;

public class YeyoMO extends Microorganism {
    class CellMov implements Comparable<CellMov> {
        int dx;
        int dy;
        float nutri;

        CellMov(int dx, int dy, float nutri) {
            this.dx = dx;
            this.dy = dy;
            this.nutri = nutri;
        }

        public int compareTo(CellMov o) {
            if (nutri == o.nutri) return 0;

            return nutri > o.nutri ? -1 : 1;
        }
    }

    class CellPos implements Comparable<CellPos> {
        int x;
        int y;
        float nutri;

        public CellPos(int x, int y, float nutri) {
            this.x = x;
            this.y = y;
            this.nutri = nutri;
        }

        public int compareTo(CellPos o) {
            return Float.compare(o.nutri, nutri);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CellPos cellPos = (CellPos) o;
            return Float.compare(cellPos.nutri, nutri) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(nutri);
        }
    }

    Petri petri = Petri.getInstance();
    private int MAX_DS = 25;
    private int TIME_MOVE = 150;

    public void move(Movement mov) {
        mov.dx = mov.dy = 0;

        float currentNutri = petri.getNutrient(pos.x, pos.y) * 1.01f;
        int myId = petri.getOpponent(pos.x, pos.y);
        int dsBetter = 1;
        java.util.List<CellPos> list = new ArrayList<CellPos>();

        for (int ds = 1; ds < MAX_DS; ds++) {
            //up
            int y1 = pos.y + ds;
            int x1 = Math.max(pos.x - ds, 0);
            int maxX1 = Math.min(pos.x + ds, Defs.DIAMETER);

            if (petri.inDish(Defs.RADIUS, y1)) {
                for (; x1 <= maxX1; x1++) {
                    if (!petri.inDish(x1, y1)) continue;

                    if (myId != petri.getOpponent(x1, y1) && petri.getNutrient(x1, y1) > 100f)
                        list.add(new CellPos(x1, y1, petri.getNutrient(x1, y1)));
                }
            }

            //bottom
            int y2 = pos.y - ds;
            int x2 = Math.max(pos.x - ds, 0);
            int maxX2 = Math.min(pos.x + ds, Defs.DIAMETER);

            if (petri.inDish(Defs.RADIUS, y2)) {
                for (; x2 <= maxX2; x2++) {
                    if (!petri.inDish(x2, y2)) continue;

                    if (myId != petri.getOpponent(x2, y2) && petri.getNutrient(x2, y2) > 100f)
                        list.add(new CellPos(x2, y2, petri.getNutrient(x2, y2)));
                }
            }

            //left
            int x3 = pos.x - ds;
            int y3 = Math.max(pos.y - ds, 0);

            int maxY3 = Math.min(pos.y + ds, Defs.DIAMETER);

            if (petri.inDish(x3, Defs.RADIUS)) {
                for (; y3 <= maxY3; y3++) {
                    if (!petri.inDish(x3, y3)) continue;

                    if (myId != petri.getOpponent(x3, y3) && petri.getNutrient(x3, y3) > 100f)
                        list.add(new CellPos(x3, y3, petri.getNutrient(x3, y3)));
                }
            }

            //right
            int x4 = pos.x + ds;
            int y4 = Math.max(pos.y - ds, 0);

            int maxY4 = Math.min(pos.y + ds, Defs.DIAMETER);

            if (petri.inDish(x4, Defs.RADIUS)) {
                for (; y4 <= maxY4; y4++) {
                    if (!petri.inDish(x4, y4)) continue;

                    if (myId != petri.getOpponent(x4, y4) && petri.getNutrient(x4, y4) > 100f)
                        list.add(new CellPos(x4, y4, petri.getNutrient(x4, y4)));
                }
            }

            //same distance, sort by nutrients
            try {
                Collections.sort(list);
            } catch (Exception ex) {
                ex.printStackTrace();
            }


            for (CellPos cellPos : list) {
                Point temp = new Point(cellPos.x, cellPos.y);

                if (canMove(pos, temp, myId, ds)) {
                    if (eatTest(pos.x + mov.dx, pos.y + mov.dy, dsBetter, temp.x, temp.y, ds) &&
                            currentNutri < cellPos.nutri) {
                        currentNutri = cellPos.nutri;
                        mov.dx = temp.x - pos.x;
                        mov.dy = temp.y - pos.y;
                        dsBetter = ds;
                    }
                }

            }
            //clean files
            list.clear();

            if (mov.isMoved()) break;
        }
        // find a relative movement and assign it
        Movement rel = findRelative(pos, mov, myId);
        mov.dx = rel.dx;
        mov.dy = rel.dy;
    }

    private Movement findRelative(Point pos, Movement mov, int myId) {
        if (mov.dx == 0 && mov.dy == 0) {
            return new Movement(0, 0);
        }
        int dx1 = mov.dx;
        int dy1 = mov.dy;

        if (dx1 != 0) dx1 /= Math.abs(dx1);
        if (dy1 != 0) dy1 /= Math.abs(dy1);

        if (canMove(pos, new Point(pos.x + dx1, pos.y + dy1), myId, 1)) {
            return new Movement(dx1, dy1);
        }

        java.util.List<CellMov> relatives = new ArrayList<CellMov>();

        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (i == 0 && j == 0) continue;
                Point p = new Point(pos.x + i, pos.y + j);

                if (!petri.inDish(p.x, p.y)) continue;

                if (myId != petri.getOpponent(p.x, p.y)) {
                    relatives.add(new CellMov(i, j, petri.getNutrient(p.x, p.y)));
                }
            }
        }

        if (relatives.isEmpty()) return new Movement(0,0);

        Collections.sort(relatives);



        for (CellMov relative : relatives) {
            int diff = Math.abs(dx1 - relative.dx) + Math.abs(dy1 - relative.dy);

            if (diff < 2) {
                return new Movement(relative.dx, relative.dy);
            }
        }


        return new Movement(0, 0);

    }

    private boolean canMove(Point pos, Point point, int myId, int ds) {
        if (!petri.inDish(point)) return false;

        int curId = petri.getOpponent(point.x, point.y);

        if (curId == myId) return false;

        if (curId == -1) return true;

        float myEnergy = petri.getEnergy(pos.x, pos.y);
        float enemyEnergy = petri.getEnergy(point.x, point.y);

        myEnergy -= Defs.LESS_LIVE * ds;
        myEnergy -= Defs.LESS_MOVE * ds;
        enemyEnergy -= Defs.LESS_LIVE * (ds - 1);
        enemyEnergy -= Defs.LESS_MOVE * (ds - 1);

        //do not calculate what energy the MO will eat until I reach him
        // because it is really difficult to have a aproximation.

        if (myEnergy <= 0f) return false;

        return myEnergy > enemyEnergy * 1.05f;
    }

    private boolean eatTest(int x1, int y1, int ds1, int x2, int y2, int ds2) {
        float eneOri = petri.getEnergy(pos.x, pos.y);
        float ene1 = petri.getEnergy(pos.x, pos.y);
        float ene2 = petri.getEnergy(pos.y, pos.y);
        float nutri1 = petri.getNutrient(x1, y1);
        float nutri2 = petri.getNutrient(x2, y2);

        if (nutri1 > nutri2) {
            System.out.println("nutri1 > nutri2=" + nutri1 + "," + nutri2+ ",eneOri=" + eneOri + ",ene1=" + ene1 + ",ene2=" + ene2 + ",nutri(" + petri.getNutrient(pos.x, pos.y) + "," + petri.getNutrient(x1, y1) + "," + petri.getNutrient(x2, y2) + "),ds=("+ds1+","+ds2+")");
            return false;
        }
        eneOri += (petri.getNutrient(pos.x, pos.y) * 0.01f * TIME_MOVE);
        ene1 += (nutri1 * 0.01f * TIME_MOVE); // 70
        ene2 += (nutri2 * 0.01f * TIME_MOVE); // 70

        if (ds1 <= TIME_MOVE) {
            ene1 -= (Defs.LESS_MOVE * ds1);
        } else {
            ene1 -= (Defs.LESS_MOVE * TIME_MOVE);
        }

        if (ds2 <= TIME_MOVE) {
            ene2 -= (Defs.LESS_MOVE * ds2);
        } else {
            ene2 -= (Defs.LESS_MOVE) * TIME_MOVE;
        }


        boolean status = ene2 > ene1 && ene2 > eneOri;
        System.out.println("status=" + status + ",eneOri=" + eneOri + ",ene1=" + ene1 + ",ene2=" + ene2 + ",nutri(" + petri.getNutrient(pos.x, pos.y) + "," + petri.getNutrient(x1, y1) + "," + petri.getNutrient(x2, y2) + "),ds=("+ds1+","+ds2+")");
        return status;
    }

    public boolean mitosis() {
        return (this.ene > Defs.E_INITIAL * 2.5);
    }

    public String getName() {
        return "Yeyo";
    }

    public String getAuthor() {
        return "YeyoMO";
    }

    public String getAffiliation() {
        return "Example";
    }
}

