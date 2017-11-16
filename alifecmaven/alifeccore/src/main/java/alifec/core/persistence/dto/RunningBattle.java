package alifec.core.persistence.dto;

import alifec.core.simulation.Cell;

import java.util.List;

/**
 * Created by nacho on 15/11/17.
 */
public class RunningBattle {

    private float[][] nutrients;
    private List<Cell> mos;
    private int dx;
    private int dy;

    public RunningBattle(float[][] nutrients, List<Cell> mos, int dx, int dy) {
        this.nutrients = nutrients;
        this.mos = mos;
        this.dx = dx;
        this.dy = dy;
    }

    public float[][] getNutrients() {
        return nutrients;
    }

    public void setNutrients(float[][] nutrients) {
        this.nutrients = nutrients;
    }

    public void addNutrient(int x, int y, float value) {
        this.nutrients[x][x] = value;
    }

    public List<Cell> getMos() {
        return mos;
    }

    public void setMos(List<Cell> mos) {
        this.mos = mos;
    }

    public int getDx() {
        return dx;
    }

    public void setDx(int dx) {
        this.dx = dx;
    }

    public int getDy() {
        return dy;
    }

    public void setDy(int dy) {
        this.dy = dy;
    }
}
