/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */
package lib;

import java.awt.Point;

public abstract class Microorganism {
    private static final long serialVersionUID = 0L;
    /**
     * Posicion absoluta del Microorganismo en
     * el entorno .
     */
    public Point pos;
    /**
     * energia actual del microorganismo.
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
}
