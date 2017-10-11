/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

package controller.java;

import java.io.File;

public class CppColony extends Colony {
    private static boolean loaded = false;

    public static boolean loadLibrary(String path) {
        if (loaded) return true;
        try {
            String os = System.getProperty("os.name").toLowerCase();

            if (os.contains("linux")) {
                System.load(path + File.separator + "libcolonies.so");
            } else {
                System.load(path + File.separator + "libcolonies.dll");
            }
            loaded = true;
            return true;
        } catch (java.lang.UnsatisfiedLinkError ex) {
            //if the file does not exist
            return false;
        } catch (SecurityException e) {
            //if a security manager exists and its checkLink method doesn't allow loading of the specified dynamic library
            return false;
        } catch (NullPointerException e) {
            //if filename is null
            return false;
        }
    }

    private String author;
    private String name;
    private String affiliation;

    public CppColony(int id, String colonyName) throws ClassNotFoundException {
        super(id);

        if (!createColony(id, colonyName))
            throw new ClassNotFoundException("Can't find:" + colonyName);

        create(0, 0, 0.0f);

        name = getName(Petri.getInstance(), id);
        author = getAuthor(Petri.getInstance(), id);
        affiliation = getAffiliation(Petri.getInstance(), id);

        kill(id, 0);
    }

    @Override
    protected boolean create(int x, int y, float ene) {
        return createMO(id, x, y, ene);
    }

    @Override
    protected void kill(int indexMO) {
        if (!kill(id, indexMO))
            System.out.println("error de eliminar MO");
    }

    @Override
    public void end() {
        end(id);
    }

    @Override
    public Movement move(int indexMO) {
        return move(Petri.getInstance(), id, indexMO);
    }

    // @Override
    public String getAuthor() {
        return author;
    }

    // @Override
    public String getName() {
        return name;
    }

    // @Override
    public String getAffiliation() {
        return affiliation;
    }

    public boolean mitosis(int indexMO) {
        return mitosis(Petri.getInstance(), id, indexMO);
    }

    public void updateInstance(int indexMO, float ene, int x, int y) {
        update(id, indexMO, ene, x, y);
    }

    //@Override
    public void clearAll() {
        clearAll(id);
    }

    /*
   * Native methods
   * */
    private native boolean createColony(int id, String name);

    private native boolean createMO(int id, int x, int y, float ene);

    private native void end(int id);

    private native boolean kill(int id, int indexMO);

    private native String getName(Petri p, int id);

    private native String getAuthor(Petri p, int id);

    private native String getAffiliation(Petri p, int id);

    private native Movement move(Petri p, int id, int indexMO);

    private native boolean mitosis(Petri p, int id, int indexMO);

    private native void update(int id, int indexMO, float ene, int x, int y);

    private native void clearAll(int id);
}
