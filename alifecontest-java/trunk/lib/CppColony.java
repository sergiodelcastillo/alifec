/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

package lib;

import java.awt.*;

public class CppColony extends Colony {


    public static void main(String[] args) throws ClassNotFoundException {
        CppColony c = new CppColony(1, "Mimo");
        System.out.println(c.size());
        Movement m = c.move(10);
        System.out.println(m.dx + "-" + m.dy);
        System.out.println("name: " + c.getName());
        System.out.println("author: " + c.getAuthor());
        System.out.println("aff: " + c.getAffiliation());
        System.out.println("size : " + c.size());

        m = c.move(3);

        if (m != null) {
            System.out.println("dx: " + m.dx);
            System.out.println("dy: " + m.dy);
        }
    }

    public static boolean loadLibrary() {
        try {
            String os = System.getProperty("os.name").toLowerCase();

            if (os.contains("linux"))
                System.loadLibrary("cppcolonies");
            else
                System.loadLibrary("libcppcolonies");

            return true;
        } catch (java.lang.UnsatisfiedLinkError ex) {
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
