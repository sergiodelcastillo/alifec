package alifec.core.contest;

import alifec.core.exception.CreateBattleException;

public class BattleResult implements Comparable<BattleResult>{

    public String nutrient;
    public String name1;
    public String name2;

    public int nutrientID = -1;
    public int ID1 = -1;
    public int ID2 = -1;

    private float winner_energy = 0.0f;
    private int winnerID = -1;

    public BattleResult(int op1, int op2, int nutri, String name1, String name2, String n)
            throws CreateBattleException {
        if (op1 == op2)
            throw new CreateBattleException("Opponent 1 and 2 can not be the same.");
        if (nutri < 0 || op1 < 0 || op2 < 0)
            throw new CreateBattleException("Unknown error, can not create this battle.");

        this.ID1 = op1;
        this.ID2 = op2;
        this.nutrientID = nutri;
        this.nutrient = n;
        this.name1 = name1;
        this.name2 = name2;
    }

    public float energy1() {
        return (winnerID == ID1) ? winner_energy : 0.f;
    }

    public float energy2() {
        return (winnerID == ID2) ? winner_energy : 0.f;
    }

    @Override
    public String toString() {
        return name1.toUpperCase() +
                " vs " + name2.toUpperCase() +
                " in " + nutrient.toUpperCase();
    }

    @Override
    public boolean equals(Object ob) {
        if (ob == null || !(ob instanceof BattleResult))
            return false;
        BattleResult b = (BattleResult) ob;
        return nutrientID == b.nutrientID &&
                ID1 == b.ID1 &&
                ID2 == b.ID2;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + this.nutrientID;
        hash = 11 * hash + this.ID1;
        hash = 11 * hash + this.ID2;
        return hash;
    }

    public Battle toBattle(){
        return new Battle(this);
    }

    public void setWinner(int id, float energy){
        this.winnerID = id;
        this.winner_energy = energy;
    }

    @Override
    public int compareTo(BattleResult o) {
        int result = name1.compareTo(o.name1);

        if (result != 0) return result;

        result = name2.compareTo(o.name2);

        if (result != 0) return result;

        return nutrient.compareTo(o.nutrient);
    }
}
