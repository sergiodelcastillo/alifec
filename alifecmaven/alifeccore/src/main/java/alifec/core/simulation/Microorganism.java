package alifec.core.simulation;


public abstract class Microorganism {
    /**
     * Absolute position of the microorganism
     */
    public int x;
    public int y;
    /**
     * Current energy of the microorganism
     */
    public float ene;


    /**
     * Move the microorganism to a relative position.
     * The microorganism can decide to move to a better position
     * or to attach the enemy.
     *
     * @param mov
     */
    public abstract void move(Movement mov);

    /**
     * This is a way that the microorganism can duplicate himself.
     *
     * @return true if the mitosis will be performed..
     */
    public abstract boolean mitosis();

    /**
     * The name of the microorganism.
     *
     * @return
     */
    public abstract String getName();

    /**
     * Author del code.
     *
     * @return
     */
    public abstract String getAuthor();

    /**
     * affiliation of author.
     *
     * @return
     */
    public abstract String getAffiliation();

    public final boolean isDead() {
        return ene <= 0.0f;
    }

    protected void update(float ene, int x, int y) {
        this.ene = ene;
        this.x = x;
        this.y = y;

    }
}
