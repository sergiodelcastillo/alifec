package alifec.core.simulation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author: Sergio Del Castillo
 * @email sergio.jose.delcastillo@gmail.com
 * <p>
 * It is a generic class which represents the colony of microorganisms.
 */
public abstract class Colony {

    final int id;
    final String path;
    private final List<Cell> moList = new ArrayList<>();
    String name = "";
    String author = "";
    String affiliation = "";

    Colony(int id, String path) {
        this.id = id;
        this.path = path;
    }

    /**
     * Adds the specified path to the java library path
     *
     * @param pathToAdd the path to add
     * @throws Exception
     */
    protected static void addLibraryPath(String pathToAdd)
            throws NoSuchFieldException, IllegalAccessException {
        final Field usrPathsField = ClassLoader.class.getDeclaredField("usr_paths");
        usrPathsField.setAccessible(true);

        //get array of paths
        final String[] paths = (String[]) usrPathsField.get(null);

        //check if the path to add is already present
        for (String path : paths) {
            if (path.equals(pathToAdd)) {
                return;
            }
        }

        //add the new path
        final String[] newPaths = Arrays.copyOf(paths, paths.length + 1);
        newPaths[newPaths.length - 1] = pathToAdd;
        usrPathsField.set(null, newPaths);
    }

    public final boolean equals(Colony c1) {
        return Objects.nonNull(c1) &&
                getName().equalsIgnoreCase(c1.getName()) &&
                getAuthor().equalsIgnoreCase(c1.getAuthor()) &&
                getAffiliation().equalsIgnoreCase(c1.getAffiliation());
    }

    public boolean createInstance(Cell mo) {
        if (!createMO(mo.ene, mo.x, mo.y)) return false;
        moList.add(mo);
        return true;
    }

    protected abstract boolean createMO(float ene, int x, int y);

    public final boolean kill(Cell mo) {
        int indexMO = moList.indexOf(mo);
        if (indexMO >= 0)
            kill(indexMO);

        return moList.remove(mo);
    }

    protected abstract void kill(int indexMO);

    public abstract void end();

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
        return moList.get(index);
    }

    public abstract String getAuthor();

    public abstract String getName();

    public abstract String getAffiliation();

    public String toString() {
        return getName();
    }

    public List<Cell> getMOs() {
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

    /*
     * System.load(String filename); to specify the complete path to the native library you want to load, perhaps together
     * with System.mapLibraryName(String) to add the platform specific file ending (e.g. .dll or .so).
     * */
    protected abstract void clearAll();

    public int getId() {
        return this.id;
    }
}
