/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

package controller.java;

import java.util.ArrayList;

/**
 * The class role is manage the microorganism behavior.
 */
public abstract class Colony {
    protected final int id;

    private final ArrayList<Cell> moList = new ArrayList<Cell>();

    public Colony(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("The colony id is a positive number.");
        }
        
        this.id = id;
    }

    public final boolean equals(Colony c1) {
        return c1 != null &&
                getName().equals(c1.getName()) &&
                getAuthor().equals(c1.getAuthor()) &&
                getAffiliation().equals(c1.getAffiliation());
    }

    /**
     * O(n) .. to improve the find.
     * @param mo
     * @return
     */
    public boolean createInstance(Cell mo) {
        if (moList.indexOf(mo) >= 0) return false;
        if (!create(mo.x, mo.y, mo.ene)) return false;
        moList.add(mo);
        
        return true;
    }

    protected abstract boolean create(int x, int y, float ene);

    /**
     * O(n)
     * @param mo
     * @return
     */
    public final boolean kill(Cell mo) {
        int indexMO = moList.indexOf(mo);

        if (indexMO >= 0) {
            kill(indexMO);
            return moList.remove(mo);
        }
        
        return false;
    }

    protected abstract void kill(int indexMO);

    protected abstract void end();

    protected abstract Movement move(int indexMO);

    protected abstract boolean mitosis(int indexMO);

    public final float getEnergy() {
        float f = 0.0f;

        for (Cell mo : moList) f += mo.ene;

        return f;
    }

    public final int getSize() {
        return moList.size();
    }

    public final Cell getMO(int index) {
        return moList.get(index);
    }

    public abstract String getAuthor();

    public abstract String getName();

    public abstract String getAffiliation();


    public String toString() {
        return getName();
    }

    public ArrayList<Cell> getMOs() {
        return moList;
    }

    public final boolean isDied() {
        return moList.isEmpty();
    }

    public void update(int indexMO, float ene, int x, int y) {
        moList.get(indexMO).ene = ene;
        moList.get(indexMO).x = x;
        moList.get(indexMO).y = y;

        updateInstance(indexMO, ene, x, y);
    }

    public abstract void updateInstance(int indexMO, float ene, int x, int y);

    public final void clear() {
        moList.clear();
        clearAll();
    }

    protected abstract void clearAll();

    public int getId() {
        return this.id;
    }

    @Override
    public boolean equals(Object o) {
        if(o == null) return false;
        if (this == o) return true;
        if (!(o instanceof Colony)) return false;

        Colony colony = (Colony) o;

        return id == colony.id || getName().equals(colony.getName());
    }

    @Override
    public int hashCode() {
        //int result = id;
//        result = 31 * result + getName().hashCode();
        return id; //unique id.
    }
}
