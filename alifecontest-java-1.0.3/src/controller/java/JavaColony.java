/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

package controller.java;

import data.java.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;


public class JavaColony extends Colony {
    private Constructor<Microorganism> constructor;
    private ArrayList<Microorganism> instances = new ArrayList<Microorganism>();

    private String name;
    private String author;
    private String affiliation;

    @SuppressWarnings("unchecked")
    JavaColony(int index, String path) throws ClassNotFoundException {
        super(index);

        constructor = (Constructor<Microorganism>) Class.forName(path).getConstructors()[0];

        // initialise the information of colony
        create(-1, -1, 0.0f);

        this.name = instances.get(0).getName();
        this.author = instances.get(0).getAuthor();
        this.affiliation = instances.get(0).getAffiliation();


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

        if (!checkIndex(indexMO)) {
            Log.save("Bad index: " + indexMO);
            return mov;
        }

        instances.get(indexMO).move(mov);

        return mov;
    }

    @Override
    protected boolean mitosis(int indexMO) {
        if (checkIndex(indexMO)) {
            return instances.get(indexMO).mitosis();
        }

        Log.save("Bad index: " + indexMO);
        return false;
    }

    private boolean checkIndex(int index) {
        return index >= 0 && index < instances.size();
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
    public boolean create(int x, int y, float ene) {
        Microorganism newMO;
        try {
            newMO = constructor.newInstance();
            newMO.pos = new Position(x, y);
            newMO.id = id;
            newMO.ene = ene;

            instances.add(newMO);
        } catch (InstantiationException ex) {
            Log.save("createMO error:", ex);
            return false;
        } catch (IllegalAccessException ex) {
            Log.save("createMO error:", ex);
            return false;
        } catch (IllegalArgumentException ex) {
            Log.save("createMO error:", ex);
            return false;
        } catch (InvocationTargetException ex) {
            Log.save("createMO error:", ex);
            return false;
        }
        return true;
    }

    @Override
    public void updateInstance(int indexMO, float ene, int x, int y) {
        instances.get(indexMO).update(x, y, ene, id);
    }

    @Override
    public void clearAll() {
        instances.clear();
    }
}
