package alifec.core.simulation; /**
 * @author Sergio Del Castillo
 * @email: sergio.jose.delcastillo@gmail.com
 */

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CppColony extends Colony {

    static Logger logger = LogManager.getLogger(CppColony.class);

    public static boolean loadLibrary(String path) {
        try {
            String os = System.getProperty("os.name").toLowerCase();

            //add the current path to java library path
            try {
                addLibraryPath(path);
            } catch (Exception ex) {
                logger.warn(ex.getMessage(), ex);
            }

            //load the library according to the os
            if (os.contains("linux")) {
                System.loadLibrary("cppcolonies");
            } else
                System.loadLibrary("libcppcolonies");

            return true;
        } catch (UnsatisfiedLinkError ex) {
            logger.error(ex.getMessage(), ex);
            return false;
        }
    }

    private String author = "";
    private String name = "";
    private String affiliation = "";

    CppColony(int index, String colony_name) throws ClassNotFoundException {
        super(index, colony_name);

        if (!createColony(index, colony_name))
            throw new ClassNotFoundException("Can't find:" + colony_name);

        createMO(-1, -1, 0.0f);

        name = getName(index);
        author = getAuthor(index);
        affiliation = getAffiliation(index);

        kill(index, 0);
    }

    private native boolean createColony(int id, String name);

    @Override
    protected boolean createMO(int x, int y, float ene) {
        return createMO(id, x, y, ene);
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

    @Override
    public String getAuthor() {
        return author;
    }

    private native String getAuthor(int id);

    @Override
    public String getName() {
        return name;
    }

    private native String getName(int id);

    @Override
    public String getAffiliation() {
        return affiliation;
    }

    private native String getAffiliation(int id);

    @Override
    public boolean mitosis(int indexMO) {
        return mitosis(id, indexMO);
    }

    private native boolean mitosis(int id, int indexMO);

    @Override
    public void update(int indexMO, float ene, int x, int y) {
        update(id, indexMO, ene, x, y);
    }

    private native void update(int id, int indexMO, float ene, int x, int y);

    @Override
    public void clearAll() {
        clearAll(id);
    }

    private native void clearAll(int id);
}
