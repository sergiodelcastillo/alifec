/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */
#ifndef CPPCOLONY_H
#define CPPCOLONY_H

#include <csignal>
#include <csetjmp>
#include <vector>
#include <iostream>

#include "Microorganism.h"

template < class MO > class CppColony{
	private:
		int id; // identificacion de la colonia
		string name;
		string author;
		string affiliation;
		vector<MO *> mos; // lista de microorganismos.
	
	public : // instance objects
		CppColony(int id):name(""), author(""), affiliation(""){
			this-> id = id;
		}
		virtual ~CppColony(){
			for(unsigned short i = 0; i < mos.size(); i++)
				delete mos[i];
		}
		
		/**
       * Elimina el microorganismo que esta en la posicion index.
		 * retorna true si se lo elimina, caso contrario retorna falso.
		 */ virtual bool kill(unsigned int index){

			if(index >= mos.size() || index < 0) 
				return false;

			mos.erase(mos.begin()+index);
			return true;
		}

		/**
		 * Elimina todas las instancias de los Microorganismos	
		 */ virtual void clear(){
			for(unsigned int i = 0; i < mos.size(); i++)
				delete mos[i];
			mos.clear();
		}

		/**
       * Crea un Microorganismo y lo agrega a la lista 
		 *	de microorganismos. La posicion del nuevo Microorganismo 
       * es pos y su energia es ene.
		 */ virtual void createMO(float ene, int x, int y){
			MO *mo = new MO();
			mo->update(ene, x, y);
			mos.push_back(mo);
			if(name == ""){
				affiliation = mo->getAffiliation();
				name = mo->getName();
				author = mo->getAuthor();
			}
		}
		
		/**
		 * Pide al microorganismo index de la lista de microorganismos 
       * que se mueva. El movimiento del microorganismo actualiza mov.
		*/ virtual bool move(unsigned int index, Movement &mov){
			try {
				if(index < mos.size()){
					mos[index]->move(mov);
				}
				return true;
			} catch (...) { /* errores de c++ */}

			return false;
		}

		/**
       * Actualiza la posicion (p) y la energia (f) del y el Microorganismo 
		 * que está en la posicion index 
       */ virtual bool update(unsigned int index, float ene, int x, int y){
			if(index < 0 ||index >= mos.size()){
				return false;
			}
			mos[index]->update(ene, x, y);
			return true;
		}
		/*
		 * Pide al microorganismo que esta en la posicion index
       * que indique si va a duplicarse.
       * True: significa que el Microorganismo se va a duplicar.
		 */ virtual bool mitosis(unsigned int index, bool &mit){		
			try {
				if(index < mos.size()){
					mit =  mos[index]->mitosis();
					return true;
				}
			} catch (...) { /*errores de c++ */ }
			
			return false;
		}
				
		/*
		 * Retorna la identificacion de la colonia.
		 * La identificacion es unica para todas las
       * colonias del entorno.	
		 */ virtual int getID() {
			return this->id;
		}
		
		/*
		 * Retorna el nombre de la colonia.
       * Debe ser unico en el entorno.
		 */ virtual string getName(){
			return name;
		}
		/*
		 * Retorna el autor de la colonia.
		 */ virtual string getAuthor(){
			return author;
		}
		/*
		 * Retorna la facultad a la que pertenece 
		 * el autor del codigo que realiza la 
       * implementacion de un microorganismo.   
		 */ virtual string getAffiliation(){
			return affiliation;
		}
};

						
#endif //end CPPCOLONY_H
