package alifec.core.simulation; /**
 * @author: Sergio Del Castillo
 * @email: sergio.jose.delcastillo@gmail.com
 */

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
     * Permite a un Microorganismo moverse a una posicion relativa
     * de su posicion actual (pos).
     *
     * @param mov
     */
    public abstract void move(Movement mov);

    /**
     * Permite a un microorganismo duplicarse.
     *
     * @return true si la mitosis se a concluido.
     */
    public abstract boolean mitosis();

    /**
     * Nombre de la colonia de Microorganismos.
     *
     * @return
     */
    public abstract String getName();

    /**
     * Autor del code.
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

    protected void setPosition(int x, int y) {
        this.x = x;
        this.y = y;

    }
}
