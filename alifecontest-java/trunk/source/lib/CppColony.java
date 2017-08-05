package lib; /**
 * @author Sergio Del Castillo
 * @email: sergio.jose.delcastillo@gmail.com
 */

import java.awt.*;
import java.lang.reflect.Field;
import java.util.Arrays;

public class CppColony extends Colony {


    public static boolean loadLibrary(String path) {
        try {
            String os = System.getProperty("os.name").toLowerCase();

            //add the current path to java library path
            try {
                addLibraryPath(path);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            //load the library according to the os
            if (os.contains("linux")) {
                System.loadLibrary("cppcolonies");
            } else
                System.loadLibrary("libcppcolonies");

            return true;
        } catch (java.lang.UnsatisfiedLinkError ex) {
            return false;
        }
    }

    /**
     * Adds the specified path to the java library path
     *
     * @param pathToAdd the path to add
     * @throws Exception
     */
    private static void addLibraryPath(String pathToAdd)
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
    public static void setLibraryPath(String path) throws Exception {
        System.setProperty("java.library.path", path);

        //set sys_paths to null
        final Field sysPathsField = ClassLoader.class.getDeclaredField("sys_paths");
        sysPathsField.setAccessible(true);
        sysPathsField.set(null, null);
    }

    private String author = "";
    private String name = "";
    private String affiliation = "";

    CppColony(int index, String colony_name) throws ClassNotFoundException {
        super(index, colony_name);

        if (!createColony(index, colony_name))
            throw new ClassNotFoundException("Can't find:" + colony_name);

        createMO(new Point(-1, -1), 0.0f);

        name = getName(index);
        author = getAuthor(index);
        affiliation = getAffiliation(index);

        kill(index, 0);
    }

    private native boolean createColony(int id, String name);

    @Override
    protected boolean createMO(Point pos, float ene) {
        return createMO(id, pos.x, pos.y, ene);
    }

    private native boolean createMO(int id, int x, int y, float ene);

    @Override
    protected void kill(int indexMO) {
        if (!kill(id, indexMO))
            System.out.println("error de eliminar MO");
    }

    private native boolean kill(int id, int indexMO);

    @Override
    public void end() {
        end(id);
    }

    private native void end(int id);

    @Override
    public Movement move(int indexMO) {
        return move(Petri.getInstance(), id, indexMO);
    }

    private native Movement move(Petri p, int id, int indexMO);

    // @Override
    public String getAuthor() {
        return author;
    }

    private native String getAuthor(int id);

    // @Override
    public String getName() {
        return name;
    }

    private native String getName(int id);

    // @Override
    public String getAffiliation() {
        return affiliation;
    }

    private native String getAffiliation(int id);

    public boolean mitosis(int indexMO) {
        return mitosis(id, indexMO);
    }

    private native boolean mitosis(int id, int indexMO);

    public void update(int indexMO, float ene, int x, int y) {
        update(id, indexMO, ene, x, y);
    }

    private native void update(int id, int indexMO, float ene, int x, int y);

    //@Override
    public void clearAll() {
        clearAll(id);
    }

    private native void clearAll(int id);
}
