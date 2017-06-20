/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

package lib;

import java.awt.*;
import java.util.Vector;

/**
 * Es la encargada de administrar cada colonia de Microorganismos.
 */
public abstract class Colony {

    final int id;
    private final Vector<Cell> moList = new Vector<Cell>();
    final String path;

    String name = "";
    String author = "";
    String affiliation = "";

    Colony(int id, String path) {
        this.id = id;
        this.path = path;
    }

    @Override
    public final void finalize() throws Throwable {
        end();
        super.finalize();
    }

    public final boolean equals(Colony c1) {
        return c1 != null &&
                getName().equalsIgnoreCase(c1.getName()) &&
                getAuthor().equalsIgnoreCase(c1.getAuthor()) &&
                getAffiliation().equalsIgnoreCase(c1.getAffiliation());
    }

    public boolean createInstance(Cell mo) {
        if (!createMO(mo.pos, mo.ene)) return false;
        moList.addElement(mo);
        return true;
    }

    protected abstract boolean createMO(Point pos, float ene);

    protected final boolean kill() {
        for (int i = 0; i < moList.size(); i++)
            kill(i);

        moList.clear();
        return true;
    }

    public final boolean kill(Cell mo) {

        int indexMO = moList.indexOf(mo);
        if (indexMO >= 0)
            kill(indexMO);

        return moList.remove(mo);
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

    public final int size() {
        return moList.size();
    }

    public final Cell getMO(int index) {
        return moList.elementAt(index);
    }

    public abstract String getAuthor();

    public abstract String getName();

    public abstract String getAffiliation();


    public String toString() {
        return getName();
    }

    public Vector<Cell> getMOs() {
        return moList;
    }

    public final boolean isDied() {
        return moList.isEmpty();
    }

    public abstract void update(int indexMO, float ene, int x, int y);

    public final void clear() {
        moList.clear();
        clearAll();
    }

    protected abstract void clearAll();

}
