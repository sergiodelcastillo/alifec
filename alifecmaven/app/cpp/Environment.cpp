
bool Environment::addColony(string name, int id){
	CppColony<Microorganism> *mo = NULL;

	if(name == "Movx"){
	   mo = (CppColony < Microorganism > *) new CppColony< Movx >(id);
   }
	if(name == "AdvancedMO"){
	   mo = (CppColony < Microorganism > *) new CppColony< AdvancedMO >(id);
   }

	colonies.push_back(mo);

   return mo != NULL;
}

