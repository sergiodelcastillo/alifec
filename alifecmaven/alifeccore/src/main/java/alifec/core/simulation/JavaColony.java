package alifec.core.simulation;

import org.apache.logging.log4j.LogManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;


/**
  * Manager of Microorganisms which are implemented in Java Language.
 */
public class JavaColony extends Colony {
    org.apache.logging.log4j.Logger logger = LogManager.getLogger(getClass());

    private Constructor<Microorganism> constructor;
    private List<Microorganism> instances = new ArrayList<>();

    @SuppressWarnings("unchecked")
    JavaColony(int index, String path) throws ClassNotFoundException {
        super(index, path.replace(".class", "").replace("/", "."));
        constructor = (Constructor<Microorganism>) Class.forName(super.path).getConstructors()[0];

        // initialise the information of colony
        createMO(0.0f,1, -1);
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
        instances.get(indexMO).move(mov);
        return mov;
    }

    @Override
    protected boolean mitosis(int indexMO) {
        return instances.get(indexMO).mitosis();
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
    public boolean createMO(float ene, int x, int y) {
        Microorganism newMO;
        try {
            newMO = constructor.newInstance();
            newMO.update(ene, x, y);

            // set Info!!
            if (name.equals("")) {
                this.name = newMO.getName();
                this.author = newMO.getAuthor();
                this.affiliation = newMO.getAffiliation();
            }
            instances.add(newMO);
        } catch (InstantiationException |
                InvocationTargetException |
                IllegalArgumentException |
                IllegalAccessException ex) {
            logger.error(ex.getMessage(), ex);
            return false;
        }
        return true;
    }

    @Override
    public void update(int indexMO, float ene, int x, int y) {
            instances.get(indexMO).update(ene, x, y);
    }

    @Override
    public void clearAll() {
        instances.clear();
    }


}
