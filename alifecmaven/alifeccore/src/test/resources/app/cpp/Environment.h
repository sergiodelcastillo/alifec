/**
 * @author Yeyo
 * mail@: sergio.jose.delcastillo@gmail.com
 */

#ifndef ENVIRONMENT_H
#define ENVIRONMENT_H

#include <jni.h>
#include <cstring>
#include <iostream>
using namespace std;

#include "Petri.h"
#include "CppColony.h"

class Environment{
	private:
		vector< CppColony<Microorganism > *> colonies;
		
	public:
		Environment(){}
		~Environment(){
			for(unsigned short i = 0; i < colonies.size(); i++)
				delete colonies[i];
		}

		bool addColony(string name, int id);

		bool delColony(int id){
			for(unsigned short i = 0; i < colonies.size(); i++)
				if(colonies[i]->getID()== id){
					colonies[i]->clear();
					colonies.erase(colonies.begin()+i);
					return true;
				}
					
			return false;
		}
		
		CppColony <Microorganism> *getColony(int id){

			for(unsigned i = 0; i < colonies.size(); i++){
				CppColony <Microorganism> * col = colonies[i];
				if(col != NULL && col->getID() == id){
					return col;
				}
			}		
			return 0;
		}

};
#include "includemos.h"
#include "Environment.cpp" 
#endif
