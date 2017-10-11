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

enum Status{OK,  FAIL_INDEX, FAIL_MO };
        
template < class MO = Microorganism > class CppColony{

	private:
		int id; // colony id
		string name;
		string author;
		string affiliation;
		vector<MO > mos;
	
	private:
		bool check_index(int index);

	public : 
		static CppColony<Microorganism> *createInstance(string type, int id);
		CppColony(int id);
	
		/*
         * Remove the microorganism in the position index
		 * @return true if the microorganism was removed successfully. False in other case.
		 */ virtual bool kill(int index);

		/**
		 * delete all instance of MOs
		 */ virtual void clear();

		/**
       * Crea un Microorganismo y lo agrega a la lista 
		 *	de microorganismos. La posicion del nuevo Microorganismo 
       * es pos y su energia es ene.
		 */ virtual void add(int x, int y, float ene);

		/**
		 * Pide al microorganismo index de la lista de microorganismos 
       * que se mueva. El movimiento del microorganismo actualiza mov.
		*/ virtual Status move(int index, Movement &mov);


		/*
		 * Pide al microorganismo que esta en la posicion index
       * que indique si va a duplicarse.
       * True: significa que el Microorganismo se va a duplicar.
		 */ virtual Status mitosis( int index, bool &mit);

	  /**
       * Actualiza la posicion (p) y la energia (f) del y el Microorganismo 
		 * que está en la posicion index 
       */ virtual bool update( int index, int x, int y, float f);

		/*
		 * Retorna la identificacion de la colonia.
		 * La identificacion es unica para todas las
       * colonias del entorno.	
		 */ virtual int getId() ;
		
		/*
		 * Retorna el nombre de la colonia.
         * Debe ser unico en el entorno.
         */
	    virtual string getName();

		/*
		 * Retorna el autor de la colonia.
		 */ virtual string getAuthor();

		/*
		 * Retorna la facultad a la que pertenece 
		 * el autor del codigo que realiza la 
       * implementacion de un microorganismo.   
		 */ virtual string getAffiliation();

		int getSize();
};

template<class MO> CppColony<MO>::CppColony(int id){
	this-> id = id;
	
	MO mo;

	affiliation = mo.getAffiliation();
	name = mo.getName();
	author = mo.getAuthor();
}


template <class MO> bool CppColony<MO>::kill(int index){
	if(!check_index(index)) return false;
	
	return mos.erase(mos.begin()+index) != mos.end();
}

template <class MO> void CppColony<MO>::clear(){
//	for(typename vector<MO *>::iterator it = mos.begin(); it != mos.end(); it++){
	//	delete *it;
   // }
    
	mos.clear();
}

template <class MO> void CppColony<MO>::add(int x, int y, float ene){
	MO mo;// = new MO();
	mo.update(x, y, ene, id);
	mos.push_back(mo);	
}

template <class MO> Status CppColony<MO>::move(int index, Movement &mov){
	mov.dx = mov.dy = 0;

	if(!check_index(index)) return FAIL_INDEX;

    try {
    	mos.at(index).move(mov);
    	return OK;
	} catch (...) {
	}

	return FAIL_MO;
}


template <class MO> Status CppColony<MO>::mitosis( int index, bool &mit){		
	mit = false;
	
	if(!check_index(index)) return FAIL_INDEX;

	try {
		mit =  mos.at(index).mitosis();
      return OK;
	} catch (...) {
	}

	return FAIL_MO;
}
template <class MO> bool CppColony<MO>::update(int index, int x, int y, float f){
	if(!check_index(index)) return false;

    mos.at(index).update(x, y, f, id);

   return true;
}		

template <class MO> int CppColony<MO>::getId() {
	return this->id; 
}

template <class MO> string CppColony<MO>::getName(){
	return name;
}
template <class MO> string CppColony<MO>::getAuthor(){
	return author;
}

template <class MO> string CppColony<MO>::getAffiliation(){
	return affiliation;
}

template <class MO> int CppColony<MO>::getSize(){
	return this->mos.size();
}						

template <class MO> bool CppColony<MO>::check_index(int index){
	return index >= 0 && index < (int)this->mos.size();
}						

#include "CppColony.cpp"
#endif //end CPPCOLONY_H
