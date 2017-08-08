package alifec.core.simulation.MOs;

import alifec.core.simulation.Microorganism;
import alifec.core.simulation.Movement;

/**
 * Created by Sergio Del Castillo on 08/08/17.
 *
 * @email: sergio.jose.delcastillo@gmail.com
 */
public class NaiveMO extends Microorganism {

    public NaiveMO(){

    }
    @Override
    public void move(Movement mov) {

    }

    @Override
    public boolean mitosis() {
        return false;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getAuthor() {
        return null;
    }

    @Override
    public String getAffiliation() {
        return null;
    }
}
