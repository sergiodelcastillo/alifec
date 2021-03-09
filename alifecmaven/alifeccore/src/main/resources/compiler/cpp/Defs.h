#ifndef DEFS_H
#define DEFS_H
/**
 * @author Sergio Del castillo
 * mail@: sergio.jose.delcastillo@gmail.com
 *
 * This class define the important constant to alifec.
 * A player can use them.
 */class Defs {
		public:
    /**
     * Petri dish radius.
     */ static const int RADIUS;
    
    /**
     * Petri dish Diameter.
     */ static const int DIAMETER;
      
    /**
     * Number of MOs within each colony at start de simulation
     */ static const int MO_INITIAL;

    /**
     * Relative maximum of the nutrient distribution.
     */ static const float MAX_NUTRI;
    
    /**
     * Initial MO energy.
     */ static const float E_INITIAL;
   
    /**
     * Less energy to live. 
     */ static const float LESS_LIVE;
    
     /**
      * Less energy to move. 
      */ static const float LESS_MOVE;
    
     /**
      * Max length of strings.
      */ static const int MAX_LENGTH;

      /*
       * Eat percent
       */
      static const int EAT_PERCENT;
};


const int   Defs::RADIUS        = 25;
const int   Defs::DIAMETER      = 50;
const int   Defs::MO_INITIAL    = 50;
const float Defs::MAX_NUTRI     = 5000.0f;
const float Defs::E_INITIAL     = 1000.0f;
const float Defs::LESS_LIVE     = 5.0f;
const float Defs::LESS_MOVE     = 10.0f;
const int   Defs::MAX_LENGTH    = 13;
const int   Defs::EAT_PERCENT    = 0.01f;

#endif