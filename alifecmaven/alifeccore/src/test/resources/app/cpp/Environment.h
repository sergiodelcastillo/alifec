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


#include "CppColony.h"
#include "includemos.h"

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
					vector<CppColony<Microorganism> *>::iterator it = colonies.begin();
					it += i;
					colonies.erase(it);
					return true;
				}
					
			return false;
		}
		
		CppColony <Microorganism> *getColony(int id){

			for(unsigned i = 0; i < colonies.size(); i++){
				CppColony <Microorganism> * col = colonies[i];
				if(col != NULL && col->getID() == id){					return col;
				}
			}		
			return 0;
		}

};
#include "Environment.cpp" 
#endif
