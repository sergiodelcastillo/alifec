#ifndef MICROORGANISM_H
#define MICROORGANISM_H

#include <string>
using namespace std;

#include "Data.h"

class Microorganism{
	protected:
		float ene;
		int x;
		int y;
	public:
	    Microorganism(){
	    }

	    virtual ~Microorganism(){
	    // please override the destructor in order to cleanup memory
	    }
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
		 */const void update(float ene, int x, int y) {
		 	this->ene = ene;
			this->x = x;
			this->y = y;
		};
};


#endif
