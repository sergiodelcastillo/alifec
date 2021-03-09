package alifec.core.simulation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.file.Paths;

public class CppColony extends Colony {

    static Logger logger = LogManager.getLogger(CppColony.class);
    private String author;
    private String name;
    private String affiliation;

    CppColony(int index, String colony_name) throws ClassNotFoundException {
        super(index, colony_name);

        if (!createColony(index, colony_name))
            throw new ClassNotFoundException("Can't find:" + colony_name);

        createMO(0.0f, -1, -1);

        name = getName(index);
        author = getAuthor(index);
        affiliation = getAffiliation(index);

        kill(index, 0);
    }

    public static boolean loadLibrary(String path) {

        try {
            String absolutePath = Paths.get(path).toAbsolutePath().toString();
            String os = System.getProperty("os.name").toLowerCase();

            String extension = os.contains("linux") ? "so" : "dll";
            //Warning: the library file is not released and closed until the JVM is closed.
            System.load(absolutePath + File.separator + "libcppcolonies." + extension);
            return true;
        } catch (UnsatisfiedLinkError ex) {
            logger.error(ex.getMessage(), ex);
            return false;
        }
    }

    private native boolean createColony(int id, String name);

    @Override
    protected boolean createMO(float ene, int x, int y) {
        return createMO(id, ene, x, y);
    }

    private native boolean createMO(int id, float ene, int x, int y);

    @Override
    protected void kill(int indexMO) {
        if (!kill(id, indexMO))
            logger.error("failed to delete cpp mo: " + id + ", index=" + indexMO);
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
