/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

package controller.java.battle;

/**
 * This class contain the history of the battle.
 */
public class Battle extends AbstractBattle {

    public Battle() {
        super();
    }

    public Battle(String line) throws IllegalArgumentException {
        super(line);
    }


    /**
     * Set the first opponent information
     *
     * @param name   colony name of the opponent
     * @param energy energy of the opponent
     */
    public void setFirstOpponent(String name, float energy) {
        this.name1 = name;
        this.energy1 = energy;
    }

    /**
     * Set the second opponent information
     *
     * @param name   colony name of the opponent
     * @param energy energy of the opponent
     */
    public void setSecondOpponent(String name, float energy) {
        this.name2 = name;
        this.energy2 = energy;
    }

    /**
     * Get the winner name
     *
     * @return the name of the winner colony.
     */
    public String getWinnerName() {
        return (energy1 > energy2) ? name1 : name2;
    }

    /**
     * Get the winner energy
     *
     * @return the energy of the winner colony
     */
    public float getWinnerEnergy() {
        return (energy1 > energy2) ? energy1 : energy2;
    }

   
}
