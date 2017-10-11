/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */
 
#ifndef MICROORGANISM_H
#define MICROORGANISM_H

#include <string>
using namespace std;

#include "Data.h"

class Microorganism{
	protected:
	   int id;
		float ene;    // energia del microorganismo.
		Position pos; // posicion absoluta del microorganismo dentro del petri.
	public:
		/**
		 * Invoca a un Microorganismo para que se mueva.	
		 * Movement mov: movimiento relativo a la posicion (pos) 
		 * del microorganismo.
  		 */ virtual void move(Movement &mov) {
			mov.dx = mov.dy = 0;
		};

		/**
		 * retorna true si el microorganismo va a realizar 
       * el proceso de mitosis(si se va a reproducir).
		 */ virtual bool mitosis(){ 
			return false;
		};
		
		/**
		 * Retorna el nombre de la colonia de microorganismos.
		 */ virtual string getName() const {
			return "Name: unknown";
		};
		
		/**
		 * Retorna el nombre del autor del codigo.
		 */ virtual string getAuthor() const {
			return "Author: unknown";
		};
		
		/**
		 * Retorna la universidad a la que pertenece el microorganismo.
		 */ virtual string getAffiliation() const {
			return "Affiliation: unknown";
		};
		
		/**
		 * Actualiza los datos del Microorganismo.
		 */const void update(int x, int y, float ene, int id) { 
			this->pos.x = x;
			this->pos.y = y;
			this->ene = ene;
			this->id = id;
		};
};


#endif
