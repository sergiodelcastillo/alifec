/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */
#ifndef DEFS_H
#define DEFS_H
/**
 * This class define the important constant to alifec.
 * A player can use them.
 */class Defs {
		public:
    /**
     * Petri dish radius.
     */ static const int RADIUS = 25;
    
    /**
     * Petri dish Diameter.
     */ static const int DIAMETER = 50; 
      
    /**
     *  Number of MOs within each colony at start de simulations
     */ static const int MO_INITIAL = 50;

    /**
     *Relative maximum of the nutrient distribution
     */ static const float MAX_NUTRI = 5000.0f;
    
    /**
     *Initial MO energy.
     */ static const float E_INITIAL = 1000.0f;
   
    /**
     * Less energy to live. 
     */ static const float LESS_LIVE = 5.0f;
    
     /**
      * Less energy to move. 
      */ static const float LESS_MOVE = 10.0f;
    
     /**
      * Max length of strings.
      */ static const int MAX_LENGTH = 13;
};

#endif