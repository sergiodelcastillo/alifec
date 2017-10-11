/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */
package controller.java.rules;

import controller.java.Cell;
import controller.java.Environment;
import controller.java.Movement;

/**
 * The class defines the rules of the environment
 * Every child of this class defines a rule that the
 * environment was using to apply to the colony of
 * microorganisms when they move.
 *
 * @Warning: don't modify this interface
 */
public interface ColonyRule {
    
    /**
     * The abstract method to apply the rules.
     * @param env the environment of the competition
     * @param mo current MO
     * @param mov the results of call the method MO.move
     * @param mitosis the results of call the method MO.mitosis
     * @return true if the mo is dead.
     */
    public StatusRule apply(Environment env,  Cell mo, Movement mov, boolean mitosis);

}
