package alifec.core.simulation; /**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */



import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Es la encargada de administrar cada colonia de Microorganismos.
 */
public abstract class Colony {

    final int id;
    private final List<Cell> moList = new ArrayList<>();
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
        if (!createMO(mo.x, mo.y, mo.ene)) return false;
        moList.add(mo);
        return true;
    }

    protected abstract boolean createMO(int x, int y, float ene);

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

    protected abstract void clearAll();

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

    /**
     * Sets the java library path to the specified path
     *
     * @param path the new library path
     * @throws Exception
     */
   /* protected static void setLibraryPath(String path) throws Exception {
        System.setProperty("java.library.path", path);

        //set sys_paths to null
        final Field sysPathsField = ClassLoader.class.getDeclaredField("sys_paths");
        sysPathsField.setAccessible(true);
        sysPathsField.set(null, null);
    }*/

    public static void addClassPath(String s) throws Exception {
        File f = new File(s);
        URI u = f.toURI();
        URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        Class<URLClassLoader> urlClass = URLClassLoader.class;
        Method method = urlClass.getDeclaredMethod("addURL", URL.class);
        method.setAccessible(true);
        method.invoke(urlClassLoader, u.toURL());
    }
}
