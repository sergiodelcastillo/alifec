
bool Environment::addColony(string name, int id){
	CppColony<Microorganism> *mo = NULL;

	if(name == "Movx"){
	   mo = (CppColony < Microorganism > *) new CppColony< Movx >(id);
   }

	colonies.push_back(mo);

   return mo != NULL;
}

