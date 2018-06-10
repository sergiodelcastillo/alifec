package alifec.core.simulation;

/**
 * Created by nacho on 19/11/17.
 */
public class TestMicroorganism extends Microorganism {

    private final String name;

    public TestMicroorganism(String name) {
        this.name = name;
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
        return name;
    }

    @Override
    public String getAuthor() {
        return name;
    }

    @Override
    public String getAffiliation() {
        return name;
    }
}
