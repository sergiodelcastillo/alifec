/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */
package controller.java;

public abstract class Microorganism {
    /**
     * Absolute position of the Microorganism
     */
    protected Position pos;

    /**
     * the Microorganism energy
     */
    protected float ene;

    /**
     * Microorganism identifier
     */
    protected int id;

    /**
     * The microorganism with this method can indicate to environment class it want move to relative position
     *
     * @param mov
     */
    public abstract void move(Movement mov);

    /**
     * The Microorganism can duplicate with this method.
     *
     * @return true if it want duplicate.
     */
    public abstract boolean mitosis();

    /**
     * @return the colony name
     */
    public abstract String getName();

    /**
     * Get the name of the source code author
     *
     * @return a string with the name of the author
     */
    public abstract String getAuthor();

    /**
     * affiliation of author.
     *
     * @return
     */
    public abstract String getAffiliation();

    /**
     * .
     * Update the attributes
     *
     * @param x   the position x of the mo
     * @param y   the position y of the mo
     * @param ene the energy of the mo
     * @param id  the id of the mo colony
     */
    public final void update(int x, int y, float ene, int id) {
        this.pos.x = x;
        this.pos.y = y;
        this.ene = ene;
        this.id = id;
    }

}
