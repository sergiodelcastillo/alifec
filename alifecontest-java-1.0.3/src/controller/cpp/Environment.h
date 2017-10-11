/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

#ifndef ENVIRONMENT_H
#define ENVIRONMENT_H

//#include <jni.h>
#include <cstring>
#include <iostream>
using namespace std;

#include "CppColony.h"
#include "includemos.h"

class Environment{
	private:
		vector< CppColony<Microorganism > *> colonies;
		
	public:
		~Environment();

		bool addColony(string name, int id);

		bool delColony(int id);
		
		CppColony <Microorganism> *getColony(int id);

		int getSize();
};

	
Environment::~Environment(){
	for(vector<CppColony<Microorganism > *>::iterator it = colonies.begin(); it != colonies.end(); it++){
		delete *it;
	}
}

bool Environment::addColony(string name, int id){
	if(getColony(id) != NULL) return false;

	CppColony<Microorganism> *mo = CppColony<>::createInstance(name, id);

	if(mo == NULL) return false;
	
	colonies.push_back(mo);
	return true;
}

bool Environment::delColony(int id){
	for(vector<CppColony<Microorganism > *>::iterator it = colonies.begin(); it != colonies.end(); it++){
		if((*it)->getId() == id){
			(*it)->clear();
			colonies.erase(it);
			return true;
		}
	}
	return false;
}

CppColony <Microorganism> *Environment::getColony(int id){
	for(vector<CppColony<Microorganism > *>::iterator it = colonies.begin(); it != colonies.end(); it++){
		if((*it)->getId() == id){	
			return *it;
		}
	}
	return NULL;
}
int Environment::getSize(){
	return this->colonies.size();
}

#endif
