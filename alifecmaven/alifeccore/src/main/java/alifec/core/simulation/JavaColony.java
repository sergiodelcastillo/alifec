package alifec.core.simulation; /**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

import java.awt.*;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Es la encargada de administrar cada colonia de Microorganismos codificados en en el lenguaje java.
 */
public class JavaColony extends Colony {
    private Constructor<Microorganism> constructor;
    private Vector<Microorganism> instances = new Vector<>();

    @SuppressWarnings("unchecked")
    JavaColony(int index, String path) throws ClassNotFoundException {
        super(index, path.replace(".class", "").replace("/", "."));
        constructor = (Constructor<Microorganism>) Class.forName(super.path).getConstructors()[0];

        // initialise the information of colony
        createMO(new Point(-1, -1), 0.0f);
        kill(0);
    }

    @Override
    protected void kill(int indexMO) {
        instances.remove(indexMO);
    }

    @Override
    protected void end() {
        instances = null;
    }

    @Override
    protected Movement move(int indexMO) {
        Movement mov = new Movement(0, 0);
        instances.elementAt(indexMO).move(mov);
        return mov;
    }

    @Override
    protected boolean mitosis(int indexMO) {
        return instances.elementAt(indexMO).mitosis();
    }

    @Override
    public String getAuthor() {
        return this.author;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getAffiliation() {
        return this.affiliation;
    }

    @Override
    public boolean createMO(Point pos, float ene) {
        Microorganism newMO;
        try {
            newMO = constructor.newInstance();
            newMO.pos = new Point();
            newMO.pos.x = pos.x;
            newMO.pos.y = pos.y;
            newMO.ene = ene;
            // set Info!!
            if (name.equals("")) {
                this.name = newMO.getName();
                this.author = newMO.getAuthor();
                this.affiliation = newMO.getAffiliation();
            }
            instances.addElement(newMO);
        } catch (InstantiationException |
                InvocationTargetException |
                IllegalArgumentException |
                IllegalAccessException ex) {
            Logger.getLogger(JavaColony.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    @Override
    public void update(int indexMO, float ene, int x, int y) {
        instances.elementAt(indexMO).ene = ene;
        instances.elementAt(indexMO).pos.x = x;
        instances.elementAt(indexMO).pos.y = y;
    }

    @Override
    public void clearAll() {
        instances.clear();
    }


}
